package org.matvey.bankrest.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles = new HashSet<>();
    private List<CardResponseDto> cards = new ArrayList<>();
}
