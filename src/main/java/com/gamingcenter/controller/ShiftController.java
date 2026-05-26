package com.gamingcenter.controller;

import com.gamingcenter.dto.ShiftDTO;
import com.gamingcenter.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping("/open")
    public ResponseEntity<ShiftDTO> openShift() {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.openShift());
    }

    @PostMapping("/close")
    public ResponseEntity<ShiftDTO> closeShift() {
        return ResponseEntity.ok(shiftService.closeShift());
    }

    @GetMapping("/current")
    public ResponseEntity<ShiftDTO> getCurrentShift() {
        ShiftDTO shift = shiftService.getCurrentShift();
        if (shift == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shift);
    }

    @GetMapping
    public ResponseEntity<List<ShiftDTO>> getShiftHistory() {
        return ResponseEntity.ok(shiftService.getShiftHistory());
    }
}
