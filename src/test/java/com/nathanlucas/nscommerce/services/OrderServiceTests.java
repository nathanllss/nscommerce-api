package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.OrderDTO;
import com.nathanlucas.nscommerce.dtos.OrderItemDTO;
import com.nathanlucas.nscommerce.dtos.ProductDTO;
import com.nathanlucas.nscommerce.entities.Order;
import com.nathanlucas.nscommerce.entities.OrderItem;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.repositories.OrderItemRepository;
import com.nathanlucas.nscommerce.repositories.OrderRepository;
import com.nathanlucas.nscommerce.repositories.ProductRepository;
import com.nathanlucas.nscommerce.services.exceptions.ForbiddenException;
import com.nathanlucas.nscommerce.services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.utils.OrderFactory;
import com.nathanlucas.nscommerce.utils.ProductFactory;
import com.nathanlucas.nscommerce.utils.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
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
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserService userService;

    private Long existingId, nonExistingId, existingProductId, nonExistingProductId;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;
    private Product product;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        existingProductId = 1L;
        nonExistingId = 200L;
        nonExistingProductId = 200L;
        admin = UserFactory.createAdmin();
        client = UserFactory.createClient();
        product = ProductFactory.createProduct();
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

    @Test
    public void insertShouldReturnOrderDTOWhenUserLogged() {
        when(userService.authenticated()).thenReturn(admin);

        when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        when(repository.save(any())).thenReturn(order);
        when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));

        OrderDTO result = service.insert(orderDTO);

        assertNotNull(result);
    }

    @Test
    public void insertShouldThrowUsernameNotFoundExceptionWhenUserNotLogged() {
        doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        assertThrows(UsernameNotFoundException.class, () -> service.insert(orderDTO));
    }

    @Test
    public void insertShouldThrowEntityNotFoundExceptionWhenProductDoesNotExist() {
        when(userService.authenticated()).thenReturn(client);
        when(productRepository.getReferenceById(any())).thenThrow(EntityNotFoundException.class);


        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        OrderItemDTO orderItemDTO = new OrderItemDTO(orderItem);
        orderDTO.getItems().add(orderItemDTO);

        assertThrows(EntityNotFoundException.class, () -> {
            OrderDTO result = service.insert(orderDTO);
        });

    }
}
