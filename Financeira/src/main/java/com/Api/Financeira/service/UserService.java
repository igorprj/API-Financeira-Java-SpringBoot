package com.Api.Financeira.service;

import com.Api.Financeira.dto.TransactionResponseDTO;
import com.Api.Financeira.dto.UserRequestDTO;
import com.Api.Financeira.dto.UserResponseDTO;
import com.Api.Financeira.exceptions.UserNotFoundException;
import com.Api.Financeira.model.Transaction;
import com.Api.Financeira.model.User;
import com.Api.Financeira.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> findAll() {
        List<User> user = userRepository.findAll();

        return user.stream()
                .map(this::toDTO)
                .toList();
    }

    public UserResponseDTO findById(Long id){
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado!"));

        return toDTO(user);
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        User user = new User();

        updateEntity(user, userRequestDTO);

        User saved = userRepository.save(user);

        return toDTO(saved);
    }

    private UserResponseDTO toDTO(User user){
        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getEmail()
        );

        return dto;
    }

    private void updateEntity(User user, UserRequestDTO userRequestDTO){
        user.setNome(userRequestDTO.nome());
        user.setEmail(userRequestDTO.email());
        user.setSenha(passwordEncoder.encode(userRequestDTO.senha()));
    }
}
