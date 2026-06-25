package com.heraim.zelix.products.repository;

import com.heraim.zelix.products.entity.ProductAvailabilityStatus;
import com.heraim.zelix.products.entity.Product;
import com.heraim.zelix.stores.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    long countByStoreAndIsDeletedFalse(Store store);

    @Query("SELECT p FROM Product p JOIN p.store s WHERE " +
            "p.isDeleted = false AND p.availabilityStatus <> :outOfStock " +
            "AND (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:category IS NULL OR p.category.id = :category) " +
            "AND (:city IS NULL OR s.city = :city) " +
            "AND (:state IS NULL OR s.state = :state)")
    Page<Product> search(
            @Param("q") String q,
            @Param("category") UUID category,
            @Param("city") String city,
            @Param("state") String state,
            @Param("outOfStock") ProductAvailabilityStatus outOfStock,
            Pageable pageable);
}
