package org.spunit.testsupport;

import org.spunit.service.CustomerService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestBeans {

    @Bean
    @Primary
    public CustomerService inMemoryCustomerService() {
        return new InMemoryCustomerService();
    }
}
