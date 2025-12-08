package com.enotes.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {


    @NotBlank
    @Size(min = 2, max =50, message = " first name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50, message = "last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must have at least 8 characters, 1 uppercase letter, 1 number, and 1 special character"
    )
    private String password;

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(
            regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$",
            message = "Invalid mobile number"
    )
    private String mobileNo;

}
