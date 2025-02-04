package com.nathanlucas.nscommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathanlucas.nscommerce.dtos.OrderDTO;
import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.Order;
import com.nathanlucas.nscommerce.entities.OrderItem;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.entities.enums.OrderStatus;
import com.nathanlucas.nscommerce.utils.ProductFactory;
import com.nathanlucas.nscommerce.utils.TokenUtil;
import com.nathanlucas.nscommerce.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private ObjectMapper objectMapper;
    private Long existingOrderId, nonExistingOrderId;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String adminToken, clientToken, invalidToken;
    private OrderDTO orderDTO;
    private Order order;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        existingOrderId = 1L;
        nonExistingOrderId = 1000L;

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto";

        user = UserFactory.createClient();
        order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, user, null);

        Product product = ProductFactory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() throws Exception {

        mockMvc.perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingOrderId))
                .andExpect(jsonPath("$.moment").value("2024-07-25T13:00:00Z"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.payment").exists())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.items[1].name").value("Macbook Pro"))
                .andExpect(jsonPath("$.total").exists());

    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndClientLogged() throws Exception {

        mockMvc.perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingOrderId))
                .andExpect(jsonPath("$.moment").value("2024-07-25T13:00:00Z"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.payment").exists())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.items[1].name").value("Macbook Pro"))
                .andExpect(jsonPath("$.total").exists());

    }

    @Test
    public void findByIdShouldReturnForbidden() throws Exception {

        mockMvc.perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingOrderId))
                .andExpect(jsonPath("$.moment").value("2024-07-25T13:00:00Z"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.payment").exists())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.items[1].name").value("Macbook Pro"))
                .andExpect(jsonPath("$.total").exists());

    }

    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoestNotBelongsToLoggedUser() throws Exception {

        Long otherOrderId = 2L;

        mockMvc.perform(get("/orders/{id}", otherOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() throws Exception {

        mockMvc.perform(get("/orders/{id}", nonExistingOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndClientLogged() throws Exception {

        mockMvc.perform(get("/orders/{id}", nonExistingOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenNoUserLogged() throws Exception {

        mockMvc.perform(get("/orders/{id}", existingOrderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }
    @Test
    public void findByIdShouldReturnUnauthorizedWheninvalidToken() throws Exception {

        mockMvc.perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }
}
