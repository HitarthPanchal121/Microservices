package com.Microservices.order_service.repository;

import com.Microservices.order_service.modal.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
