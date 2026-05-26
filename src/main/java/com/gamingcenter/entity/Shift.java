package com.gamingcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @Column(nullable = false)
    private Boolean active;

    @Builder.Default
    private Double sessionRevenue = 0.0;

    @Builder.Default
    private Double buffetRevenue = 0.0;

    @Builder.Default
    private Double totalExpenses = 0.0;

    @PrePersist
    protected void onCreate() {
        if (active == null) active = true;
        if (openedAt == null) openedAt = LocalDateTime.now();
    }

    public Double getNetProfit() {
        return sessionRevenue + buffetRevenue - totalExpenses;
    }
}
