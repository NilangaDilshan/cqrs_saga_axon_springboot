package com.dilshan.productservice.query.api.controller;

import com.dilshan.productservice.query.api.queries.FindProductsQuery;
import com.dilshan.productservice.query.api.rest.ProductRestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductsQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public List<ProductRestModel> getProduct() {
        log.info("Get Products");
        var findProductsQuery = new FindProductsQuery();
        var products = this.queryGateway.query(findProductsQuery,
                ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();
        return products;
    }
}
