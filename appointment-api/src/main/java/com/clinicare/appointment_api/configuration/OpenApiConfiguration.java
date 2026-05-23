package com.clinicare.appointment_api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Appointment")
                        .version("1.0.0")
                        .description("Documentação da API para agendamento de consultas médicas.")
                        .contact(new Contact()
                                .name("Suporte")
                                .email("suporte@exemplo.com")));
    }
}
