package com.Api.Financeira.service;

import com.Api.Financeira.dto.TransactionRequestDTO;
import com.Api.Financeira.dto.TransactionResponseDTO;
import com.Api.Financeira.exceptions.TransactionNotFoundException;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponseDTO> findAll(){
        List<Transaction> transaction = transactionRepository.findAll();

        return transaction.stream()
                .map(this::toDTO)
                .toList();

    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO){
        Transaction transaction = new Transaction();

        updateEntity(transaction, transactionRequestDTO);

        Transaction saved = transactionRepository.save(transaction);

        return toDTO(saved);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Long id,  TransactionRequestDTO transactionRequestDTO){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada!"));

        updateEntity(transaction, transactionRequestDTO);

        Transaction updated = transactionRepository.save(transaction);

        return toDTO(updated);
    }

    @Transactional
    public void deleteTransaction(Long id){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada!"));
        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO findById(Long id){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada!"));

        return toDTO(transaction);
    }

    private TransactionResponseDTO toDTO(Transaction transaction){
        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.setId(transaction.getId());
        dto.setDescricao(transaction.getDescricao());
        dto.setValor(transaction.getValor());
        dto.setTipo(transaction.getTipo());
        dto.setDataCriacao(transaction.getDataCriacao());

        return dto;
    }

    private void updateEntity(Transaction transaction, TransactionRequestDTO transactionRequestDTO){
        transaction.setDescricao(transactionRequestDTO.getDescricao());
        transaction.setValor(transactionRequestDTO.getValor());
        transaction.setTipo(transactionRequestDTO.getTipo());
    }
}
