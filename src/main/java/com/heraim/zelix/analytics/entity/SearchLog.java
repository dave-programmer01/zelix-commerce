package com.heraim.zelix.analytics.entity;

import com.heraim.zelix.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_logs")
@Builder
public class SearchLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String searchTerm;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @Column(nullable = false, name = "results_count")
    private Integer resultCount;

    @CreationTimestamp
    private LocalDateTime createdAt;



}
