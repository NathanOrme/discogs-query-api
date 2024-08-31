package org.discogs.query.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.discogs.query.deserializers.DiscogsTypesDeserializer;
import org.discogs.query.enums.DiscogsTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DiscogsTypes.class, new DiscogsTypesDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}