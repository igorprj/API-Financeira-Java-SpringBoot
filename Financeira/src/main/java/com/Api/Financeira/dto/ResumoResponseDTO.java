package com.Api.Financeira.dto;

import java.math.BigDecimal;

public record ResumoResponseDTO(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldo
) {
}
