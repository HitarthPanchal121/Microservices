package com.Microservices.order_service.service;

import com.Microservices.order_service.dto.InventoryResponse;
import com.Microservices.order_service.dto.OrderLineItemsDto;
import com.Microservices.order_service.dto.OrderRequest;
import com.Microservices.order_service.modal.Order;
import com.Microservices.order_service.modal.OrderLineItems;
import com.Microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    public void placeOrders(OrderRequest orderRequest){
        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

   List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
   order.setOrderLineItemsList(orderLineItems);

   List<String> skuCodes=order.getOrderLineItemsList().stream()
           .map(OrderLineItems::getSkuCode)
           .toList();

   // call inventory service , and place order only if the product is in the stock
       InventoryResponse[] inventoryResponsesArray= webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();
        boolean allProductInStock= Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);
       if(allProductInStock){
           orderRepository.save(order);
       }
       else {
           throw new IllegalArgumentException("Product is not in stock Please try Again Later");
       }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
