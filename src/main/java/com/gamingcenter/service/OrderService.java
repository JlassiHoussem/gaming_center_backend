package com.gamingcenter.service;

import com.gamingcenter.dto.OrderDTO;
import com.gamingcenter.dto.OrderItemDTO;
import com.gamingcenter.entity.*;
import com.gamingcenter.exception.BusinessException;
import com.gamingcenter.exception.ResourceNotFoundException;
import com.gamingcenter.repository.OrderRepository;
import com.gamingcenter.repository.ProductRepository;
import com.gamingcenter.repository.SessionRepository;
import com.gamingcenter.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SessionRepository sessionRepository;
    private final ShiftRepository shiftRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<OrderDTO> getOrdersByShift(Long shiftId) {
        return orderRepository.findByShiftId(shiftId).stream()
                .map(this::toDTO)
                .toList();
    }

    public OrderDTO createOrder(OrderDTO dto) {
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BusinessException("La commande doit contenir au moins un article");
        }

        Order order = Order.builder()
                .items(new ArrayList<>())
                .build();

        // Associate with session if provided
        if (dto.sessionId() != null) {
            Session session = sessionRepository.findById(dto.sessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée: " + dto.sessionId()));
            order.setShift(session.getShift());
        } else {
            shiftRepository.findByActiveTrue().ifPresent(order::setShift);
        }

        for (OrderItemDTO itemDTO : dto.items()) {
            Product product = productRepository.findById(itemDTO.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé: " + itemDTO.productId()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDTO.quantity())
                    .unitPrice(itemDTO.unitPrice() != null ? itemDTO.unitPrice() : product.getPrice())
                    .build();
            order.getItems().add(item);
        }

        order.calculateTotal();
        order = orderRepository.save(order);

        // Update shift buffet revenue
        if (order.getShift() != null) {
            Shift shift = order.getShift();
            shift.setBuffetRevenue(shift.getBuffetRevenue() + order.getTotal());
            shiftRepository.save(shift);
        }

        return toDTO(order);
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderDTO(
                order.getId(),
                itemDTOs,
                order.getTotal(),
                null
        );
    }
}
