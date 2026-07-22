package com.Api.Financeira.service;

import com.Api.Financeira.dto.ResumoResponseDTO;
import com.Api.Financeira.dto.TransactionRequestDTO;
import com.Api.Financeira.dto.TransactionResponseDTO;
import com.Api.Financeira.enums.TransactionType;
import com.Api.Financeira.exceptions.AccessDeniedTransactionException;
import com.Api.Financeira.exceptions.TransactionNotFoundException;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.model.User;
import com.Api.Financeira.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @InjectMocks
    private TransactionService transactionService;

    private User usuarioLogado;

    @BeforeEach
    void setUp() {
        usuarioLogado = new User();
        usuarioLogado.setId(1L);
        usuarioLogado.setNome("Usuário Teste");
        usuarioLogado.setEmail("teste@teste.com");
    }

    @Test
    void deveCriarTransacaoComSucesso() {
        TransactionRequestDTO dto = new TransactionRequestDTO(
                "Conta de água",
                new BigDecimal("120.00"),
                TransactionType.DESPESAS
        );

        Transaction transactionSalva = new Transaction();
        transactionSalva.setId(1L);
        transactionSalva.setDescricao(dto.getDescricao());
        transactionSalva.setValor(dto.getValor());
        transactionSalva.setTipo(dto.getTipo());
        transactionSalva.setUser(usuarioLogado);

        when(authenticatedUser.getUser()).thenReturn(usuarioLogado);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionSalva);

        TransactionResponseDTO resultado = transactionService.createTransaction(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescricao()).isEqualTo(dto.getDescricao());
        assertThat(resultado.getValor()).isEqualTo(dto.getValor());
        assertThat(resultado.getTipo()).isEqualTo(dto.getTipo());

    }

    @Test
    void deveLancarExcecaodeTransacaoInexistente() {
        Long idInexistente = 999L;
        when(transactionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(idInexistente);
        });
    }

    @Test
    void deveLancarExcecaoAoDeletarTransacaoDeOutroUsuario() {
        User outroUsuario = new User();
        outroUsuario.setId(2L);

        Transaction transacaoOutroUsuario = new Transaction();
        transacaoOutroUsuario.setId(1L);
        transacaoOutroUsuario.setUser(outroUsuario);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transacaoOutroUsuario));
        when(authenticatedUser.getUserId()).thenReturn(1L);

        assertThrows(AccessDeniedTransactionException.class, () -> {
            transactionService.deleteTransaction(1L);
        });
    }

    @Test
    void deveDeletarTransacaoComSucesso() {
        Transaction transactionSalva = new Transaction();
        transactionSalva.setId(1L);
        transactionSalva.setUser(usuarioLogado);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transactionSalva));
        when(authenticatedUser.getUserId()).thenReturn(1L);

        transactionService.deleteTransaction(1L);

        verify(transactionRepository, times(1)).delete(transactionSalva);
    }

    @Test
    void deveLancarExcecaoAoAtualizarTrasacaoInexistente() {
        TransactionRequestDTO transactionSalva = new TransactionRequestDTO();

        Long idInexistente = 999L;
        when(transactionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.updateTransaction(idInexistente, transactionSalva);
        });
    }

    @Test
    void deveLancarExcecaoAoAtualizarTransacaoDeOutroUsuario() {
        User outroUsuario = new User();
        outroUsuario.setId(2L);

        Transaction transacaoOutroUsuario = new Transaction();
        transacaoOutroUsuario.setId(1L);
        transacaoOutroUsuario.setUser(outroUsuario);

        TransactionRequestDTO dto = new TransactionRequestDTO(
                "Conta de água",
                new BigDecimal("120.00"),
                TransactionType.DESPESAS
        );

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transacaoOutroUsuario));
        when(authenticatedUser.getUserId()).thenReturn(1L);

        assertThrows(AccessDeniedTransactionException.class, () -> {
            transactionService.updateTransaction(1L, dto);
        });
    }

    @Test
    void deveAtualizarTransacaoComSucesso() {
        Transaction transactionSalva = new Transaction();
        transactionSalva.setId(1L);
        transactionSalva.setDescricao("Conta antiga");
        transactionSalva.setValor(new BigDecimal("12.00"));
        transactionSalva.setTipo(TransactionType.RECEITA);
        transactionSalva.setUser(usuarioLogado);

        TransactionRequestDTO dto = new TransactionRequestDTO(
                "Conta de água",
                new BigDecimal("120.00"),
                TransactionType.DESPESAS
        );

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transactionSalva));
        when(authenticatedUser.getUserId()).thenReturn(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionSalva);

        TransactionResponseDTO resultado = transactionService.updateTransaction(1L, dto);

        assertThat(resultado.getDescricao()).isEqualTo(dto.getDescricao());
        assertThat(resultado.getValor()).isEqualTo(dto.getValor());
        assertThat(resultado.getTipo()).isEqualTo(dto.getTipo());
    }

    @Test
    void deveLancarExcecaoSeNaoEncontrarTransacao() {
        Long idInexistente = 999L;
        when(transactionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.findById(idInexistente);
        });
    }

    @Test
    void deveEncontrarTransacaoComSucesso() {
        Transaction transactionSalva = new Transaction();
        transactionSalva.setId(1L);
        transactionSalva.setDescricao("Conta antiga");
        transactionSalva.setValor(new BigDecimal("120.00"));
        transactionSalva.setTipo(TransactionType.DESPESAS);
        transactionSalva.setUser(usuarioLogado);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transactionSalva));
        when(authenticatedUser.getUserId()).thenReturn(1L);

        TransactionResponseDTO resultado = transactionService.findById(1L);

        assertThat(resultado.getDescricao()).isEqualTo(transactionSalva.getDescricao());
        assertThat(resultado.getValor()).isEqualByComparingTo(transactionSalva.getValor());
        assertThat(resultado.getTipo()).isEqualTo(transactionSalva.getTipo());
    }

    @Test
    void deveRetornarTodasTransacoesComSucesso() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setDescricao("Conta antiga");
        transaction1.setValor(new BigDecimal("120.00"));
        transaction1.setTipo(TransactionType.DESPESAS);
        transaction1.setUser(usuarioLogado);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setDescricao("Conta nova");
        transaction2.setValor(new BigDecimal("130.00"));
        transaction2.setTipo(TransactionType.RECEITA);
        transaction2.setUser(usuarioLogado);

        when(authenticatedUser.getUserId()).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of(transaction1, transaction2));

        List<TransactionResponseDTO> resultado = transactionService.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getDescricao()).isEqualTo(transaction1.getDescricao());
        assertThat(resultado.get(1).getDescricao()).isEqualTo(transaction2.getDescricao());
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemTransacoes() {

        when(authenticatedUser.getUserId()).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of());

        List<TransactionResponseDTO> resultado = transactionService.findAll();

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarTransacaoPeloTipoComSucesso() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setDescricao("Conta antiga");
        transaction1.setValor(new BigDecimal("120.00"));
        transaction1.setTipo(TransactionType.DESPESAS);
        transaction1.setUser(usuarioLogado);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setDescricao("Conta nova");
        transaction2.setValor(new BigDecimal("130.00"));
        transaction2.setTipo(TransactionType.DESPESAS);
        transaction2.setUser(usuarioLogado);

        when(transactionRepository.findByTipo(TransactionType.DESPESAS)).thenReturn(List.of(transaction1, transaction2));

        List<TransactionResponseDTO> resultado = transactionService.findByType(TransactionType.DESPESAS);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipo()).isEqualTo(transaction1.getTipo());
        assertThat(resultado.get(1).getTipo()).isEqualTo(transaction2.getTipo());
    }

    @Test
    void deveCalcularResumoSemFiltroDePeriodo() {
        Transaction transaction1 = new Transaction();
        transaction1.setTipo(TransactionType.DESPESAS);
        transaction1.setValor(new BigDecimal("1200.00"));

        Transaction transaction2 = new Transaction();
        transaction2.setTipo(TransactionType.RECEITA);
        transaction2.setValor(new BigDecimal("1300.00"));

        when(authenticatedUser.getUserId()).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of(transaction1, transaction2));

        ResumoResponseDTO resultado = transactionService.getResumo(null, null);

        assertThat(resultado.totalReceitas()).isEqualByComparingTo(transaction2.getValor());
        assertThat(resultado.totalDespesas()).isEqualByComparingTo(transaction1.getValor());
        assertThat(resultado.saldo()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveCalcularResumoComFiltroDePeriodo() {
        LocalDateTime inicio = LocalDateTime.of(2026, 7, 1, 0, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 7, 31, 23, 59, 59);

        Transaction transaction1 = new Transaction();
        transaction1.setTipo(TransactionType.RECEITA);
        transaction1.setValor(new BigDecimal("1200.00"));

        when(authenticatedUser.getUserId()).thenReturn(1L);
        when(transactionRepository.findByUserIdAndDataCriacaoBetween(1L, inicio, fim)).thenReturn(List.of(transaction1));

        ResumoResponseDTO resultado = transactionService.getResumo(inicio, fim);

        assertThat(resultado.totalReceitas()).isEqualByComparingTo(transaction1.getValor());
        assertThat(resultado.totalDespesas()).isEqualByComparingTo("0");
        assertThat(resultado.saldo()).isEqualByComparingTo("1200.00");
    }

    @Test
    void deveRetornarResumoZeradoQuandoNaoHaTransacoes() {
        when(authenticatedUser.getUserId()).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of());

        ResumoResponseDTO resultado = transactionService.getResumo(null, null);

        assertThat(resultado.totalReceitas()).isEqualByComparingTo("0");
        assertThat(resultado.totalDespesas()).isEqualByComparingTo("0");
        assertThat(resultado.saldo()).isEqualByComparingTo("0");
    }
}
