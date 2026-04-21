package com.skgain.payment_service.controllers;

import com.skgain.payment_service.models.PaymentRequest;
import com.skgain.payment_service.models.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = new PaymentResponse(
                request.getOrderId(),
                "SUCCESS",
                UUID.randomUUID().toString()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long orderId) {
        PaymentResponse response = new PaymentResponse(
                orderId,
                "SUCCESS",
                "TXN-" + orderId + "-ABC123"
        );
        return ResponseEntity.ok(response);
    }
}
