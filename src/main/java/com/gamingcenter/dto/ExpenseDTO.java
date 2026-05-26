package com.gamingcenter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ExpenseDTO(
    Long id,
    @NotBlank(message = "La description est requise")
    String description,
    @NotNull(message = "Le montant est requis")
    @Min(value = 1, message = "Le montant doit être supérieur à 0")
    Double amount,
    LocalDateTime expenseDate,
    Long shiftId
) {}
