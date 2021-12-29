package inz.gameadvisor.restapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

import static java.util.Collections.singletonList;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .produces(new HashSet<>(
                        Arrays.asList("application/json")))
                .securitySchemes(singletonList(createSchema()))
                .securityContexts(singletonList(createContext()));
    }

    private SecurityContext createContext() {
        return SecurityContext.builder()
                .securityReferences(createRef())
                .operationSelector(operationContext -> true)
                .build();
    }

    private List<SecurityReference> createRef(){
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return singletonList(new SecurityReference("apiKey",authorizationScopes));
    }

    private SecurityScheme createSchema(){
        return new ApiKey("apiKey","Authorization","header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GameAdvisor REST API Documentation")
                .description("Work in Progress")
                //.description("Dokumentacja API dla projektu inżynierskiego \"System rekomendacji gier wykorzystujący informacje na temat parametrów urządzeń użytkownika\"")
                .version("0.1")
                .build();
    }
}
