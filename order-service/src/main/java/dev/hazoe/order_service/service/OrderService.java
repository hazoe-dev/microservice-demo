package dev.hazoe.order_service.service;

import dev.hazoe.order_service.client.UserClient;
import dev.hazoe.order_service.dto.CreateOrderRequest;
import dev.hazoe.order_service.dto.OrderResponse;
import dev.hazoe.order_service.dto.UserResponse;
import dev.hazoe.order_service.entity.Order;
import dev.hazoe.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;

    public OrderResponse createOrder(CreateOrderRequest request) {
        UserResponse user = userClient.getUserById(request.getUserId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "User not found: " + request.getUserId()
            ));

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setUserName(user.getName());
        order.setProduct(request.getProduct());
        order.setAmount(request.getAmount());

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Order not found: " + id
            ));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setUserName(order.getUserName());
        response.setProduct(order.getProduct());
        response.setAmount(order.getAmount());
        response.setStatus(order.getStatus());
        return response;
    }
}
