package org.spunit.testsupport;

import org.spunit.entity.Customer;
import org.spunit.service.CustomerService;
import org.spunit.common.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test-only in-memory implementation of CustomerService.
 * Avoids any database usage by storing entities in a map.
 */
public class InMemoryCustomerService implements CustomerService {

    private final Map<Long, Customer> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<Customer> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Customer> getById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Customer create(Customer customer) {
        // simulate behavior: id must be null, then assigned
        Long id = seq.incrementAndGet();
        Customer copy = new Customer(customer.getName(), customer.getEmail());
        copy.setId(id);
        store.put(id, copy);
        return copy;
    }

    @Override
    public Customer update(Long id, Customer customer) {
        Customer existing = store.get(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.ERROR_CUSTOMER_NOT_FOUND);
        }
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        store.put(id, existing);
        return existing;
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }

    // Test-only helpers
    public void clear() {
        store.clear();
        seq.set(0);
    }

    public void resetWith(List<Customer> customers) {
        clear();
        for (Customer c : customers) {
            create(c);
        }
    }
}
