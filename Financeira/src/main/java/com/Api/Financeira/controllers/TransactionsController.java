package com.Api.Financeira.controllers;

import com.Api.Financeira.dto.TransactionRequestDTO;
import com.Api.Financeira.dto.TransactionResponseDTO;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponseDTO> getTransactions() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public TransactionResponseDTO getTransactions(@RequestParam Long id){
        return transactionService.findById(id);
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO){
        return transactionService.createTransaction(transactionRequestDTO);
    }

    @PutMapping("/{id}")
    public TransactionResponseDTO updateTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO,  @RequestParam Long id){
        return transactionService.updateTransaction(id, transactionRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@RequestParam Long id){
        transactionService.deleteTransaction(id);
    }
}
