package de.holisticon.tldrdatareader.adapter.out.aimodel;

import de.holisticon.tldrdatareader.domain.PartList;
import de.holisticon.tldrdatareader.domain.PdfContainer;
import de.holisticon.tldrdatareader.infrastructure.ApplicationProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.retry.NonTransientAiException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiModelOutAdaperTest {

    @InjectMocks
    private AiModelOutAdaper sut;

    @Mock
    private OpenAiChatModel chatModel;
    @Mock
    private BeanOutputConverter<PartList> partListBeanOutputConverter;
    @Mock
    private ApplicationProperties applicationProperties;

    @Test
    @DisplayName("extractStructuredData: shouldReturnEmptyListWhenNonTransientAiExceptionOccurs")
    void extractStructuredData_shouldReturnEmptyListWhenNonTransientAiExceptionOccurs() {
        // given
        PdfContainer pdfContainer = mock(PdfContainer.class);
        List<Document> documents = List.of(mock(Document.class));
        String promptTemplate = "Template with {{document}}";

        when(applicationProperties.getPromptTemplate()).thenReturn(promptTemplate);
        when(chatModel.call(any(Prompt.class))).thenThrow(new NonTransientAiException("AI error"));

        // when
        Optional<String> actualParts = sut.extractStructuredData(pdfContainer);

        // then
        assertTrue(actualParts.isEmpty());
        verify(chatModel).call(any(Prompt.class));
    }

    @Test
    @DisplayName("getDocumentList: shouldReturnDocumentListWhenValidPdfContainerProvided")
    void getDocumentList_shouldReturnDocumentListWhenValidPdfContainerProvided() {
        // given
        final PdfContainer pdfContainer = mock(PdfContainer.class);
        final Document document = mock(Document.class);
        final List<Object> rawList = List.of(document);

        when(pdfContainer.getDocumentList()).thenReturn(rawList);

        // when
        final List<Document> actualDocuments = sut.getDocumentList(pdfContainer);

        // then
        assertEquals(1, actualDocuments.size());
        assertEquals(document, actualDocuments.getFirst());
    }

    @Test
    @DisplayName("getDocumentList: shouldReturnEmptyListWhenInvalidPdfContainerProvided")
    void getDocumentList_shouldReturnEmptyListWhenInvalidPdfContainerProvided() {
        // given
        final PdfContainer pdfContainer = mock(PdfContainer.class);
        final List<Object> rawList = List.of("Invalid item");

        when(pdfContainer.getDocumentList()).thenReturn(rawList);

        // when
        final List<Document> actualDocuments = sut.getDocumentList(pdfContainer);

        // then
        assertTrue(actualDocuments.isEmpty());
    }

    @Test
    @DisplayName("extractStructuredData(String): shouldReturnTextWhenChatModelReturnsGeneration")
    void extractStructuredData_shouldReturnTextWhenChatModelReturnsGeneration() {
        // given
        final String template = "Template: {{document}}";
        final String textFromJpeg = "hello";
        final String expected = "{\"parts\": []}";

        final ChatResponse chatResponse = mock(ChatResponse.class);
        final Generation generation = mock(Generation.class);
        final AssistantMessage outputMessage = mock(AssistantMessage.class);
        final ChatResponseMetadata metadata = mock(ChatResponseMetadata.class);

        when(applicationProperties.getPromptTemplate()).thenReturn(template);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getMetadata()).thenReturn(metadata);
        when(metadata.getUsage()).thenReturn(null); // production code calls getMetadata().getUsage()
        when(chatResponse.getResult()).thenReturn(generation);
        // use doReturn for generic-return method to avoid Mockito+generics issues
        doReturn(outputMessage).when(generation).getOutput();
        when(outputMessage.getText()).thenReturn(expected);

        // when
        final Optional<String> actual = sut.extractStructuredData(textFromJpeg);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(applicationProperties).getPromptTemplate();
        verify(chatModel).call(any(Prompt.class));
        // ensure converter mock isn't unexpectedly used in this flow
        verifyNoInteractions(partListBeanOutputConverter);
    }

    @Test
    @DisplayName("extractStructuredData(String): shouldReturnEmptyWhenChatModelThrowsNonTransientAiException")
    void extractStructuredData_shouldReturnEmptyWhenChatModelThrowsNonTransientAiException() {
        // given
        final String template = "Template: {{document}}";
        final String textFromJpeg = "some text";

        when(applicationProperties.getPromptTemplate()).thenReturn(template);
        when(chatModel.call(any(Prompt.class))).thenThrow(new NonTransientAiException("AI error"));

        // when
        final Optional<String> actual = sut.extractStructuredData(textFromJpeg);

        // then
        assertTrue(actual.isEmpty());
        verify(chatModel).call(any(Prompt.class));
        verifyNoInteractions(partListBeanOutputConverter);
    }

    @Test
    @DisplayName("extractStructuredData(String): shouldThrowWhenInputIsNull")
    void extractStructuredData_shouldThrowWhenInputIsNull() {
        // given
        final String input = null;

        // when / then
        assertThrows(NullPointerException.class, () -> sut.extractStructuredData(input));
    }

    @Test
    @DisplayName("extractStructuredData(String): shouldReturnEmptyWhenGenerationOutputIsNull")
    void extractStructuredData_shouldReturnEmptyWhenGenerationOutputIsNull() {
        // given
        final String template = "Template: {{document}}";
        final String textFromJpeg = "text";

        final ChatResponse chatResponse = mock(ChatResponse.class);
        final Generation generation = mock(Generation.class);
        final ChatResponseMetadata metadata = mock(ChatResponseMetadata.class);

        when(applicationProperties.getPromptTemplate()).thenReturn(template);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getMetadata()).thenReturn(metadata);
        when(metadata.getUsage()).thenReturn(null);
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(null).when(generation).getOutput();

        // when
        final Optional<String> actual = sut.extractStructuredData(textFromJpeg);

        // then
        assertTrue(actual.isEmpty());
        verify(applicationProperties).getPromptTemplate();
        verify(chatModel).call(any(Prompt.class));
        verifyNoInteractions(partListBeanOutputConverter);
    }

    // New tests for extractStructuredData(String fileContent, String jsonSchema)

    @Test
    @DisplayName("extractStructuredData(fileContent,jsonSchema): shouldReturnTextWhenChatModelReturnsGeneration")
    void extractStructuredDataWithSchema_shouldReturnTextWhenChatModelReturnsGeneration() {
        // given
        final String template = "Template: {{document}}";
        final String fileContent = "file content";
        final String jsonSchema = "{\"type\":\"object\"}";
        final String expected = "{\"parts\": []}";

        final ChatResponse chatResponse = mock(ChatResponse.class);
        final Generation generation = mock(Generation.class);
        final AssistantMessage outputMessage = mock(AssistantMessage.class);

        when(applicationProperties.getPromptTemplate()).thenReturn(template);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(outputMessage).when(generation).getOutput();
        when(outputMessage.getText()).thenReturn(expected);

        // when
        final Optional<String> actual = sut.extractStructuredData(fileContent, jsonSchema);

        // then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        verify(applicationProperties).getPromptTemplate();
        verify(chatModel).call(any(Prompt.class));
        verifyNoInteractions(partListBeanOutputConverter);
    }

    @Test
    @DisplayName("extractStructuredData(fileContent,jsonSchema): shouldReturnEmptyWhenChatModelThrowsNonTransientAiException")
    void extractStructuredDataWithSchema_shouldReturnEmptyWhenChatModelThrowsNonTransientAiException() {
        // given
        final String template = "Template: {{document}}";
        final String fileContent = "file content";
        final String jsonSchema = "{\"type\":\"object\"}";

        when(applicationProperties.getPromptTemplate()).thenReturn(template);
        when(chatModel.call(any(Prompt.class))).thenThrow(new NonTransientAiException("AI error"));

        // when
        final Optional<String> actual = sut.extractStructuredData(fileContent, jsonSchema);

        // then
        assertTrue(actual.isEmpty());
        verify(chatModel).call(any(Prompt.class));
        verifyNoInteractions(partListBeanOutputConverter);
    }

    @Test
    @DisplayName("extractStructuredData(fileContent,jsonSchema): shouldReturnEmptyWhenGenerationOutputIsNull")
    void extractStructuredDataWithSchema_shouldReturnEmptyWhenGenerationOutputIsNull() {
        // given
        final String template = "Template: {{document}}";
        final String fileContent = "file content";
        final String jsonSchema = "{\"type\":\"object\"}";

        final ChatResponse chatResponse = mock(ChatResponse.class);
        final Generation generation = mock(Generation.class);

        when(applicationProperties.getPromptTemplate()).thenReturn(template);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(null).when(generation).getOutput();

        // when
        final Optional<String> actual = sut.extractStructuredData(fileContent, jsonSchema);

        // then
        assertTrue(actual.isEmpty());
        verify(applicationProperties).getPromptTemplate();
        verify(chatModel).call(any(Prompt.class));
        verifyNoInteractions(partListBeanOutputConverter);
    }

    @Test
    @DisplayName("extractStructuredData(fileContent,jsonSchema): shouldThrowWhenFileContentIsNull")
    void extractStructuredDataWithSchema_shouldThrowWhenFileContentIsNull() {
        // given
        final String fileContent = null;
        final String jsonSchema = "{\"type\":\"object\"}";

        // when / then
        assertThrows(NullPointerException.class, () -> sut.extractStructuredData(fileContent, jsonSchema));
    }

}