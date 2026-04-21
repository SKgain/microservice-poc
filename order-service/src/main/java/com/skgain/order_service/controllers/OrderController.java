package com.skgain.order_service.controllers;

import com.skgain.order_service.feigns.PaymentClient;
import com.skgain.order_service.models.OrderRequest;
import com.skgain.order_service.models.PaymentRequest;
import com.skgain.order_service.models.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final PaymentClient paymentClient;

    @PostMapping
    public ResponseEntity<OrderRequest> placeOrder(@RequestBody OrderRequest order) {
        PaymentRequest paymentRequest = new PaymentRequest(
                order.getId(),
                order.getAmount()
        );

        PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);
        order.setPaymentStatus(paymentResponse.getStatus());

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequest> getOrder(@PathVariable Long id) {
        OrderRequest order = new OrderRequest(id, "Laptop", 1, 1200.00, "PAID");
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderRequest>> getAllOrders() {
        List<OrderRequest> orders = List.of(
                new OrderRequest(1L, "Laptop", 1, 1200.00, "PAID"),
                new OrderRequest(2L, "Phone", 2, 800.00, "PAID"),
                new OrderRequest(3L, "Tablet", 1, 500.00, "PENDING")
        );
        return ResponseEntity.ok(orders);
    }
}
