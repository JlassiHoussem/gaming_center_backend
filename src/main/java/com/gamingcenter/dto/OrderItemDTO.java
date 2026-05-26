package com.gamingcenter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDTO(
    Long id,
    @NotNull(message = "Le produit est requis")
    Long productId,
    @NotNull(message = "La quantité est requise")
    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    Integer quantity,
    Double unitPrice,
    Double subtotal
) {}
