package org.spunit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.spunit.controller.CustomerController;
import org.spunit.testsupport.TestBeans;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestBeans.class)
class StartToEndDeploymentFlowApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomerController customerController;

    @Test
    void applicationContextStartsSuccessfully() {
        assertNotNull(applicationContext, "ApplicationContext should be loaded");
        assertNotNull(customerController, "CustomerController bean should be created");
    }

}
