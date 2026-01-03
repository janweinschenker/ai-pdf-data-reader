package org.weinschenker.tldrdatareader.adapter.out.pdf.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

}