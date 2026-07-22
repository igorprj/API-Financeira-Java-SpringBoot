package com.Api.Financeira.service;

import com.Api.Financeira.dto.UserRequestDTO;
import com.Api.Financeira.dto.UserResponseDTO;
import com.Api.Financeira.exceptions.UserNotFoundException;
import com.Api.Financeira.model.User;
import com.Api.Financeira.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void deveCriarUmUsuarioComSucesso() {
        UserRequestDTO dto = new UserRequestDTO(
                "igor",
                "test@gmail.com",
                "senha123"
        );

        User user = new User();
        user.setId(1L);
        user.setEmail(dto.email());
        user.setNome(dto.nome());
        user.setSenha(passwordEncoder.encode(dto.senha()));

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO resultado = userService.createUser(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo(dto.nome());
        assertThat(resultado.email()).isEqualTo(dto.email());
    }

    @Test
    void deveDeletarUmUsuarioComSucesso() {
        User  user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deveLancarExcecaoUsuarioNaoEncontrado() {
        Long idInexistente = 999L;

        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class , () -> {
            userService.deleteUser(idInexistente);
        });
    }

    @Test
    void acharUsuarioPorIdComSucesso() {
        User  user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setNome("igor");
        user.setSenha("senha123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO resultado = userService.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo(user.getNome());
        assertThat(resultado.email()).isEqualTo(user.getEmail());
    }

    @Test
    void deveLancarExcecaoUserNaoEncontrado() {
        Long idInexistente = 999L;

        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class , () -> {
            userService.deleteUser(idInexistente);
        });
    }

    @Test
    void deveAtualizarUmUsuarioComSucesso() {
        User   user = new User();
        user.setId(1L);
        user.setNome("igor");
        user.setEmail("igor@gmail.com");
        user.setSenha("senha123");

        UserRequestDTO dto = new UserRequestDTO(
            "test",
            "test@gmail.com",
            "senha123"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO resultado = userService.updateUser(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo(dto.nome());
        assertThat(resultado.email()).isEqualTo(dto.email());
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente () {
        UserRequestDTO dto = new UserRequestDTO(
                "igor",
                "igor@gmail.com",
                "senha123"
        );
        Long idInexistente = 999L;

        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class , () -> {
            userService.updateUser(idInexistente, dto);
        });
    }

    @Test
    void deveRetornarTodosUsuariosComSucesso() {
        User  user = new User();
        user.setId(1L);
        user.setNome("igor");
        user.setEmail("igor@gmail.com");
        user.setSenha("senha123");

        User user2 = new User();
        user2.setId(2L);
        user2.setNome("igor2");
        user2.setEmail("test@gmail.com");
        user2.setSenha("senha1234");

        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserResponseDTO> resultado = userService.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo(user.getNome());
        assertThat(resultado.get(0).email()).isEqualTo(user.getEmail());
        assertThat(resultado.get(1).nome()).isEqualTo(user2.getNome());
        assertThat(resultado.get(1).email()).isEqualTo(user2.getEmail());
    }

    @Test
    void deveRetornarListaVazia () {

        when(userRepository.findAll()).thenReturn(List.of());
        List<UserResponseDTO> resultado = userService.findAll();

        assertThat(resultado).isEmpty();
    }
}
