package com.gamingcenter.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SettingsDTO(
    Long id,
    @NotBlank String establishmentName,
    @NotBlank String currency,
    @NotBlank String timezone,
    List<String> deviceTypes,
    List<String> buffetCategories
) {}
