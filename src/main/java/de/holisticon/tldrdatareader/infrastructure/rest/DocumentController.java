package de.holisticon.tldrdatareader.infrastructure.rest;

import de.holisticon.tldrdatareader.application.port.in.DataExtractionInPort;
import de.holisticon.tldrdatareader.domain.PartList;
import de.holisticon.tldrdatareader.infrastructure.rest.mapper.PartListMapper;
import de.holisticon.tldrdatareader.infrastructure.rest.dto.PartListDto;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
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
public class DocumentController implements DocumentApiDelegate {

    private final DataExtractionInPort dataExtractionInPort;
    private final ObservationRegistry observationRegistry;

    @Override
    @SneakyThrows
    public ResponseEntity<PartListDto> uploadDocument(MultipartFile file,
                                                      String schema) {
        log.info("Received request for schema {}", schema);
        final Optional<PartList> response = dataExtractionInPort.extractStructuredData(file.getBytes(), file.getContentType(), schema);
        return Observation
                .createNotStarted("ai.call", observationRegistry)
                .observe(() -> createResponse(response));

    }

    private ResponseEntity<PartListDto> createResponse(final Optional<PartList> response) {
        return response
                .map(PartListMapper.INSTANCE::toDto)
                .map(it -> ResponseEntity.ok().body(it))
                .orElse(ResponseEntity.badRequest().body(null));
    }
}
