package com.heraim.zelix.followers.service;
import com.heraim.zelix.common.exception.DuplicateResourceException;
import com.heraim.zelix.common.exception.ResourceNotFoundException;
import com.heraim.zelix.followers.entity.StoreFollower;
import com.heraim.zelix.followers.repository.StoreFollowerRepository;
import com.heraim.zelix.stores.dto.StoreSummary;
import com.heraim.zelix.stores.entity.Store;
import com.heraim.zelix.stores.service.StoreService;
import com.heraim.zelix.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreFollowerService {
    private final StoreFollowerRepository storeFollowerRepository;
    private final StoreService storeService;

    @Transactional
    public void follow(UUID storeId, User user) {
        Store store = storeService.getStoreEntityById(storeId);

        if (storeFollowerRepository.existsByUserAndStore(user, store)) {
            throw new DuplicateResourceException("You are already following this store");
        }

        StoreFollower follower = StoreFollower.builder()
                .user(user)
                .store(store)
                .build();

        storeFollowerRepository.save(follower);

        storeService.incrementFollowerCount(store);
    }

    @Transactional
    public void unfollow(UUID storeId, User user) {
        Store store = storeService.getStoreEntityById(storeId);

        StoreFollower follower = storeFollowerRepository.findByUserAndStore(user, store)
                .orElseThrow(() -> new ResourceNotFoundException("You are not following this store"));

        storeFollowerRepository.delete(follower);

        storeService.decrementFollowerCount(store);
    }

    @Transactional(readOnly = true)
    public List<StoreSummary> getFollowing(User user) {
        return storeFollowerRepository.findByUser(user).stream()
                .map(follow -> StoreSummary.from(follow.getStore()))
                .toList();
    }
}
