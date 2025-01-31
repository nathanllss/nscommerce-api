package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.projections.UserDetailsProjection;
import com.nathanlucas.nscommerce.repositories.UserRepository;
import com.nathanlucas.nscommerce.services.exceptions.ForbiddenException;
import com.nathanlucas.nscommerce.utils.UserDetailsFactory;
import com.nathanlucas.nscommerce.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    private String existingUsername, nonExistingUsername;
    private User client, admin;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp() {
        existingUsername = "maria@gmail.com";
        nonExistingUsername = "user@gmail.com";
        client = UserFactory.createClient();
        admin = UserFactory.createAdmin();
        userDetails = UserDetailsFactory.createCustomClientUser("Maria Brown");

    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUsernameExists() {
        when(userRepository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
        UserDetails result = service.loadUserByUsername(existingUsername);

        assertNotNull(result);
        assertThat(result.getUsername()).isEqualTo(existingUsername);
    }
    @Test
    public void loadUserByUsernameShouldThrowExceptionWhenUsernameDoesNotExist() {
        when(userRepository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUsername);
        });
    }

    @Test
    public void validateSelfOrAdminShouldNotThrowExceptionWhenIdMatch() {
        when(userService.authenticated()).thenReturn(client);

        assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(1L);
        });
    }

    @Test
    public void validateSelfOrAdminShouldThrowExceptionWhenIdDontMatch() {
        when(userService.authenticated()).thenReturn(client);

        assertThrows(ForbiddenException.class, () -> {
            service.validateSelfOrAdmin(200L);
        });
    }

    @Test
    public void validateSelfOfAdminShouldNotThrowExceptionWhenUserIsAdmin() {
        when(userService.authenticated()).thenReturn(admin);

        assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(200L);
        });
    }

}
