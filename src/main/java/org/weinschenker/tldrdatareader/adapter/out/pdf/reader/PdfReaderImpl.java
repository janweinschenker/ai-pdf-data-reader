package org.weinschenker.tldrdatareader.adapter.out.pdf.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PdfReaderImpl implements PdfReader {

    /**
     * Configures and returns a ParagraphPdfDocumentReader for reading PDF documents.
     *
     * @param resourceUrl the URL of the PDF document to read
     * @return a configured ParagraphPdfDocumentReader instance
     */
    @Override
    public PagePdfDocumentReader getPdfReader(final String resourceUrl) {
        final var pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build();
        return new PagePdfDocumentReader(resourceUrl,
                pdfDocumentReaderConfig);
    }
}
