package de.holisticon.tldrdatareader.adapter.out.aimodel;

import de.holisticon.tldrdatareader.application.port.out.AiModelOutPort;
import de.holisticon.tldrdatareader.domain.PartList;
import de.holisticon.tldrdatareader.domain.PdfContainer;
import de.holisticon.tldrdatareader.infrastructure.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AiModelOutAdaper implements AiModelOutPort {

    private final @NonNull OpenAiChatModel chatModel;
    private final @NonNull BeanOutputConverter<PartList> partListBeanOutputConverter;
    private final @NonNull ApplicationProperties applicationProperties;

    @Override
    public Optional<PartList> extractStructuredData(final String textFromJpeg) {
        final var promptTemplate = applicationProperties.getPromptTemplate();
        final var preparedPrompt = promptTemplate.replace("{{document}}", textFromJpeg);

        log.info("AiModelOutAdaper prepared prompt {}", preparedPrompt);

        try {
            final var chatResponse = chatModel.call(new Prompt(new UserMessage(preparedPrompt)));
            final Generation generation = chatResponse.getResult();

            final var usage = chatResponse.getMetadata().getUsage();
            log.info("AiModelOutAdaper usage {}", usage);

            return Optional.of(generation)
                    .map(Generation::getOutput)
                    .map(AbstractMessage::getText)
                    .map(partListBeanOutputConverter::convert);
        } catch (final NonTransientAiException e) {
            log.error("AiModelOutAdaper error calling chat model", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<PartList> extractStructuredData(final PdfContainer pdfContainer) {

        final List<Document> docs = getDocumentList(pdfContainer);

        final var promptTemplate = applicationProperties.getPromptTemplate();
        final var combined = new StringBuilder();

        docs.forEach(d -> combined.append(d.getFormattedContent()).append("\n"));

        final var preparedPrompt = promptTemplate.replace("{{document}}", combined.toString());

        log.info("AiModelOutAdaper prepared prompt {}", preparedPrompt);

        try {
            final var chatResponse = chatModel.call(new Prompt(new UserMessage(preparedPrompt))).getResult();

            return Optional.of(chatResponse)
                    .map(Generation::getOutput)
                    .map(AbstractMessage::getText)
                    .map(partListBeanOutputConverter::convert);
        } catch (final NonTransientAiException e) {
            log.error("AiModelOutAdaper error calling chat model", e);
            return Optional.empty();
        }
    }

    /**
     * Extracts a list of Document objects from the given PdfContainer.
     *
     * @param pdfContainer the PdfContainer containing the document list
     * @return the list of Document objects, or an empty list if the document list is not valid
     */
    List<Document> getDocumentList(final PdfContainer pdfContainer) {
        List<Document> result = new ArrayList<>();
        if (pdfContainer.getDocumentList() instanceof List<?> rawList) {
            for (Object item : rawList) {
                if (item instanceof Document doc) {
                    result.add(doc);
                } else {
                    log.warn("AiModelOutAdaper encountered non-Document item in document list: {}", item);
                }
            }
            return result;
        } else {
            log.warn("AiModelOutAdaper document list is not a List, returning empty list");
            return List.of();
        }
    }

    @Override
    public Optional<PartList> extractStructuredData(String fileContent, String jsonSchema) {
        final var promptTemplate = applicationProperties.getPromptTemplate();
        final var preparedPrompt = promptTemplate.replace("{{document}}", fileContent);
        log.info("AiModelOutAdaper prepared prompt {}", preparedPrompt);

        try {
            final OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .responseFormat(ResponseFormat.builder()
                            .type(ResponseFormat.Type.JSON_SCHEMA)
                            .jsonSchema(jsonSchema)
                            .build())
                    .build();
            final var chatResponse = chatModel.call(new Prompt(new UserMessage(preparedPrompt), options));
            final var result = chatResponse.getResult();
            final ChatResponseMetadata metadata = chatResponse.getMetadata();
            final Usage usage = metadata.getUsage();
            log.info("Prompt tokens: " + usage.getPromptTokens());
            log.info("Completion tokens: " + usage.getCompletionTokens());
            log.info("Total tokens: " + usage.getTotalTokens());

            return Optional.of(result)
                    .map(Generation::getOutput)
                    .map(AbstractMessage::getText)
                    .map(partListBeanOutputConverter::convert);
        } catch (final NonTransientAiException e) {
            log.error("AiModelOutAdaper error calling chat model", e);
            return Optional.empty();
        }
    }

}
