package com.gamingcenter.service;

import com.gamingcenter.dto.DeviceDTO;
import com.gamingcenter.entity.Device;
import com.gamingcenter.entity.Session;
import com.gamingcenter.exception.BusinessException;
import com.gamingcenter.exception.ResourceNotFoundException;
import com.gamingcenter.repository.DeviceRepository;
import com.gamingcenter.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final SessionRepository sessionRepository;

    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DeviceDTO getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appareil non trouvé: " + id));
        return toDTO(device);
    }

    public DeviceDTO createDevice(DeviceDTO dto) {
        if (deviceRepository.existsByName(dto.name())) {
            throw new BusinessException("Un appareil avec ce nom existe déjà");
        }
        Device device = Device.builder()
                .name(dto.name())
                .type(dto.type())
                .hourlyRate(dto.hourlyRate())
                .status(dto.status() != null ? dto.status() : Device.DeviceStatus.LIBRE)
                .build();
        device = deviceRepository.save(device);
        return toDTO(device);
    }

    public DeviceDTO updateDevice(Long id, DeviceDTO dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appareil non trouvé: " + id));
        device.setName(dto.name());
        device.setType(dto.type());
        device.setHourlyRate(dto.hourlyRate());
        if (dto.status() != null) device.setStatus(dto.status());
        device = deviceRepository.save(device);
        return toDTO(device);
    }

    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appareil non trouvé: " + id));

        List<Session> activeSessions = sessionRepository.findByDeviceIdAndActiveTrue(id).stream().toList();
        for (Session session : activeSessions) {
            session.setEndTime(LocalDateTime.now());
            session.setAmount(session.calculateAmount());
            session.setActive(false);
            sessionRepository.save(session);
        }

        List<Session> allSessions = sessionRepository.findByDeviceId(id);
        sessionRepository.deleteAll(allSessions);

        deviceRepository.delete(device);
    }

    private DeviceDTO toDTO(Device device) {
        return new DeviceDTO(
                device.getId(),
                device.getName(),
                device.getType(),
                device.getHourlyRate(),
                device.getStatus()
        );
    }
}
