package com.gamingcenter.dto;

import com.gamingcenter.entity.Device.DeviceStatus;
import com.gamingcenter.entity.Device.DeviceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceDTO(
    Long id,
    @NotBlank(message = "Le nom est requis")
    String name,
    @NotNull(message = "Le type est requis")
    DeviceType type,
    @NotNull(message = "Le tarif est requis")
    @Min(value = 1, message = "Le tarif doit être supérieur à 0")
    Double hourlyRate,
    DeviceStatus status
) {}
