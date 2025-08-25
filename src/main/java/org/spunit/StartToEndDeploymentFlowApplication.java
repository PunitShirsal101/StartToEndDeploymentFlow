package org.spunit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class StartToEndDeploymentFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartToEndDeploymentFlowApplication.class, args);
    }

}
