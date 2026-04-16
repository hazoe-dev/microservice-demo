package dev.hazoe.order_service.dto;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String product;
    private Double amount;
    private String status;
}
