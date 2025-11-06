package de.holisticon.tldrdatareader.application;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import de.holisticon.tldrdatareader.adapter.in.textextractor.TextExtractor;
import de.holisticon.tldrdatareader.application.port.out.AiModelOutPort;
import io.restassured.module.jsv.JsonSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExtraktStructuredDataUseCase {

    private final List<TextExtractor> textExtractorList;
    private final AiModelOutPort aiModelOutPort;

    /**
     * Extract structured data from file content based on the provided JSON schema.
     *
     * @param file        The file content as byte array
     * @param contentType The content type of the file, e.g. application/pdf
     * @param schema      The requestes json schema
     * @return Optional containing the structured data as String if extraction was successful, otherwise an empty Optional
     */
    public Optional<String> extractStructuredData(byte[] file, String contentType, String schema) {

        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(SchemaVersion.DRAFTV4).freeze()).freeze();


        return textExtractorList.stream()
                .filter(it -> it.canHandle(contentType))
                .findFirst()
                .map(it -> it.extractText(file))
                .flatMap(it -> aiModelOutPort.extractStructuredData(it, schema))
                .filter(it -> JsonSchemaValidator.matchesJsonSchema(schema).using(jsonSchemaFactory).matches(it));

    }

}
