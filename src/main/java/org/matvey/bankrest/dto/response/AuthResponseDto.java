package org.matvey.bankrest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.matvey.bankrest.entity.User;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private UserResponseDto user;
    private String accessToken;
}
