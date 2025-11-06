package de.holisticon.tldrdatareader.adapter.in.textextractor;

public interface TextExtractor {

    String extractText(byte[] fileContent);

    boolean canHandle(String contentType);


}
