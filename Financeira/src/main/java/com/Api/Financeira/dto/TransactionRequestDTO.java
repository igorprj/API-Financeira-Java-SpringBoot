package com.Api.Financeira.dto;

import com.Api.Financeira.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String descricao;

    @NotNull
    @Positive
    private BigDecimal valor;
    private TransactionType tipo;
}
