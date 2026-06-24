package com.heraim.zelix.stores.entity;

import com.heraim.zelix.category.entity.Category;
import com.heraim.zelix.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "stores")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;

    @Column(unique = true)
    private String slug;

    private String description;

    private String phone;

    private String whatsapp;

    private String email;

    private String address;

    private String city;

    private String state;

    private String country;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String logoUrl;

    private String bannerUrl;

    private String paymentAccountName;

    private String paymentAccountNumber;

    private String paymentBankName;

    @Builder.Default
    private int followerCount = 0;

    @Builder.Default
    private int productCount = 0;

    @Builder.Default
    private int trustScore = 50;

    @Builder.Default
    private boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ElementCollection(targetClass = DeliveryOption.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "store_delivery_options", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "delivery_option")
    @Builder.Default
    private Set<DeliveryOption> deliveryOptions = new HashSet<>();

    @ElementCollection(targetClass = PaymentMethod.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "store_accepted_payment_methods", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "payment_method")
    @Builder.Default
    private Set<PaymentMethod> acceptedPaymentMethods = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VerificationTier verificationTier = VerificationTier.STANDARD;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

