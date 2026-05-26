package com.gamingcenter.controller;

import com.gamingcenter.dto.SettingsDTO;
import com.gamingcenter.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<SettingsDTO> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<SettingsDTO> saveSettings(@Valid @RequestBody SettingsDTO dto) {
        return ResponseEntity.ok(settingsService.saveSettings(dto));
    }
}
