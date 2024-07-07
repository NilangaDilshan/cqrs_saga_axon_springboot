package com.dilshan.productservice.query.api.projection;

import com.dilshan.productservice.core.data.repository.ProductsRepository;
import com.dilshan.productservice.query.api.queries.FindProductsQuery;
import com.dilshan.productservice.query.api.rest.ProductRestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductsProjection {

    private final ProductsRepository productsRepository;
    
    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query) {
        log.info("Get Products");
        return this.productsRepository.findAll().stream().map(productEntity -> new ProductRestModel(productEntity.getProductId(),
                productEntity.getTitle(), productEntity.getPrice(), productEntity.getQuantity())
        ).toList();
    }
}
