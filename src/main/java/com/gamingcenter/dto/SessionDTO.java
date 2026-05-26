package com.gamingcenter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record SessionDTO(
    Long id,
    Long deviceId,
    String deviceName,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startTime,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endTime,
    Double amount,
    Boolean active,
    Double durationHours
) {}
