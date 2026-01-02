package org.weinschenker.tldrdatareader.adapter.out.pdf;

import org.weinschenker.tldrdatareader.application.port.in.DataExtractionInPort;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

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