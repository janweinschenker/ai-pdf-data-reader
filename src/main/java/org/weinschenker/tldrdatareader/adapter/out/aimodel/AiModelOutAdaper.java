package org.weinschenker.tldrdatareader.adapter.out.aimodel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;
import org.weinschenker.tldrdatareader.application.port.out.AiModelOutPort;
import org.weinschenker.tldrdatareader.domain.PartList;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@NullMarked
public class AiModelOutAdaper implements AiModelOutPort {

    private final OpenAiChatModel chatModel;
    private final BeanOutputConverter<PartList> partListBeanOutputConverter;
    private final ApplicationProperties applicationProperties;

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
