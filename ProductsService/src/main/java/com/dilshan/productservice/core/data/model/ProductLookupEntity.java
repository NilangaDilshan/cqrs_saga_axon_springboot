package com.dilshan.productservice.core.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products_lookup")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLookupEntity {
    /*
     * This class is used to store the product lookup data in the database.
     * */
    @Id
    private String productId;
    @Column(unique = true, nullable = false)
    private String title;
}
