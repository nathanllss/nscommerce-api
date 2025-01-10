package com.nathanlucas.nscommerce.repositories;

import com.nathanlucas.nscommerce.entities.OrderItem;
import com.nathanlucas.nscommerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}