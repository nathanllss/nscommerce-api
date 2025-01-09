package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.Role;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.projections.UserDetailsProjection;
import com.nathanlucas.nscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }
        User user = customLoadUser(username, result);
        return user;
    }

    private User customLoadUser(String username, List<UserDetailsProjection> projections) {
        User user = new User();
        user.setEmail(username);
        user.setPassword(projections.get(0).getPassword());
        for (UserDetailsProjection projection : projections) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

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
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }
}