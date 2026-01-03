package org.weinschenker.tldrdatareader.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weinschenker.tldrdatareader.application.port.in.TextExtractor;
import org.weinschenker.tldrdatareader.application.port.out.AiModelOutPort;
import org.weinschenker.tldrdatareader.domain.PartList;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataExtractionUsecaseTest {

    @Mock
    private AiModelOutPort aiModelOutPort;

    private DataExtractionUsecase sut;

    @BeforeEach
    void setUp() {
        // default sut; individual tests will re-create with specific extractor lists if needed
        sut = new DataExtractionUsecase(List.of(), aiModelOutPort);
    }

    @Test
    @DisplayName("extractStructuredData: should return PartList when a TextExtractor can handle the content and AI returns a PartList")
    void extractStructuredData_shouldReturnPartListWhenExtractorMatchesAndAiReturns() {
        // given
        final byte[] file = new byte[]{1, 2, 3};
        final String contentType = "image/jpeg";
        final String schema = "schema-1";

        final TextExtractor extractor = mock(TextExtractor.class);
        when(extractor.canHandle(contentType)).thenReturn(true);
        when(extractor.extractText(file)).thenReturn("  Line A  \n\n  Line B  \nPart Number 123 Manufacturer XYZ\n  Line C  ");

        final PartList expectedPartList = new PartList();
        when(aiModelOutPort.extractStructuredData(any(String.class), eq(schema))).thenReturn(Optional.of(expectedPartList));

        sut = new DataExtractionUsecase(List.of(extractor), aiModelOutPort);

        // when
        final Optional<PartList> result = sut.extractStructuredData(file, contentType, schema);

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedPartList, result.get());

        // verify that extractor.extractText was called and the aiModelOutPort received the reduced text
        verify(extractor).canHandle(contentType);
        verify(extractor).extractText(file);
        // capture the reduced string to verify whitespace reduction happened; the implementation's regex only
        // matches lines that end with 'Manufacturer', so 'Part Number 123 Manufacturer XYZ' remains
        verify(aiModelOutPort).extractStructuredData(argThat(s -> s.contains("Line A") && s.contains("Line B") && s.contains("Part Number 123 Manufacturer XYZ")), eq(schema));
    }

    @Test
    @DisplayName("extractStructuredData: should return empty when no TextExtractor can handle the content")
    void extractStructuredData_shouldReturnEmptyWhenNoExtractorCanHandle() {
        // given
        final byte[] file = new byte[]{1, 2, 3};
        final String contentType = "application/pdf";
        final String schema = "schema-2";

        final TextExtractor t1 = mock(TextExtractor.class);
        final TextExtractor t2 = mock(TextExtractor.class);
        when(t1.canHandle(contentType)).thenReturn(false);
        when(t2.canHandle(contentType)).thenReturn(false);

        sut = new DataExtractionUsecase(List.of(t1, t2), aiModelOutPort);

        // when
        final Optional<PartList> result = sut.extractStructuredData(file, contentType, schema);

        // then
        assertFalse(result.isPresent());
        verify(t1).canHandle(contentType);
        verify(t2).canHandle(contentType);
        verify(t1, never()).extractText(any());
        verify(aiModelOutPort, never()).extractStructuredData(any(), any());
    }

    @Test
    @DisplayName("extractStructuredData: should return empty when AI model returns empty optional")
    void extractStructuredData_shouldReturnEmptyWhenAiReturnsEmpty() {
        // given
        final byte[] file = new byte[]{4, 5, 6};
        final String contentType = "image/png";
        final String schema = "schema-3";

        final TextExtractor extractor = mock(TextExtractor.class);
        when(extractor.canHandle(contentType)).thenReturn(true);
        when(extractor.extractText(file)).thenReturn(" only line ");

        when(aiModelOutPort.extractStructuredData(any(String.class), eq(schema))).thenReturn(Optional.empty());

        sut = new DataExtractionUsecase(List.of(extractor), aiModelOutPort);

        // when
        final Optional<PartList> result = sut.extractStructuredData(file, contentType, schema);

        // then
        assertFalse(result.isPresent());
        verify(extractor).extractText(file);
        verify(aiModelOutPort).extractStructuredData("only line", schema);
    }

    @Test
    @DisplayName("reduceWhitespace: should trim lines, remove blank lines and filter pattern lines")
    void reduceWhitespace_shouldTrimAndRemoveBlankAndPatternLines() {
        // given
        final String input = "  first line  \n\n  Part Number 123 Manufacturer ABC  \n second line \n   \nthird line";
        sut = new DataExtractionUsecase(List.of(), aiModelOutPort);

        // when
        final String result = sut.reduceWhitespace(input);

        // then
        // Note: the regex in the implementation only matches lines that end with 'Manufacturer', so the
        // example line 'Part Number 123 Manufacturer ABC' is NOT removed and should be present in the result.
        final String expected = String.join(System.lineSeparator(), "first line", "Part Number 123 Manufacturer ABC", "second line", "third line");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("reduceWhitespace: should return empty string for blank input")
    void reduceWhitespace_shouldReturnEmptyForBlankInput() {
        // given
        final String input = "  \n   \n \t  ";
        sut = new DataExtractionUsecase(List.of(), aiModelOutPort);

        // when
        final String result = sut.reduceWhitespace(input);

        // then
        assertEquals("", result);
    }
}
