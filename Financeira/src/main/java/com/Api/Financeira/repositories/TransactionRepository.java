package com.Api.Financeira.repositories;

import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdandUser(Long id, User user);

    Page<Transaction> findByUser(User user, Pageable pageable);
}
