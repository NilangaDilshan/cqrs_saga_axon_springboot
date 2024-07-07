package com.dilshan.productservice.core.data.repository;

import com.dilshan.productservice.core.data.model.ProductLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductLookupEntityRepository extends JpaRepository<ProductLookupEntity, String> {
    Optional<ProductLookupEntity> findByProductIdOrTitle(String productId, String title);
}
