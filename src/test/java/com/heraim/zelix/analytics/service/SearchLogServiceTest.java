package com.heraim.zelix.analytics.service;

import com.heraim.zelix.analytics.entity.SearchLog;
import com.heraim.zelix.analytics.repository.SearchLogRepository;
import com.heraim.zelix.users.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchLogServiceTest {

    @Mock
    private SearchLogRepository searchLogRepository;

    @InjectMocks
    private SearchLogService searchLogService;

    @Test
    void logSearch_Success() {
        searchLogService.logSearch("test", 1.0, 2.0, 10, new User());
        verify(searchLogRepository, times(1)).save(any(SearchLog.class));
    }

    @Test
    void logSearch_HandlesExceptionGracefully() {
        when(searchLogRepository.save(any(SearchLog.class))).thenThrow(new RuntimeException("DB error"));
        
        // Should not throw exception
        searchLogService.logSearch("test", 1.0, 2.0, 10, null);
        
        verify(searchLogRepository, times(1)).save(any(SearchLog.class));
    }
}
