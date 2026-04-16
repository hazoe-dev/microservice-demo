package dev.hazoe.order_service.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long userId;
    private String product;
    private Double amount;
}
