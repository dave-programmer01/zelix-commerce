package com.heraim.zelix.analytics.repository;

import com.heraim.zelix.analytics.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
    
}
