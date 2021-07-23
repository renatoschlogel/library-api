package br.com.renatoschlogel.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Contact;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.renatoschlogel.libraryapi.api.resource"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Lybrary API")
				                   .description("Lybrary API")
				                   .version("1.0")
				                   .contact(contact())
				                   .build();
	}

	private Contact contact() {
		return new Contact("Renato Welinton Schlogel", "https://github.com/renatoschlogel", "renato.s@outlook.com");
	}
}
