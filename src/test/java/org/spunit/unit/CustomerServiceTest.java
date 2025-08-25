package org.spunit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spunit.entity.Customer;
import org.spunit.notification.Notifier;
import org.spunit.repository.CustomerRepository;
import org.spunit.service.CustomerServiceImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private Notifier notifier;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer existing;

    @BeforeEach
    void setUp() {
        existing = new Customer("Alok Pol", "alok@gmail.com");
        existing.setId(1L);
    }

    @Test
    void shouldReturnAllCustomersWhenRepositoryHasData() {
        List<Customer> customers = Arrays.asList(existing, new Customer("Rahul Verma", "rahul.verma@gmail.com"));
        when(repository.findAll()).thenReturn(customers);

        List<Customer> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void shouldNullIdAndSaveWhenCreatingCustomer() {
        Customer toCreate = new Customer("Neha Shah", "neha.shah@gmail.com");
        toCreate.setId(99L); // should be nulled by service
        Customer saved = new Customer("Neha Shah", "neha.shah@gmail.com");
        saved.setId(10L);
        when(repository.save(any(Customer.class))).thenReturn(saved);

        Customer result = service.create(toCreate);

        assertEquals(10L, result.getId());
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getId(), "Service should null the ID before save");
    }

    @Test
    void shouldUpdateFieldsAndSaveWhenCustomerExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Customer update = new Customer("Alok Updated", "alok.updated@gmail.com");
        Customer result = service.update(1L, update);
        assertEquals(1L, result.getId());
        assertEquals("Alok Updated", result.getName());
        assertEquals("alok.updated@gmail.com", result.getEmail());
        verify(repository).save(existing);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingCustomer() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        Customer update = new Customer("Temp Name", "temp@gmail.com");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.update(2L, update));
        assertEquals(404, ex.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteAndReturnTrueWhenCustomerExists() {
        when(repository.existsById(1L)).thenReturn(true);
        boolean deleted = service.delete(1L);
        assertTrue(deleted);
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingCustomer() {
        when(repository.existsById(99L)).thenReturn(false);
        boolean deleted = service.delete(99L);
        assertFalse(deleted);
        verify(repository, never()).deleteById(any());
    }
}
