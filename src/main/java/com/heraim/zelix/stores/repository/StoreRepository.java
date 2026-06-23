package com.heraim.zelix.stores.repository;

import com.heraim.zelix.stores.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findBySlug(String slug);

    @Query("SELECT s FROM Store s WHERE " +
            "(:q IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:city IS NULL OR s.city = :city) " +
            "AND (:state IS NULL OR s.state = :state)")
    Page<Store> search(@Param("q") String q, @Param("city") String city, @Param("state") String state, Pageable pageable);
}
