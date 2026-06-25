package com.heraim.zelix.stores.dto;

import com.heraim.zelix.stores.entity.Store;
import java.util.UUID;

public record StoreSummary(
        UUID id,
        String name,
        String slug,
        String city,
        String logoUrl
) {
    public static StoreSummary from(Store store) {
        return new StoreSummary(
                store.getId(),
                store.getName(),
                store.getSlug(),
                store.getCity(),
                store.getLogoUrl()
        );
    }
}
