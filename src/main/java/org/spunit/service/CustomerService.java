package org.spunit.service;

import org.spunit.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<Customer> getAll();

    Optional<Customer> getById(Long id);

    Customer create(Customer customer);

    Customer update(Long id, Customer customer);

    boolean delete(Long id);
}
