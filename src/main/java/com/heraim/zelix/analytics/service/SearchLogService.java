package com.heraim.zelix.analytics.service;

import com.heraim.zelix.analytics.entity.SearchLog;
import com.heraim.zelix.analytics.repository.SearchLogRepository;
import com.heraim.zelix.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;

    public void logSearch(String searchTerm, Double latitude, Double longitude, int resultCount, User user) {
        try {
            SearchLog logEntry = SearchLog.builder()
                    .searchTerm(searchTerm != null ? searchTerm : "")
                    .latitude(latitude != null ? BigDecimal.valueOf(latitude) : null)
                    .longitude(longitude != null ? BigDecimal.valueOf(longitude) : null)
                    .resultCount(resultCount)
                    .user(user)
                    .build();
            searchLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to save search log: {}", e.getMessage(), e);
        }
    }
}
