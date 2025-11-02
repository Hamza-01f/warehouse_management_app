package com.brief.demo.dto.request;

import com.brief.demo.enums.Role;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
