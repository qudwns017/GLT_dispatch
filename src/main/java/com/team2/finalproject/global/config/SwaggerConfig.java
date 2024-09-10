package com.team2.finalproject.global.config;

import com.team2.finalproject.global.swagger.customizer.CustomOperationCustomizer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Value("${server.url}")
    String requestUrl;

    private final CustomOperationCustomizer customOperationCustomizer;

    @Bean
    public OpenAPI openAPI() {

        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        Server server = new Server()
            .url(requestUrl)
            .description("GLT KOREA TMS API Server");

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .addServersItem(server)
                .components(components);
    }


    @Bean
    public GroupedOpenApi errorOpenApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/**")
                .addOperationCustomizer(customOperationCustomizer)
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("GLT KOREA TMS")
                .version("1.0.0");
    }
}
