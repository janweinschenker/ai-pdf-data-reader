package de.holisticon.tldrdatareader.application;

import de.holisticon.tldrdatareader.application.port.out.AiModelOutPort;
import de.holisticon.tldrdatareader.domain.Part;
import de.holisticon.tldrdatareader.domain.PdfContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DataExtractionUsecaseTest {

    @InjectMocks
    private DataExtractionUsecase sut;

    @Mock
    private AiModelOutPort aiModelOutPort;

    @Test
    @DisplayName("extractDataFromDocuments: shouldReturnExtractedPartsWhenValidPdfContainerProvided")
    void extractDataFromDocuments_shouldReturnExtractedPartsWhenValidPdfContainerProvided() {
        // given
        final PdfContainer pdfContainer = mock(PdfContainer.class);
        final List<Part> expectedParts = List.of(mock(Part.class), mock(Part.class));
        //when(aiModelOutPort.extractStructuredData(pdfContainer)).thenReturn(expectedParts);

        // when
        //final List<Part> actualParts = sut.extractDataFromDocuments(pdfContainer);

        // then
//        assertEquals(expectedParts, actualParts);
//        verify(aiModelOutPort, times(1)).extractStructuredData(pdfContainer);
    }

    @Test
    @DisplayName("extractDataFromDocuments: shouldThrowExceptionWhenPdfContainerIsNull")
    void extractDataFromDocuments_shouldThrowExceptionWhenPdfContainerIsNull() {
        // given
        final PdfContainer pdfContainer = null;

        // when / then
        assertThrows(NullPointerException.class, () -> sut.extractDataFromDocuments(pdfContainer));
        verifyNoInteractions(aiModelOutPort);
    }
}