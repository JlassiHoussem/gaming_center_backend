package com.gamingcenter.service;

import com.gamingcenter.dto.ExpenseDTO;
import com.gamingcenter.entity.Expense;
import com.gamingcenter.entity.Shift;
import com.gamingcenter.exception.ResourceNotFoundException;
import com.gamingcenter.repository.ExpenseRepository;
import com.gamingcenter.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ShiftRepository shiftRepository;

    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ExpenseDTO> getExpensesByShift(Long shiftId) {
        return expenseRepository.findByShiftId(shiftId).stream()
                .map(this::toDTO)
                .toList();
    }

    public ExpenseDTO createExpense(ExpenseDTO dto) {
        Expense expense = Expense.builder()
                .description(dto.description())
                .amount(dto.amount())
                .build();

        if (dto.shiftId() != null) {
            Shift shift = shiftRepository.findById(dto.shiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift non trouvé: " + dto.shiftId()));
            expense.setShift(shift);
        } else {
            shiftRepository.findByActiveTrue().ifPresent(expense::setShift);
        }

        expense = expenseRepository.save(expense);

        // Update shift total
        if (expense.getShift() != null) {
            Shift shift = expense.getShift();
            shift.setTotalExpenses(shift.getTotalExpenses() + expense.getAmount());
            shiftRepository.save(shift);
        }

        return toDTO(expense);
    }

    public ExpenseDTO getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense non trouvée: " + id));
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO dto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense non trouvée: " + id));

        double oldAmount = expense.getAmount();
        expense.setDescription(dto.description());
        expense.setAmount(dto.amount());

        // Update shift total if amount changed
        if (expense.getShift() != null && Double.compare(oldAmount, dto.amount()) != 0) {
            Shift shift = expense.getShift();
            shift.setTotalExpenses(shift.getTotalExpenses() - oldAmount + dto.amount());
            shiftRepository.save(shift);
        }

        expense = expenseRepository.save(expense);
        return toDTO(expense);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense non trouvée: " + id));

        // Update shift total
        if (expense.getShift() != null) {
            Shift shift = expense.getShift();
            shift.setTotalExpenses(Math.max(0, shift.getTotalExpenses() - expense.getAmount()));
            shiftRepository.save(shift);
        }

        expenseRepository.deleteById(id);
    }

    private ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getShift() != null ? expense.getShift().getId() : null
        );
    }
}
