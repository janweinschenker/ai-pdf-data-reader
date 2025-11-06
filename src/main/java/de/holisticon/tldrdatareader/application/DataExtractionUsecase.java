package de.holisticon.tldrdatareader.application;

import de.holisticon.tldrdatareader.application.port.in.DataExtractionInPort;
import de.holisticon.tldrdatareader.application.port.out.AiModelOutPort;
import de.holisticon.tldrdatareader.domain.Part;
import de.holisticon.tldrdatareader.domain.PdfContainer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataExtractionUsecase implements DataExtractionInPort {

    private final @NonNull AiModelOutPort aiModelOutPort;

    @Override
    public Optional<String> extractDataFromDocuments(final @NonNull PdfContainer pdfContainer) {
        return aiModelOutPort.extractStructuredData(pdfContainer);
    }

    @Override
    public Optional<String> extractDataFromDocuments(final @NonNull String textFromJpeg) {
        return aiModelOutPort.extractStructuredData(textFromJpeg);
    }
}
