package de.holisticon.tldrdatareader.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PdfContainer {

    private String resourceUrl;

    private Object documentList;
}
