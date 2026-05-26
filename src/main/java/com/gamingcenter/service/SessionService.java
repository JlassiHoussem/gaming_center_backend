package com.gamingcenter.service;

import com.gamingcenter.dto.SessionDTO;
import com.gamingcenter.entity.Device;
import com.gamingcenter.entity.Session;
import com.gamingcenter.entity.Shift;
import com.gamingcenter.exception.BusinessException;
import com.gamingcenter.exception.ResourceNotFoundException;
import com.gamingcenter.repository.DeviceRepository;
import com.gamingcenter.repository.SessionRepository;
import com.gamingcenter.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final DeviceRepository deviceRepository;
    private final ShiftRepository shiftRepository;

    public List<SessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<SessionDTO> getActiveSessions() {
        return sessionRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    public SessionDTO startSession(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Appareil non trouvé: " + deviceId));

        if (device.getStatus() != Device.DeviceStatus.LIBRE) {
            throw new BusinessException("L'appareil n'est pas disponible (statut: " + device.getStatus() + ")");
        }

        Session session = Session.builder()
                .device(device)
                .startTime(LocalDateTime.now())
                .active(true)
                .build();

        device.setStatus(Device.DeviceStatus.OCCUPE);
        deviceRepository.save(device);

        shiftRepository.findByActiveTrue().ifPresent(session::setShift);

        session = sessionRepository.save(session);
        return toDTO(session);
    }

    public SessionDTO stopSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée: " + sessionId));

        if (!session.getActive()) {
            throw new BusinessException("Cette session est déjà terminée");
        }

        session.setEndTime(LocalDateTime.now());
        session.setAmount(session.calculateAmount());
        session.setActive(false);

        Device device = session.getDevice();
        device.setStatus(Device.DeviceStatus.LIBRE);
        deviceRepository.save(device);

        session = sessionRepository.save(session);
        return toDTO(session);
    }

    public SessionDTO stopSessionByDevice(Long deviceId) {
        Session session = sessionRepository.findByDeviceIdAndActiveTrue(deviceId)
                .orElseThrow(() -> new BusinessException("Aucune session active pour cet appareil"));
        return stopSession(session.getId());
    }

    private SessionDTO toDTO(Session session) {
        double durationHours = 0;
        if (session.getStartTime() != null) {
            LocalDateTime end = session.getEndTime() != null ? session.getEndTime() : LocalDateTime.now();
            durationHours = Duration.between(session.getStartTime(), end).toMinutes() / 60.0;
        }
        return new SessionDTO(
                session.getId(),
                session.getDevice().getId(),
                session.getDevice().getName(),
                session.getStartTime(),
                session.getEndTime(),
                session.getAmount(),
                session.getActive(),
                Math.round(durationHours * 100.0) / 100.0
        );
    }
}
