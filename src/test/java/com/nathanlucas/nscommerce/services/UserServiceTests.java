package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.repositories.UserRepository;
import com.nathanlucas.nscommerce.util.CustomUserUtil;
import com.nathanlucas.nscommerce.utils.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;
    @Mock
    private CustomUserUtil customUserUtil;

    private User user;
    private UserDTO userDTO;
    private String loggedUsername;

    @BeforeEach
    void setUp() {
        user = UserFactory.createClient();
        userDTO = UserFactory.createClientDTO();
        loggedUsername = "maria@gmail.com";

    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserLogged() {
        when(customUserUtil.getLoggedUsername()).thenReturn(loggedUsername);
        when(repository.findByEmail(loggedUsername)).thenReturn(Optional.of(user));

        UserService spyService = spy(service);
        doReturn(user).when(spyService).authenticated();

        UserDTO loggedUser = service.getMe();

        assertThat(loggedUser).isNotNull();
        assertThat(loggedUser.getId()).isEqualTo(userDTO.getId());
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserNotLogged() {
        UserService spyService = spy(service);
        doThrow(ClassCastException.class).when(spyService).authenticated();

        assertThrows(UsernameNotFoundException.class, () -> service.getMe());
    }

    @Test
    public void authenticatedShouldReturnUserWhenUserLogged() {
        when(customUserUtil.getLoggedUsername()).thenReturn(loggedUsername);
        when(repository.findByEmail(loggedUsername)).thenReturn(Optional.of(user));

        User loggedUser = service.authenticated();

        assertThat(loggedUser).isNotNull();
        assertThat(loggedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserNotLogged() {
        doThrow(ClassCastException.class).when(customUserUtil).getLoggedUsername();

        assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
    }

}
