package org.spunit.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.spunit.dto.ApiResponse;
import org.spunit.dto.CustomerCreateUpdateDto;
import org.spunit.dto.CustomerDto;
import org.spunit.dto.PagedResponse;
import org.spunit.entity.Customer;
import org.spunit.mapper.CustomerMapper;
import org.spunit.common.Constants;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomerApiService {
    private final CustomerService service;
    private final CustomerMapper mapper;

    public CustomerApiService(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    public ResponseEntity<ApiResponse> getAll(HttpServletRequest request) {
        String requestId = (String) request.getAttribute(Constants.REQUEST_ID_ATTRIBUTE);
        List<CustomerDto> list = service.getAll().stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse.ok(list, requestId));
    }

    public ResponseEntity<ApiResponse> search(Pageable pageable, HttpServletRequest request) {
        // In-memory pagination + sorting on top of current service list
        List<Customer> all = new ArrayList<>(service.getAll());

        if (pageable.getSort().isSorted()) {
            Comparator<Customer> comparator = buildComparator(pageable.getSort());
            if (comparator != null) {
                all.sort(comparator);
            }
        }

        int from = Math.min((int) pageable.getOffset(), all.size());
        int to = Math.min(from + pageable.getPageSize(), all.size());
        List<CustomerDto> content = all.subList(from, to).stream().map(mapper::toDto).toList();
        int totalPages = (int) Math.ceil(all.size() / (double) pageable.getPageSize());
        String sort = pageable.getSort().toString();
        PagedResponse<CustomerDto> body = new PagedResponse<>(content, pageable.getPageNumber(), pageable.getPageSize(), all.size(), totalPages, sort);
        return ResponseEntity.ok(ApiResponse.ok(body, (String) request.getAttribute(Constants.REQUEST_ID_ATTRIBUTE)));
    }

    private Comparator<Customer> buildComparator(Sort sort) {
        Comparator<Customer> comparator = null;
        for (Sort.Order order : sort) {
            Comparator<Customer> c = comparatorForProperty(order.getProperty());
            if (c == null) {
                continue; // unsupported property; skip
            }
            if (order.isDescending()) {
                c = c.reversed();
            }
            comparator = (comparator == null) ? c : comparator.thenComparing(c);
        }
        return comparator;
    }

    private Comparator<Customer> comparatorForProperty(String property) {
        String prop = property == null ? "" : property.toLowerCase();
        return switch (prop) {
            case "name" -> Comparator.comparing(Customer::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "email" -> Comparator.comparing(Customer::getEmail, Comparator.nullsLast(String::compareToIgnoreCase));
            case "id" -> Comparator.comparing(Customer::getId, Comparator.nullsLast(Long::compareTo));
            default -> null;
        };
    }

    public ResponseEntity<ApiResponse> getById(Long id, HttpServletRequest request) {
        String requestId = (String) request.getAttribute(Constants.REQUEST_ID_ATTRIBUTE);
        return service.getById(id)
                .map(mapper::toDto)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto, requestId)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.ERROR_CUSTOMER_NOT_FOUND));
    }

    public ResponseEntity<ApiResponse> create(@Valid CustomerCreateUpdateDto payload, HttpServletRequest request) {
        Customer toSave = mapper.toEntity(payload);
        Customer saved = service.create(toSave);
        CustomerDto body = mapper.toDto(saved);
        ApiResponse envelope = ApiResponse.created(body, (String) request.getAttribute(Constants.REQUEST_ID_ATTRIBUTE));
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getId())).body(envelope);
    }

    public ResponseEntity<ApiResponse> update(Long id, @Valid CustomerCreateUpdateDto payload, HttpServletRequest request) {
        Customer temp = mapper.toEntity(payload);
        Customer saved = service.update(id, temp); // may throw ResponseStatusException -> handled globally
        return ResponseEntity.ok(ApiResponse.ok(mapper.toDto(saved), (String) request.getAttribute(Constants.REQUEST_ID_ATTRIBUTE)));
    }

    public ResponseEntity<Void> delete(Long id) {
        boolean deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.ERROR_CUSTOMER_NOT_FOUND);
    }
}
