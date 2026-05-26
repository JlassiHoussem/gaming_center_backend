package com.gamingcenter.controller;

import com.gamingcenter.dto.SessionDTO;
import com.gamingcenter.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SessionDTO>> getActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }

    @PostMapping("/start/{deviceId}")
    public ResponseEntity<SessionDTO> startSession(@PathVariable Long deviceId) {
        return ResponseEntity.ok(sessionService.startSession(deviceId));
    }

    @PostMapping("/stop/{sessionId}")
    public ResponseEntity<SessionDTO> stopSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.stopSession(sessionId));
    }

    @PostMapping("/stop/device/{deviceId}")
    public ResponseEntity<SessionDTO> stopSessionByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(sessionService.stopSessionByDevice(deviceId));
    }
}
