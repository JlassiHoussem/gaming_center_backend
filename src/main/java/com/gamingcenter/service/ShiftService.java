package com.gamingcenter.service;

import com.gamingcenter.dto.ShiftDTO;
import com.gamingcenter.entity.Session;
import com.gamingcenter.entity.Shift;
import com.gamingcenter.exception.BusinessException;
import com.gamingcenter.exception.ResourceNotFoundException;
import com.gamingcenter.repository.SessionRepository;
import com.gamingcenter.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final SessionRepository sessionRepository;

    public ShiftDTO openShift() {
        if (shiftRepository.findByActiveTrue().isPresent()) {
            throw new BusinessException("Un shift est déjà en cours");
        }

        Shift shift = Shift.builder()
                .openedAt(LocalDateTime.now())
                .active(true)
                .sessionRevenue(0.0)
                .buffetRevenue(0.0)
                .totalExpenses(0.0)
                .build();

        return toDTO(shiftRepository.save(shift));
    }

    public ShiftDTO closeShift() {
        Shift shift = shiftRepository.findByActiveTrue()
                .orElseThrow(() -> new BusinessException("Aucun shift en cours"));

        // Calculate session revenue from all sessions in this shift
        double sessionRevenue = sessionRepository.findByShiftId(shift.getId()).stream()
                .filter(s -> s.getAmount() != null)
                .mapToDouble(Session::getAmount)
                .sum();

        shift.setSessionRevenue(sessionRevenue);
        shift.setClosedAt(LocalDateTime.now());
        shift.setActive(false);

        return toDTO(shiftRepository.save(shift));
    }

    public ShiftDTO getCurrentShift() {
        return shiftRepository.findByActiveTrue()
                .map(this::toDTO)
                .orElse(null);
    }

    public List<ShiftDTO> getShiftHistory() {
        return shiftRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    private ShiftDTO toDTO(Shift shift) {
        return new ShiftDTO(
                shift.getId(),
                shift.getOpenedAt(),
                shift.getClosedAt(),
                shift.getActive(),
                shift.getSessionRevenue(),
                shift.getBuffetRevenue(),
                shift.getTotalExpenses(),
                shift.getNetProfit()
        );
    }
}
