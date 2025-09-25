package org.matvey.bankrest.dto.response;

import lombok.Data;

@Data
public class AuthResponseDto {
    private UserResponseDto userResponseDto;
    private String token;
}
