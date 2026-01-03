package org.weinschenker.tldrdatareader.adapter.out.pdf;

import net.sourceforge.tess4j.Tesseract;
import org.mockito.MockedConstruction;
import org.weinschenker.tldrdatareader.application.port.in.DataExtractionInPort;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PdfOutAdapterTest {

    @InjectMocks
    private PdfOutAdapter sut;

    @Mock
    private DataExtractionInPort dataExtractionInPort;
    @Mock
    private ApplicationProperties applicationProperties;

    @DisplayName("canHandle: shouldReturnTrueForPdfAndFalseForOthers")
    @Test
    void canHandle_shouldReturnTrueForPdfAndFalseForOthers() {
        // given
        final String pdfType = "application/pdf; charset=binary";
        final String plainType = "text/plain";

        // when
        final boolean canHandlePdf = sut.canHandle(pdfType);
        final boolean canHandlePlain = sut.canHandle(plainType);

        // then
        assertTrue(canHandlePdf);
        assertFalse(canHandlePlain);
    }

    @DisplayName("extractText: shouldReturnTextWhenValidPdfProvided")
    @Test
    void extractText_shouldReturnTextWhenValidPdfProvided() throws IOException {
        // given
        // load sample pdf from test resources
        final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("pdf/sample1.pdf");
        assertNotNull(is, "Test resource 'pdf/sample1.pdf' must be present");
        final byte[] pdfBytes = toByteArray(is);

        // when
        final String result = sut.extractText(pdfBytes);

        // then
        // result should not be null or empty
        assertNotNull(result);
        assertFalse(result.isBlank(), "extracted text should not be blank");
    }

    @DisplayName("extractText: shouldReturnTextWhenValidImagePdfProvided")
    @Test
    void extractText_shouldReturnTextWhenValidImagePdfProvided() throws IOException {
        // given
        // load sample pdf from test resources
        final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("pdf/sample2.pdf");
        assertNotNull(is, "Test resource 'pdf/sample2.pdf' must be present");
        final byte[] pdfBytes = toByteArray(is);
        when(applicationProperties.getTessdataPath()).thenReturn("/tmp/tessdata");
        when(applicationProperties.getTessdataLanguage()).thenReturn("eng");

        // when // then
        try (MockedConstruction<Tesseract> mocked = mockConstruction(Tesseract.class,
                (mock, context) -> {
                    when(mock.doOCR(any(BufferedImage.class))).thenReturn("recognized text");
                })) {
            // when
            final String result = sut.extractText(pdfBytes);

            // then
            assertEquals("recognized text", result);
            List<Tesseract> constructed = mocked.constructed();
            assertEquals(1, constructed.size());
            Tesseract used = constructed.getFirst();
            verify(used).setDatapath("/tmp/tessdata");
            verify(used).setLanguage("eng");
            verify(used).setOcrEngineMode(3);
            verify(used).setPageSegMode(3);
        }
    }

    @DisplayName("extractText: shouldThrowWhenInputIsNull")
    @Test
    void extractText_shouldThrowWhenInputIsNull() {
        // given
        final byte[] input = null;

        // when / then
        assertThrows(IllegalArgumentException.class, () -> sut.extractText(input));
    }

    // helper
    private static byte[] toByteArray(InputStream is) throws IOException {
        try (is) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toByteArray();
        }
    }

}