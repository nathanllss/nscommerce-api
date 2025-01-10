package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.Services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.dtos.OrderDTO;
import com.nathanlucas.nscommerce.entities.Order;
import com.nathanlucas.nscommerce.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order  = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado"));
        return new OrderDTO(order);
    }
}
