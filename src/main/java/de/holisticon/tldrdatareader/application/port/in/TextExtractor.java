package de.holisticon.tldrdatareader.application.port.in;

public interface TextExtractor {

    String extractText(byte[] fileContent);

    boolean canHandle(String contentType);


}
