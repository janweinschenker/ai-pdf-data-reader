package org.weinschenker.tldrdatareader.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class PartList {

    private List<Part> parts;
}
