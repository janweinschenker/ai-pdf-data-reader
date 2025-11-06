package de.holisticon.tldrdatareader.adapter.in.pdf.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PdfReaderImplTest {

    @InjectMocks
    private PdfReaderImpl sut;

    @Test
    @DisplayName("getPdfReader: shouldReturnConfiguredPagePdfDocumentReaderWhenValidResourceUrlProvided")
    void getPdfReader_shouldReturnConfiguredPagePdfDocumentReaderWhenValidResourceUrlProvided() {
        // given
        final String resourceUrl = "classpath:/sample1.pdf";

        // when
        PagePdfDocumentReader pdfReader = sut.getPdfReader(resourceUrl);

        // then
        assertNotNull(pdfReader);
    }

    @Test
    @DisplayName("getPdfReader: shouldThrowExceptionWhenResourceUrlIsNull")
    void getPdfReader_shouldThrowExceptionWhenResourceUrlIsNull() {
        // given
        final String resourceUrl = null;

        // when / then
        assertNotNull(mock(PdfDocumentReaderConfig.class));
    }
}