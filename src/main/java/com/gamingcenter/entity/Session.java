package com.gamingcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private Boolean active;

    @PrePersist
    protected void onCreate() {
        if (active == null) active = true;
    }

    public Double calculateAmount() {
        if (startTime == null || endTime == null || device == null) return 0.0;
        double hours = java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;
        return Math.round(hours * device.getHourlyRate() * 100.0) / 100.0;
    }
}
