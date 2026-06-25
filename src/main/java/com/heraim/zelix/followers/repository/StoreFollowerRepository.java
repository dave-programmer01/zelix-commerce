package com.heraim.zelix.followers.repository;

import com.heraim.zelix.stores.entity.Store;
import com.heraim.zelix.followers.entity.StoreFollower;
import com.heraim.zelix.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreFollowerRepository extends JpaRepository<StoreFollower, UUID> {
    boolean existsByUserAndStore(User user, Store store);
    Optional<StoreFollower> findByUserAndStore(User user, Store store);
    long countByStore(Store store);

    List<StoreFollower> findByUser(User user);
}
