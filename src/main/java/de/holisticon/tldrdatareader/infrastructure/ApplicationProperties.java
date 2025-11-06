package de.holisticon.tldrdatareader.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "tldr")
@Component
@Data
public class ApplicationProperties {
    private String promptTemplate;
    private String resourceUrl;
    private String tessdataPath;
    private String tessdataLanguage;
}
