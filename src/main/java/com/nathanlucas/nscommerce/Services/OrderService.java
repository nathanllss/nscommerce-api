package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.Services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.dtos.OrderDTO;
import com.nathanlucas.nscommerce.dtos.OrderItemDTO;
import com.nathanlucas.nscommerce.entities.Order;
import com.nathanlucas.nscommerce.entities.OrderItem;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.entities.User;
import com.nathanlucas.nscommerce.entities.enums.OrderStatus;
import com.nathanlucas.nscommerce.repositories.OrderItemRepository;
import com.nathanlucas.nscommerce.repositories.OrderRepository;
import com.nathanlucas.nscommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado"));
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {

        Order order = new Order();

        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        User user = userService.authenticated();
        order.setClient(user);

        for (OrderItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
