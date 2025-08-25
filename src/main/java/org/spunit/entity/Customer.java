package org.spunit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = Customer.CUSTOMERS)
public class Customer extends BaseEntity {

    public static final String CUSTOMERS = "customers";

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be at most 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    @Column(nullable = false, unique = true)
    private String email;

    public Customer() {}

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
