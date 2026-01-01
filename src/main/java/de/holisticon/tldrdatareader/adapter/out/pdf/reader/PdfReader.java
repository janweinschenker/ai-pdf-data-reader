package de.holisticon.tldrdatareader.adapter.out.pdf.reader;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;

public interface PdfReader {
    PagePdfDocumentReader getPdfReader(String resourceUrl);
}
