package de.holisticon.tldrdatareader.domain;

import lombok.Data;

@Data
public class Part {
    private final String partNumber;
    private final String manufacturer;
    private final String serialNumber;
}
