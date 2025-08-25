package org.spunit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Start-To-End Customer API",
                version = "v1",
                description = "Simple Customer service demonstrating a clean CI/CD flow and OpenAPI docs.",
                contact = @Contact(name = "Project Maintainer", url = "https://github.com/PunitShirsal101"),
                license = @License(name = "Apache-2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
@Configuration
public class OpenApiConfig {
    // No code required; annotations provide metadata.
}
