package org.weinschenker.tldrdatareader.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.weinschenker.tldrdatareader.application.port.in.DataExtractionInPort;
import org.weinschenker.tldrdatareader.application.port.in.TextExtractor;
import org.weinschenker.tldrdatareader.application.port.out.AiModelOutPort;
import org.weinschenker.tldrdatareader.domain.PartList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Use case for reading files and extracting structured data.
 */

@Component
@Slf4j
public class DataExtractionUsecase implements DataExtractionInPort {

    private final List<TextExtractor> textExtractorList;
    private final AiModelOutPort aiModelOutPort;

    public DataExtractionUsecase(final List<TextExtractor> textExtractorList, final AiModelOutPort aiModelOutPort) {
        this.textExtractorList = textExtractorList;
        this.aiModelOutPort = aiModelOutPort;
    }

    /**
     * Extract structured data from file content based on the provided JSON schema.
     *
     * @param file        The file content as byte array
     * @param contentType The content type of the file, e.g. application/pdf
     * @param schema      The requestes json schema
     * @return Optional containing the structured data as String if extraction was successful, otherwise an empty Optional
     */
    @Override
    public Optional<PartList> extractStructuredData(final byte[] file, final String contentType, final String schema) {
        return textExtractorList.stream()
                .filter(it -> it.canHandle(contentType))
                .findFirst()
                .map(it -> it.extractText(file))
                .map(this::reduceWhitespace)
                .flatMap(it -> aiModelOutPort.extractStructuredData(it, schema));
    }

    /**
     * <p>Reduces whitespace in the input string by trimming lines, removing blank lines,
     * and filtering out lines that match specific patterns.
     * </p>
     * <p>This aims to reduce token usage with the GenAI model.</p>
     *
     * @param input the input string to process
     * @return the processed string with reduced whitespace
     */
    String reduceWhitespace(final String input) {
        return input.lines()
                .map(String::trim)
                .filter(string -> !string.isBlank())
                .filter(string -> !string.matches("Part Number.*Manufacturer"))
                .collect(Collectors.joining(System.lineSeparator()));
    }

}
