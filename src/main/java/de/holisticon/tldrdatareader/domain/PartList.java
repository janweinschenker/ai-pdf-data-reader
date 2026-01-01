package de.holisticon.tldrdatareader.domain;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@NonNull
public class PartList {

    private List<Part> parts;
}
