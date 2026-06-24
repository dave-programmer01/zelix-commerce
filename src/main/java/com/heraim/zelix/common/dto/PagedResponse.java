package com.heraim.zelix.common.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> data,
        PaginationMetadata pagination
) {
    public record PaginationMetadata(
            int page,
            int limit,
            long total,
            int pages
    ) {}
}
