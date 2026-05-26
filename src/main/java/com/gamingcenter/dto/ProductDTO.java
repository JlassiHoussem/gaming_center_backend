package com.gamingcenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record ProductDTO(
    Long id,
    @NotBlank(message = "Le nom est requis")
    String name,
    @NotBlank(message = "La catégorie est requise")
    String category,
    @NotNull(message = "Le prix est requis")
    @Min(value = 1, message = "Le prix doit être supérieur à 0")
    Double price,
    String imageUrl,
    Boolean active
) {}
