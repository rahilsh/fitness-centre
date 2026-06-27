package com.rsh.fitness_centre.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Fitness Centre API")
            .description("REST API for managing fitness centres, bookings, and user activities")
            .version("1.0.0")
            .contact(new Contact()
                .name("Fitness Centre Support")
                .email("support@fitnesscentre.com")
                .url("https://fitnesscentre.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
        .addServersItem(new Server()
            .url("http://localhost:8080")
            .description("Development Server"))
        .addServersItem(new Server()
            .url("https://api.fitnesscentre.com")
            .description("Production Server"));
  }
}
