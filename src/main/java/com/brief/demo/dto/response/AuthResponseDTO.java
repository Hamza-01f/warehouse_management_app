package com.brief.demo.dto.response;

import com.brief.demo.enums.Role;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String message;
}
