package de.holisticon.tldrdatareader.application.port.in;

import de.holisticon.tldrdatareader.domain.Part;
import de.holisticon.tldrdatareader.domain.PdfContainer;
import lombok.NonNull;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Optional;

public interface DataExtractionInPort {
    Optional<String> extractDataFromDocuments(PdfContainer pdfContainer);

    Optional<String>  extractDataFromDocuments(@NonNull String textFromJpeg);
}
