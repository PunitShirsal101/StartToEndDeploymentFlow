package org.spunit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerRedirectConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Backward-compatible redirect to Springdoc v2 UI location
        registry.addRedirectViewController("/swagger-ui.html", "/swagger-ui/index.html");
    }
}
