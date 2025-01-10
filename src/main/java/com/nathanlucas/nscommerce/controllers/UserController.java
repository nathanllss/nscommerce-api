package com.nathanlucas.nscommerce.controllers;

import com.nathanlucas.nscommerce.Services.UserService;
import com.nathanlucas.nscommerce.config.security.TokenService;
import com.nathanlucas.nscommerce.dtos.auth.LoginDTO;
import com.nathanlucas.nscommerce.dtos.auth.LoginResponseDTO;
import com.nathanlucas.nscommerce.dtos.auth.RegisterDTO;
import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.User;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

//TODO: Adicionar experiationDate ao token retornado ao usuario

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService service;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> findMe() {
        return ResponseEntity.ok().body(service.getMe());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (this.service.existsByEmail(data.getEmail())) return ResponseEntity.badRequest().build();

        RegisterDTO registerDTO = setEncodePassword(data);
        User newUser = modelMapper.map(registerDTO, User.class);

        this.service.saveUser(newUser);

        return ResponseEntity.ok().build();
    }



    private RegisterDTO setEncodePassword(RegisterDTO data) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.getPassword());
        data.setPassword(encryptedPassword);
        return data;
    }
}
