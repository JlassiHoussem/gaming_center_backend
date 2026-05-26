package com.gamingcenter.dto;

import java.time.LocalDateTime;

public record ShiftDTO(
    Long id,
    LocalDateTime openedAt,
    LocalDateTime closedAt,
    Boolean active,
    Double sessionRevenue,
    Double buffetRevenue,
    Double totalExpenses,
    Double netProfit
) {}
