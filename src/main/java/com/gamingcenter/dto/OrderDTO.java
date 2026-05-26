package com.gamingcenter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderDTO(
    Long id,
    List<OrderItemDTO> items,
    Double total,
    Long sessionId
) {}
