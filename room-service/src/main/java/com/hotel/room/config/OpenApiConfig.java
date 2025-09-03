package com.hotel.room.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for Room Service
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI roomServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Development Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.hotel.com");
        prodServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("support@hotel.com");
        contact.setName("Hotel Support Team");
        contact.setUrl("https://www.hotel.com/support");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Hotel Room Management API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for managing hotel rooms and bookings")
                .termsOfService("https://www.hotel.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
