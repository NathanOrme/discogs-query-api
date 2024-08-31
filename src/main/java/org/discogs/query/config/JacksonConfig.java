package org.discogs.query.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.discogs.query.deserializers.DiscogsTypesDeserializer;
import org.discogs.query.enums.DiscogsTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for customizing Jackson's {@link ObjectMapper}.
 * <p>
 * This class provides a Spring configuration to customize the default behavior of Jackson's {@link ObjectMapper}.
 * Specifically, it registers a custom deserializer for the {@link DiscogsTypes} enum, which allows for
 * the proper deserialization of enum values from JSON strings.
 * <p>
 * The custom deserializer {@link DiscogsTypesDeserializer} is added to a {@link SimpleModule}, which is then
 * registered with the {@link ObjectMapper}. This setup ensures that any JSON processing involving {@link DiscogsTypes}
 * will use the specified deserializer to convert JSON strings into enum constants.
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures an {@link ObjectMapper} bean with custom deserialization settings.
     * <p>
     * This method initializes an {@link ObjectMapper} and configures it with a {@link SimpleModule} that includes
     * a custom deserializer for {@link DiscogsTypes}. The deserializer, {@link DiscogsTypesDeserializer}, handles
     * the conversion of JSON strings to {@link DiscogsTypes} enum values.
     *
     * @return a configured {@link ObjectMapper} instance with the custom deserializer registered
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DiscogsTypes.class, new DiscogsTypesDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
