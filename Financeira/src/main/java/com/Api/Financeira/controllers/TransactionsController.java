package com.Api.Financeira.controllers;

import com.Api.Financeira.dto.ResumoResponseDTO;
import com.Api.Financeira.dto.TransactionRequestDTO;
import com.Api.Financeira.dto.TransactionResponseDTO;
import com.Api.Financeira.enums.TransactionType;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponseDTO> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate fim,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {

        if (inicio != null && fim != null) {
            return transactionService.findByPeriodo(inicio.atStartOfDay(), fim.atTime(23, 59, 59));
        }

        if (mes != null && ano != null) {
            YearMonth yearMonth = YearMonth.of(mes, ano);
            return transactionService.findByPeriodo(
                    yearMonth.atDay(1).atStartOfDay(),
                    yearMonth.atEndOfMonth().atTime(23, 59, 59)
            );
        }

        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponseDTO getTransactions(@PathVariable Long id){
        return transactionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO createTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO){
        return transactionService.createTransaction(transactionRequestDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponseDTO updateTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO,  @PathVariable Long id){
        return transactionService.updateTransaction(id, transactionRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransaction(id);
    }

    @GetMapping("/tipo/{tipo}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponseDTO> getTransactionsByType(@PathVariable TransactionType tipo){
        return transactionService.findByType(tipo);
    }

    @GetMapping("/resumo")
    @ResponseStatus(HttpStatus.OK)
    public ResumoResponseDTO getResumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim){

        LocalDateTime inicioDateTime = (inicio != null) ? inicio.atStartOfDay() : null;
        LocalDateTime fimDateTime = (fim != null) ? fim.atTime(23, 59, 59) : null;

        return transactionService.getResumo(inicioDateTime, fimDateTime);
    }
}
