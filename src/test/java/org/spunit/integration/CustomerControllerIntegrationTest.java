package org.spunit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.spunit.entity.Customer;
import org.spunit.repository.CustomerRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository repository;

    private String baseUrl = "http://localhost:";

    @BeforeEach
    void setUp() {
        baseUrl = baseUrl.concat(port + "/api/customers");
        // Ensure clean, predictable data before each test
        repository.deleteAll();
        repository.saveAll(Arrays.asList(
                new Customer("Alok Pol", "alok@gmail.com"),
                new Customer("Asha Patil", "asha@gmail.com"),
                new Customer("Vikram Rao", "vikram@gmail.com")
        ));
    }

    @Test
    void shouldReturnSeededDataWhenGettingAllCustomers() {
        ResponseEntity<java.util.Map> response = restTemplate.getForEntity(baseUrl, java.util.Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Object data = response.getBody().get("data");
        assertNotNull(data);
        @SuppressWarnings("unchecked")
        List<java.util.Map<String, Object>> customers = (List<java.util.Map<String, Object>>) data;
        assertTrue(customers.size() >= 3);
        assertTrue(customers.stream().anyMatch(c -> "alok@gmail.com".equals(c.get("email"))));
    }

    @Test
    void shouldCreateCustomerAndReturnLocationHeader() {
        Customer newCustomer = new Customer("Ravi Kumar", "ravi.kumar@gmail.com");
        ResponseEntity<java.util.Map> createResp = restTemplate.postForEntity(baseUrl, newCustomer, java.util.Map.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        assertNotNull(createResp.getBody());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) createResp.getBody().get("data");
        assertNotNull(data.get("id"));
        assertEquals("Ravi Kumar", data.get("name"));
        assertEquals("ravi.kumar@gmail.com", data.get("email"));
        String location = createResp.getHeaders().getFirst(HttpHeaders.LOCATION);
        assertNotNull(location);
        assertTrue(location.startsWith("/api/customers/"));
    }

    @Test
    void shouldReturnCustomerWhenGetById() {
        // First create a customer to get its ID
        Customer newCustomer = new Customer("Sita Sharma", "sita.sharma@gmail.com");
        ResponseEntity<java.util.Map> createResp = restTemplate.postForEntity(baseUrl, newCustomer, java.util.Map.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> createdData = (java.util.Map<String, Object>) createResp.getBody().get("data");
        Long id = ((Number) createdData.get("id")).longValue();

        ResponseEntity<java.util.Map> getResp = restTemplate.getForEntity(baseUrl + "/" + id, java.util.Map.class);
        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) getResp.getBody().get("data");
        assertEquals("Sita Sharma", data.get("name"));
        assertEquals("sita.sharma@gmail.com", data.get("email"));
    }

    @Test
    void shouldReturnUpdatedCustomerWhenUpdating() {
        Customer newCustomer = new Customer("Ajay Mehta", "ajay.mehta@gmail.com");
        ResponseEntity<java.util.Map> createResp = restTemplate.postForEntity(baseUrl, newCustomer, java.util.Map.class);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> createdData = (java.util.Map<String, Object>) createResp.getBody().get("data");
        Long id = ((Number) createdData.get("id")).longValue();

        Customer update = new Customer("Vijay Mehta", "vijay.mehta@gmail.com");
        HttpEntity<Customer> requestEntity = new HttpEntity<>(update);
        ResponseEntity<java.util.Map> putResp = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.PUT, requestEntity, java.util.Map.class);
        assertEquals(HttpStatus.OK, putResp.getStatusCode());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) putResp.getBody().get("data");
        assertEquals("Vijay Mehta", data.get("name"));
        assertEquals("vijay.mehta@gmail.com", data.get("email"));
    }

    @Test
    void shouldDeleteCustomerAndSubsequentGetReturnsNotFound() {
        Customer newCustomer = new Customer("Geeta Patel", "geeta.patel@gmail.com");
        ResponseEntity<Customer> createResp = restTemplate.postForEntity(baseUrl, newCustomer, Customer.class);
        Long id = createResp.getBody().getId();

        ResponseEntity<Void> deleteResp = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, deleteResp.getStatusCode());

        ResponseEntity<Customer> getAfterDelete = restTemplate.getForEntity(baseUrl + "/" + id, Customer.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getAfterDelete.getStatusCode());
    }
}
