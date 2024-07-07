package com.dilshan.orderservice.command.api.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class CreateOrderRestModel {
    @NotBlank(message = "{order.productId.required}")
    private String productId;
    @Min(value = 1, message = "{order.quantity.min}")
    @Max(value = 5, message = "{order.quantity.max}")
    private Integer quantity;
    @NotBlank(message = "{order.addressId.required}")
    private String addressId;
}
