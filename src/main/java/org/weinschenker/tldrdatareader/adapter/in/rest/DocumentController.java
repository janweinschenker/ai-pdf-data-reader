package org.weinschenker.tldrdatareader.adapter.in.rest;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.weinschenker.tldrdatareader.adapter.in.rest.dto.gen.PartListDto;
import org.weinschenker.tldrdatareader.adapter.in.rest.gen.DocumentApiDelegate;
import org.weinschenker.tldrdatareader.adapter.in.rest.mapper.PartListMapper;
import org.weinschenker.tldrdatareader.application.port.in.DataExtractionInPort;
import org.weinschenker.tldrdatareader.domain.PartList;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestController
@NullMarked
public class DocumentController implements DocumentApiDelegate {

    private final DataExtractionInPort dataExtractionInPort;
    private final ObservationRegistry observationRegistry;

    @Override
    @SneakyThrows
    public ResponseEntity<PartListDto> uploadDocument(final MultipartFile file,
                                                      final String schema) {
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
