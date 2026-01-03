package org.weinschenker.tldrdatareader.adapter.in.rest;

import io.micrometer.observation.ObservationRegistry;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.weinschenker.tldrdatareader.adapter.in.rest.dto.gen.PartListDto;
import org.weinschenker.tldrdatareader.application.port.in.DataExtractionInPort;
import org.weinschenker.tldrdatareader.domain.PartList;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@NullMarked
class DocumentControllerTest {
    // given
    @Mock
    private DataExtractionInPort dataExtractionInPort;
    @Spy
    private ObservationRegistry observationRegistry = ObservationRegistry.create();
    @Mock
    private MultipartFile multipartFile;

    private DocumentController sut;

    @BeforeEach
    void setUp() {
        sut = new DocumentController(dataExtractionInPort, observationRegistry);
    }

    @Test
    @DisplayName("uploadDocument: should return OK with PartListDto when extraction succeeds")
    void uploadDocument_shouldReturnOkWhenExtractionSucceeds() throws Exception {
        // given
        final String schema = "test-schema";
        final byte[] fileBytes = new byte[]{1, 2, 3};
        final String contentType = "application/pdf";
        final PartList partList = new PartList();
        final PartListDto partListDto = new PartListDto(null); // Use builder to avoid deprecated constructor
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(dataExtractionInPort.extractStructuredData(fileBytes, contentType, schema)).thenReturn(Optional.of(partList));


        // when
        ResponseEntity<PartListDto> response = sut.uploadDocument(multipartFile, schema);

        // then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(partListDto, response.getBody());
    }

    @Test
    @DisplayName("uploadDocument: should return BAD_REQUEST when extraction returns empty")
    void uploadDocument_shouldReturnBadRequestWhenExtractionEmpty() throws Exception {
        // given
        final String schema = "test-schema";
        final byte[] fileBytes = new byte[]{1, 2, 3};
        final String contentType = "application/pdf";
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(dataExtractionInPort.extractStructuredData(fileBytes, contentType, schema)).thenReturn(Optional.empty());
        // when
        ResponseEntity<PartListDto> response = sut.uploadDocument(multipartFile, schema);
        // then
        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("uploadDocument: should propagate exception from extractStructuredData")
    void uploadDocument_shouldPropagateException() throws Exception {
        // given
        final String schema = "test-schema";
        final byte[] fileBytes = new byte[]{1, 2, 3};
        final String contentType = "application/pdf";
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(dataExtractionInPort.extractStructuredData(fileBytes, contentType, schema)).thenThrow(new RuntimeException("Extraction failed"));
        // when // then
        assertThrows(RuntimeException.class, () -> sut.uploadDocument(multipartFile, schema));
    }


}
