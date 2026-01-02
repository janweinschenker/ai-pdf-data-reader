package org.weinschenker.tldrdatareader.application.port.in;

import org.weinschenker.tldrdatareader.domain.PartList;

import java.util.Optional;

public interface DataExtractionInPort {
    Optional<PartList> extractStructuredData(byte[] file, String contentType, String schema);
}
