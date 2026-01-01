package de.holisticon.tldrdatareader.application.port.out;

import de.holisticon.tldrdatareader.domain.Part;
import de.holisticon.tldrdatareader.domain.PartList;
import de.holisticon.tldrdatareader.domain.PdfContainer;

import java.util.List;
import java.util.Optional;

public interface AiModelOutPort {
    Optional<PartList> extractStructuredData(String textFromJpeg);

    Optional<PartList> extractStructuredData(PdfContainer pdfContainer);

    Optional<PartList> extractStructuredData(String fileContent, String jsonSchema);
}
