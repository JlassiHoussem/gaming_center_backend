package com.gamingcenter.dto;

import java.util.List;

public record ReportDTO(
    String period,
    Double totalSales,
    Double totalExpenses,
    Double netProfit,
    List<RevenuePerDayDTO> revenuePerDay,
    List<DeviceHoursDTO> deviceHours,
    List<TopProductDTO> topProducts,
    List<ShiftSummaryDTO> shifts
) {}
