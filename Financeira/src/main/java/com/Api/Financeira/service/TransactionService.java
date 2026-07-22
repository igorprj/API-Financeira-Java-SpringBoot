package com.Api.Financeira.service;

import com.Api.Financeira.dto.ResumoResponseDTO;
import com.Api.Financeira.dto.TransactionRequestDTO;
import com.Api.Financeira.dto.TransactionResponseDTO;
import com.Api.Financeira.enums.TransactionType;
import com.Api.Financeira.exceptions.AccessDeniedTransactionException;
import com.Api.Financeira.exceptions.TransactionNotFoundException;
import com.Api.Financeira.exceptions.UserNotFoundException;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.model.User;
import com.Api.Financeira.repositories.TransactionRepository;
import com.Api.Financeira.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private final AuthenticatedUser authenticatedUser;

    public List<TransactionResponseDTO> findAll(){
        Long userId = authenticatedUser.getUserId();
        List<Transaction> transaction = transactionRepository.findByUserId(userId);

        return transaction.stream()
                .map(this::toDTO)
                .toList();

    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO){
        Transaction transaction = new Transaction();

        updateEntity(transaction, transactionRequestDTO);
        transaction.setUser(authenticatedUser.getUser());

        Transaction saved = transactionRepository.save(transaction);

        return toDTO(saved);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Long id,  TransactionRequestDTO transactionRequestDTO){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada!"));

        validarPropriedade(transaction);

        updateEntity(transaction, transactionRequestDTO);

        Transaction updated = transactionRepository.save(transaction);

        return toDTO(updated);
    }

    @Transactional
    public void deleteTransaction(Long id){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada!"));

        validarPropriedade(transaction);

        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO findById(Long id){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada!"));

        validarPropriedade(transaction);

        return toDTO(transaction);
    }

    public List<TransactionResponseDTO> findByType(TransactionType tipo){
        List<Transaction> transaction = transactionRepository.findByTipo(tipo);

        return transaction.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TransactionResponseDTO> findByPeriodo(LocalDateTime inicio, LocalDateTime fim){
        Long userId = authenticatedUser.getUserId();
        List<Transaction> transaction = transactionRepository.findByUserIdAndDataCriacaoBetween(userId, inicio, fim);

        return transaction.stream()
                .map(this::toDTO)
                .toList();
    }

    public ResumoResponseDTO getResumo(LocalDateTime inicio, LocalDateTime fim){
        Long userId = authenticatedUser.getUserId();

        List<Transaction> transactions = (inicio != null && fim != null)
                ? transactionRepository.findByUserIdAndDataCriacaoBetween(userId, inicio, fim)
                : transactionRepository.findByUserId(userId);

        BigDecimal totalReceitas = transactions.stream()
                .filter(t -> t.getTipo() == TransactionType.RECEITA)
                .map(Transaction::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transactions.stream()
                .filter(t -> t.getTipo() == TransactionType.DESPESAS)
                .map(Transaction::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new ResumoResponseDTO(totalReceitas, totalDespesas, saldo);
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

    private void validarPropriedade(Transaction transaction){
        Long userId = authenticatedUser.getUserId();
        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedTransactionException("Você não tem permissão para acessar essa transação!");
        }
    }
}
