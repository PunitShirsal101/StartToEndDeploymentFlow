package org.spunit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spunit.entity.Customer;
import org.spunit.notification.Notifier;
import org.spunit.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository repository;
    private final Notifier notifier;

    public CustomerServiceImpl(CustomerRepository repository, Notifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "customers", key = "#id")
    public Optional<Customer> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "customers", allEntries = true)
    public Customer create(Customer customer) {
        customer.setId(null);
        Customer saved = repository.save(customer);
        notifier.onCustomerCreated(saved);
        log.info("Created customer id={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "customers", key = "#id")
    public Customer update(Long id, Customer customer) {
        Customer updated = repository.findById(id)
                .map(existing -> {
                    existing.setName(customer.getName());
                    existing.setEmail(customer.getEmail());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, org.spunit.common.Constants.ERROR_CUSTOMER_NOT_FOUND));
        notifier.onCustomerUpdated(updated);
        log.info("Updated customer id={} email={}", updated.getId(), updated.getEmail());
        return updated;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "customers", key = "#id")
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Deleted customer id={}", id);
            return true;
        }
        return false;
    }
}
