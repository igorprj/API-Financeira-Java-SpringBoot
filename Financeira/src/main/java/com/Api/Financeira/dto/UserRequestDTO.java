package com.Api.Financeira.dto;

public record UserRequestDTO(
        String nome,
        String email,
        String senha
) {
}
