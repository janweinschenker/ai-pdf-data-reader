package org.weinschenker.tldrdatareader.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    private String partNumber;
    private String manufacturer;
    private String serialNumber;
}
