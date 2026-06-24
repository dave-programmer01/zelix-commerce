package com.heraim.zelix.stores.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "store_operating_hours")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOperatingHours {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;  // java.time.DayOfWeek - already exists, no need to build your own enum


    private LocalTime openTime;
    private LocalTime closeTime;

    private boolean closed;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}