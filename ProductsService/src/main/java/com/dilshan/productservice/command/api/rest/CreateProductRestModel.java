package com.dilshan.productservice.command.api.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class CreateProductRestModel {
    @NotBlank(message = "{product.title.required}")
    private String title;
    @Min(value = 1, message = "{product.price.min}")
    private BigDecimal price;
    @Min(value = 1, message = "{product.quantity.min}")
    @Max(value = 5, message = "{product.quantity.max}")
    private Integer quantity;
}
