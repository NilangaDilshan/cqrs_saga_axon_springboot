package com.dilshan.productservice.core.data.repository;

import com.dilshan.productservice.core.data.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByProductId(String productId);

    Optional<ProductEntity> findByProductIdOrTitle(String productId, String title);

    @Query("SELECT COUNT(pe) FROM ProductEntity pe")
    long countAllRecords();


}
