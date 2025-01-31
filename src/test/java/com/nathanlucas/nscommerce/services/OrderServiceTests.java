package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.OrderDTO;
import com.nathanlucas.nscommerce.entities.Order;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.repositories.OrderRepository;
import com.nathanlucas.nscommerce.services.exceptions.ForbiddenException;
import com.nathanlucas.nscommerce.services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.utils.OrderFactory;
import com.nathanlucas.nscommerce.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;
    @Mock
    private AuthService authService;

    private Long existingId, nonExistingId;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 200L;
        admin = UserFactory.createAdmin();
        client = UserFactory.createClient();
        order = OrderFactory.createOrder(client);
        orderDTO = new OrderDTO(order);
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {
        when(repository.findById(existingId)).thenReturn(Optional.of(order));
        doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = service.findById(existingId);

        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(existingId);
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {
        when(repository.findById(existingId)).thenReturn(Optional.of(order));
        doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = service.findById(existingId);

        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(existingId);
    }

    @Test
    public void findByIdShouldThrowForbiddenExceptionWhenIdExistsAndOtherClientLogged() {
        when(repository.findById(existingId)).thenReturn(Optional.of(order));
        doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        assertThrows(ForbiddenException.class, () -> service.findById(existingId));

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

}
