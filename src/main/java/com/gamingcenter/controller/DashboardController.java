package com.gamingcenter.controller;

import com.gamingcenter.dto.KpiDTO;
import com.gamingcenter.entity.Device;
import com.gamingcenter.entity.Order;
import com.gamingcenter.entity.Session;
import com.gamingcenter.entity.Expense;
import com.gamingcenter.repository.DeviceRepository;
import com.gamingcenter.repository.OrderRepository;
import com.gamingcenter.repository.SessionRepository;
import com.gamingcenter.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final SessionRepository sessionRepository;
    private final DeviceRepository deviceRepository;
    private final OrderRepository orderRepository;
    private final ExpenseRepository expenseRepository;

    @GetMapping("/kpis")
    public ResponseEntity<KpiDTO> getKpis() {
        List<Session> sessions = sessionRepository.findAll();
        List<Device> devices = deviceRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();

        double revenusTotaux = sessions.stream()
                .filter(s -> !s.getActive() && s.getAmount() != null)
                .mapToDouble(Session::getAmount)
                .sum();

        long appareilsActifs = devices.stream()
                .filter(d -> d.getStatus() == Device.DeviceStatus.OCCUPE)
                .count();

        double ventesBuffet = orders.stream()
                .mapToDouble(Order::getTotal)
                .sum();

        double totalExpenses = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double beneficeNet = revenusTotaux + ventesBuffet - totalExpenses;

        return ResponseEntity.ok(new KpiDTO(
                Math.round(revenusTotaux * 100.0) / 100.0,
                (int) appareilsActifs,
                Math.round(ventesBuffet * 100.0) / 100.0,
                Math.round(beneficeNet * 100.0) / 100.0
        ));
    }
}
