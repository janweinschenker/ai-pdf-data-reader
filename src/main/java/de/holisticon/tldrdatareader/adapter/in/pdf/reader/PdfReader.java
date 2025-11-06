package de.holisticon.tldrdatareader.adapter.in.pdf.reader;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;

public interface PdfReader {
    PagePdfDocumentReader getPdfReader(String resourceUrl);
}
