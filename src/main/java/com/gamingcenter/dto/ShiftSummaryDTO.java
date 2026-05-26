package com.gamingcenter.dto;

public record ShiftSummaryDTO(
    String date,
    String openedAt,
    String closedAt,
    Double sessionRevenue,
    Double buffetRevenue,
    Double expenses,
    Double netProfit
) {}
