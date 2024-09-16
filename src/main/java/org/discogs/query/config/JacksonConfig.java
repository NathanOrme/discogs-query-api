package org.discogs.query.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.discogs.query.config.deserializers.DiscogsCountryDeserializer;
import org.discogs.query.config.deserializers.DiscogsFormatsDeserializer;
import org.discogs.query.config.deserializers.DiscogsTypesDeserializer;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsFormats;
import org.discogs.query.model.enums.DiscogsTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for customizing Jackson's {@link ObjectMapper}.
 *
 * <p>This class provides a Spring configuration to
 * customize the default behavior of Jackson's
 * {@link ObjectMapper}. Specifically, it registers
 * custom deserializers for the {@link DiscogsTypes}
 * and {@link DiscogsFormats} enums, allowing proper
 * deserialization of enum values from JSON strings.
 *
 * <p>The custom deserializers, {@link DiscogsTypesDeserializer}
 * and {@link DiscogsFormatsDeserializer},
 * are added to a {@link SimpleModule}, which is then
 * registered with the {@link ObjectMapper}.
 * This setup ensures that any JSON processing
 * involving {@link DiscogsTypes} and {@link DiscogsFormats}
 * will use the specified deserializers to convert JSON strings into enum
 * constants.
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures an {@link ObjectMapper} bean with custom
     * deserialization settings.
     *
     * <p>This method initializes an {@link ObjectMapper} and
     * configures it with a {@link SimpleModule} that includes
     * custom deserializers for {@link DiscogsTypes} and {@link DiscogsFormats}.
     * The deserializers handle the conversion
     * of JSON strings to their respective enum values.
     *
     * @return a configured {@link ObjectMapper} instance with the custom
     * deserializers registered
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DiscogsTypes.class, new DiscogsTypesDeserializer());
        module.addDeserializer(DiscogsFormats.class, new DiscogsFormatsDeserializer());
        module.addDeserializer(DiscogCountries.class, new DiscogsCountryDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
