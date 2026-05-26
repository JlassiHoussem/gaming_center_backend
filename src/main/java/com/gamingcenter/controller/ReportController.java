package com.gamingcenter.controller;

import com.gamingcenter.dto.ReportDTO;
import com.gamingcenter.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/weekly")
    public ResponseEntity<ReportDTO> getWeeklyReport() {
        return ResponseEntity.ok(reportService.getWeeklyReport());
    }

    @GetMapping("/monthly")
    public ResponseEntity<ReportDTO> getMonthlyReport() {
        return ResponseEntity.ok(reportService.getMonthlyReport());
    }

    @GetMapping("/annual")
    public ResponseEntity<ReportDTO> getAnnualReport() {
        return ResponseEntity.ok(reportService.getAnnualReport());
    }
}
