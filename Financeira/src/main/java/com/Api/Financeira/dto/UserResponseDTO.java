package com.Api.Financeira.dto;

import com.Api.Financeira.enums.Role;

public record UserResponseDTO(
        Long id,
        String nome,
        String email,
        Role role
) {
}
