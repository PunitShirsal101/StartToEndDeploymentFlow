package org.spunit.mapper;

import org.spunit.dto.CustomerCreateUpdateDto;
import org.spunit.dto.CustomerDto;
import org.spunit.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer entity) {
        if (entity == null) return null;
        return new CustomerDto(entity.getId(), entity.getName(), entity.getEmail());
    }

    public Customer toEntity(CustomerCreateUpdateDto dto) {
        if (dto == null) return null;
        Customer c = new Customer();
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        return c;
    }
}
