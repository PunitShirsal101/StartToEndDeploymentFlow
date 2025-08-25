package org.spunit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class CustomerCreateUpdateDto {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    public CustomerCreateUpdateDto(String name, String email) {
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
