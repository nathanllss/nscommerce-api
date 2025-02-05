package com.nathanlucas.nscommerce.controllers;

import com.nathanlucas.nscommerce.dtos.OrderDTO;
import com.nathanlucas.nscommerce.entities.Order;
import com.nathanlucas.nscommerce.entities.OrderItem;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.entities.enums.OrderStatus;
import com.nathanlucas.nscommerce.utils.ProductFactory;
import com.nathanlucas.nscommerce.utils.TokenUtilRA;
import com.nathanlucas.nscommerce.utils.UserFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private Long existingOrderId, nonExistingOrderId;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String adminToken, clientToken, invalidToken;
    private OrderDTO orderDTO;
    private Order order;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        baseURI = "http://localhost:8081";

        existingOrderId = 1L;
        nonExistingOrderId = 1000L;

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        adminToken = TokenUtilRA.obtainAccessToken(adminUsername, adminPassword);
        clientToken = TokenUtilRA.obtainAccessToken(clientUsername, clientPassword);
        invalidToken = adminToken + "xpto";

        user = UserFactory.createClient();
        order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, user, null);

        Product product = ProductFactory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", existingOrderId)
        .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2024-07-25T13:00:00Z"))
                .body("status", is("PAID"))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2024-07-25T15:00:00Z"))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("total", is(1431.0f));

    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", existingOrderId)
        .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2024-07-25T13:00:00Z"))
                .body("status", is("PAID"))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2024-07-25T15:00:00Z"))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("total", is(1431.0f));

    }

    @Test
    public void findByIdShouldReturnForbiddenWhenClientLoggedAndIdDoesNotBelongToClient() {
        Long otherOrderId = 2L;

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", otherOrderId)
        .then()
                .statusCode(403);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", nonExistingOrderId)
        .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", nonExistingOrderId)
        .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenTokenIsInvalid() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", existingOrderId)
        .then()
                .statusCode(401);
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenNoTokenIsGiven() {
        given()
                .header("Content-Type", "application/json")
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", existingOrderId)
        .then()
                .statusCode(401);
    }
}
