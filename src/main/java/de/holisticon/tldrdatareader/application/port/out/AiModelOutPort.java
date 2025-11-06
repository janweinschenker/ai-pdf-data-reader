package de.holisticon.tldrdatareader.application.port.out;

import de.holisticon.tldrdatareader.domain.Part;
import de.holisticon.tldrdatareader.domain.PdfContainer;

import java.util.List;
import java.util.Optional;

public interface AiModelOutPort {
    Optional<String> extractStructuredData(String textFromJpeg);

    Optional<String> extractStructuredData(PdfContainer pdfContainer);

    Optional<String> extractStructuredData(String fileContent, String jsonSchema);
}
