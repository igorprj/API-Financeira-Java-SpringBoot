package com.Api.Financeira.dto;

import com.Api.Financeira.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO{

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private TransactionType tipo;
    private LocalDateTime dataCriacao;
}
