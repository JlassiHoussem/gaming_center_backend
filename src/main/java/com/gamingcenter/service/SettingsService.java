package com.gamingcenter.service;

import com.gamingcenter.dto.SettingsDTO;
import com.gamingcenter.entity.Settings;
import com.gamingcenter.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingsService {

    private final SettingsRepository settingsRepository;

    private static final String DELIMITER = ",";

    public SettingsDTO getSettings() {
        return settingsRepository.findAll()
                .stream()
                .findFirst()
                .map(this::toDTO)
                .orElseGet(() -> new SettingsDTO(null, "Gaming Center Pro", "EUR", "Europe/Paris",
                        Arrays.asList("PC", "PS4", "PS5", "Xbox", "Simulateur"),
                        Arrays.asList("Boissons", "Snacks", "Repas")));
    }

    public SettingsDTO saveSettings(SettingsDTO dto) {
        Settings settings = settingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> Settings.builder().build());

        settings.setEstablishmentName(dto.establishmentName());
        settings.setCurrency(dto.currency());
        settings.setTimezone(dto.timezone());
        settings.setDeviceTypes(dto.deviceTypes() != null ? String.join(DELIMITER, dto.deviceTypes()) : "");
        settings.setBuffetCategories(dto.buffetCategories() != null ? String.join(DELIMITER, dto.buffetCategories()) : "");

        return toDTO(settingsRepository.save(settings));
    }

    private SettingsDTO toDTO(Settings s) {
        return new SettingsDTO(
                s.getId(),
                s.getEstablishmentName(),
                s.getCurrency(),
                s.getTimezone(),
                splitList(s.getDeviceTypes()),
                splitList(s.getBuffetCategories())
        );
    }

    private List<String> splitList(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split(DELIMITER));
    }
}
