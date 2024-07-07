package com.dilshan.productservice.command.api.controller;

import com.dilshan.productservice.command.api.commands.CreateProductCommand;
import com.dilshan.productservice.command.api.rest.CreateProductRestModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@Slf4j
@RequiredArgsConstructor
public class ProductsCommandController {

    private final Environment environment;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {
        log.info("Create Product: {}", createProductRestModel);
        var productCommand = CreateProductCommand.builder()
                .price(createProductRestModel.getPrice())
                .quantity(createProductRestModel.getQuantity())
                .title(createProductRestModel.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();
        var productId = this.commandGateway.sendAndWait(productCommand);
        return "Create Product Id: %s".formatted(productId);
    }

   /* @GetMapping
    public String getProducts() {
        log.info("Get Products");
        return "Get Products from port: %s".formatted(environment.getProperty("local.server.port"));
    }

    @PutMapping
    public String updateProduct() {
        log.info("Update Product");
        return "Update Product";
    }

    @DeleteMapping
    public String deleteProduct() {
        log.info("Delete Product");
        return "Delete Product";
    }*/
}
