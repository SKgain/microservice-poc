package com.skgain.order_service.feigns;

import com.skgain.order_service.models.PaymentRequest;
import com.skgain.order_service.models.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}
