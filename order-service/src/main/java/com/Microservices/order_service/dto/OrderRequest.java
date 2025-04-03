package com.Microservices.order_service.dto;

import com.Microservices.order_service.modal.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  OrderRequest {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
