package org.spunit.controller;

import jakarta.validation.Valid;
import org.spunit.dto.CustomerCreateUpdateDto;
import org.spunit.service.CustomerApiService;
import org.spunit.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

    private final CustomerApiService apiService;

    public CustomerController(CustomerApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(HttpServletRequest request) {
        return apiService.getAll(request);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(@PageableDefault Pageable pageable, HttpServletRequest request) {
        return apiService.search(pageable, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return apiService.getById(id, request);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CustomerCreateUpdateDto payload, HttpServletRequest request) {
        return apiService.create(payload, request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerCreateUpdateDto payload, HttpServletRequest request) {
        return apiService.update(id, payload, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return apiService.delete(id);
    }
}
