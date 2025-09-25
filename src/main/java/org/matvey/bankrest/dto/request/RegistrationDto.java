package org.matvey.bankrest.dto.request;

import lombok.Data;

@Data
public class RegistrationDto {
    private String name;
    private String email;
    private String password;
}
