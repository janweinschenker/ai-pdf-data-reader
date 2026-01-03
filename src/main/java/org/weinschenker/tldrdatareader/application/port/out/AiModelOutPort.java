package org.weinschenker.tldrdatareader.application.port.out;

import org.weinschenker.tldrdatareader.domain.PartList;
import org.weinschenker.tldrdatareader.domain.PdfContainer;

import java.util.Optional;

public interface AiModelOutPort {
    Optional<PartList> extractStructuredData(String textFromJpeg);

    Optional<PartList> extractStructuredData(String fileContent, String jsonSchema);
}
