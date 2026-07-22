package com.Api.Financeira.repositories;

import com.Api.Financeira.enums.TransactionType;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTipo(TransactionType tipo);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndDataCriacaoBetween(Long userId, LocalDateTime inicio, LocalDateTime fim);
}
