package com.skgain.order_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long id;
    private String product;
    private int quantity;
    private double amount;
    private String paymentStatus;
}
