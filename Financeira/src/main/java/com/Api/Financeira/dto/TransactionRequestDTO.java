package com.Api.Financeira.dto;

import com.Api.Financeira.enums.TransactionType;
import com.Api.Financeira.model.User;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotBlank(message = "A Descrição é obrigatória!")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2, message = "O Valor deve ter no máximo 2 casas decimais")
    private BigDecimal valor;

    @NotNull
    private TransactionType tipo;
}
