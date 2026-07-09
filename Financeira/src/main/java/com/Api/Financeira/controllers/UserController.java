package com.Api.Financeira.controllers;

import com.Api.Financeira.dto.UserRequestDTO;
import com.Api.Financeira.dto.UserResponseDTO;
import com.Api.Financeira.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO){
        return userService.createUser(userRequestDTO);
    }
}
