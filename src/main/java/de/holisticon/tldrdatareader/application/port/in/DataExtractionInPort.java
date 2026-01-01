package de.holisticon.tldrdatareader.application.port.in;

import de.holisticon.tldrdatareader.domain.PartList;

import java.util.Optional;

public interface DataExtractionInPort {
    Optional<PartList> extractStructuredData(byte[] file, String contentType, String schema);
}
