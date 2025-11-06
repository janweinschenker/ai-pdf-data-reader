package de.holisticon.tldrdatareader.infrastructure;

import de.holisticon.tldrdatareader.domain.PartList;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfig {

    @Bean
    public BeanOutputConverter<PartList> partListBeanOutputConverter() {
        return new BeanOutputConverter<>(PartList.class);
    }

}
