package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.Role;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already registered");
        }
        return saveClient(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
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
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(userEmail).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }
}