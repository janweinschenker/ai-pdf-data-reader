package de.holisticon.tldrdatareader.infrastructure.rest;

import de.holisticon.tldrdatareader.application.ExtraktStructuredDataUseCase;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestController
public class DocumentController implements de.holisticon.tldrdatareader.infrastructure.rest.DocumentApiDelegate {

    private final ExtraktStructuredDataUseCase extraktStructuredDataUseCase;

    @Override
    @SneakyThrows
    public ResponseEntity<String> uploadDocument(MultipartFile file,
                                                 String schema) {
        log.info("Received request for schema {}", schema);

        Optional<String> response = extraktStructuredDataUseCase.extractStructuredData(file.getBytes(), file.getContentType(), schema);


        return response
                .map(it -> ResponseEntity.ok().body(it))
                .orElse(ResponseEntity.badRequest().body("Could not extract structured data from file"));

    }
}
