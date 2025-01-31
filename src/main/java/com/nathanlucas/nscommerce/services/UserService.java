package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.Role;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.repositories.UserRepository;
import com.nathanlucas.nscommerce.util.CustomUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserUtil customUserUtil;

    @Transactional
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already registered");
        }
        return saveClient(user);
    }

    private User saveClient(User client) {
        client.addRole(new Role(1L, "ROLE_CLIENT"));
        return userRepository.save(client);
    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {
        User user = authenticated();
        return new UserDTO(user);
    }

    protected User authenticated() {
        try {
            String username = customUserUtil.getLoggedUsername();

            return userRepository.findByEmail(username).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }
}