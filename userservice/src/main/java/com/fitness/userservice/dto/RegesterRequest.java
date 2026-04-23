package com.fitness.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegesterRequest {

    @NotBlank(message = "Email is requried")
    @Email(message = "Invalid email format")
    private String email;

    private String keykloakid;

    @NotBlank(message = "Password is requried")
    @Size(min=6,message = "Password contain atleast 6 charecters")
    private String password;
    private String firstName;
    private String lastName;
}
