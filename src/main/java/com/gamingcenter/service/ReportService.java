package com.gamingcenter.service;

import com.gamingcenter.dto.*;
import com.gamingcenter.entity.*;
import com.gamingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final SessionRepository sessionRepository;
    private final OrderRepository orderRepository;
    private final ExpenseRepository expenseRepository;
    private final ShiftRepository shiftRepository;
    private final DeviceRepository deviceRepository;

    public ReportDTO getWeeklyReport() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        return buildReport("7 derniers jours", from);
    }

    public ReportDTO getMonthlyReport() {
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        return buildReport("30 derniers jours", from);
    }

    public ReportDTO getAnnualReport() {
        LocalDateTime from = LocalDateTime.now().minusDays(365);
        return buildReport("Année complète", from);
    }

    private ReportDTO buildReport(String period, LocalDateTime from) {
        List<Session> sessions = sessionRepository.findAll().stream()
                .filter(s -> s.getEndTime() != null && s.getEndTime().isAfter(from))
                .toList();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getOrderDate().isAfter(from))
                .toList();

        List<Expense> expenses = expenseRepository.findAll().stream()
                .filter(e -> e.getExpenseDate().isAfter(from))
                .toList();

        List<Shift> shifts = shiftRepository.findAll().stream()
                .filter(s -> s.getOpenedAt().isAfter(from))
                .toList();

        double totalSales = sessions.stream().mapToDouble(s -> s.getAmount() != null ? s.getAmount() : 0).sum()
                + orders.stream().mapToDouble(Order::getTotal).sum();
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();

        List<RevenuePerDayDTO> revenuePerDay = buildRevenuePerDay(sessions, orders, from);
        List<DeviceHoursDTO> deviceHours = buildDeviceHours(sessions);
        List<TopProductDTO> topProducts = buildTopProducts(orders);
        List<ShiftSummaryDTO> shiftSummaries = buildShiftSummaries(shifts);

        return new ReportDTO(period, totalSales, totalExpenses, totalSales - totalExpenses,
                revenuePerDay, deviceHours, topProducts, shiftSummaries);
    }

    private List<RevenuePerDayDTO> buildRevenuePerDay(List<Session> sessions, List<Order> orders, LocalDateTime from) {
        Map<String, double[]> dailyData = new LinkedHashMap<>();

        sessions.forEach(s -> {
            String day = s.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM"));
            dailyData.computeIfAbsent(day, k -> new double[2])[0] += s.getAmount() != null ? s.getAmount() : 0;
        });

        orders.forEach(o -> {
            String day = o.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM"));
            dailyData.computeIfAbsent(day, k -> new double[2])[1] += o.getTotal();
        });

        return dailyData.entrySet().stream()
                .map(e -> new RevenuePerDayDTO(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();
    }

    private List<DeviceHoursDTO> buildDeviceHours(List<Session> sessions) {
        Map<String, Double> deviceHours = new LinkedHashMap<>();

        sessions.forEach(s -> {
            if (s.getEndTime() != null) {
                double hours = Duration.between(s.getStartTime(), s.getEndTime()).toMinutes() / 60.0;
                String name = s.getDevice().getName();
                deviceHours.merge(name, hours, Double::sum);
            }
        });

        return deviceHours.entrySet().stream()
                .map(e -> new DeviceHoursDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                .sorted(Comparator.comparingDouble(DeviceHoursDTO::hours).reversed())
                .toList();
    }

    private List<TopProductDTO> buildTopProducts(List<Order> orders) {
        Map<String, int[]> productData = new LinkedHashMap<>();

        orders.forEach(o -> o.getItems().forEach(item -> {
            String name = item.getProduct().getName();
            int[] data = productData.computeIfAbsent(name, k -> new int[2]);
            data[0] += item.getQuantity();
            data[1] += item.getQuantity() * item.getUnitPrice().intValue();
        }));

        return productData.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]))
                .limit(5)
                .map(e -> new TopProductDTO(0, e.getKey(), e.getValue()[0], (double) e.getValue()[1]))
                .toList();
    }

    private List<ShiftSummaryDTO> buildShiftSummaries(List<Shift> shifts) {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM");

        return shifts.stream()
                .sorted(Comparator.comparing(Shift::getOpenedAt).reversed())
                .limit(20)
                .map(s -> new ShiftSummaryDTO(
                        s.getOpenedAt().format(dateFmt),
                        s.getOpenedAt().format(timeFmt),
                        s.getClosedAt() != null ? s.getClosedAt().format(timeFmt) : "—",
                        s.getSessionRevenue(),
                        s.getBuffetRevenue(),
                        s.getTotalExpenses(),
                        s.getNetProfit()
                ))
                .toList();
    }
}
