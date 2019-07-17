package ar.edu.itba.cep.users_service.rest.config;

import com.bellotapps.webapps_commons.data_transfer.json.ApiObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration class to create a custom {@link ApiObjectMapper}.
 */
@Configuration
public class ApiObjectMapperConfig {

    /**
     * Creates a bean of an {@link ApiObjectMapper}.
     *
     * @return The {@link ApiObjectMapper} bean.
     */
    @Bean
    public ApiObjectMapper apiObjectMapper() {
        final var apiObjectMapper = new ApiObjectMapper();
        apiObjectMapper.registerModule(new JavaTimeModule());
        return apiObjectMapper;
    }
}
