package com.ccsw.tutorial.loan;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.exceptions.ApplicationException;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    GameService gameService;

    @Autowired
    ModelMapper mapper;

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {
        return this.loanRepository.findAll(dto.getPageable().getPageable());
    }

    @Override
    public void save(Long id, LoanDto loanDto) {
        validateLoanDto(loanDto);

        System.out.println("LoanDto recibido: " + loanDto);

        Loan loan;
        if (id == null) {
            loan = new Loan();
        } else {
            loan = loanRepository.findById(id).orElseThrow(() -> new ApplicationException("Préstamo no encontrado"));
        }

        try {
            loan.setClient(clientService.get(loanDto.getClient().getId()));
            loan.setGame(gameService.get(loanDto.getGame().getId()));
        } catch (Exception e) {
            throw new ApplicationException("Error al mapear LoanDto a Loan: " + e.getMessage(), e);
        }
        loan.setStartDate(loanDto.getStartDate());
        loan.setEndDate(loanDto.getEndDate());

        loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) {
        loanRepository.deleteById(id);
    }

    @Override
    public boolean validateLoan(LoanDto loanDto) {
        LocalDate startDate = loanDto.getStartDate();
        LocalDate endDate = loanDto.getEndDate();

        if (endDate.isBefore(startDate)) {
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (startDate.plusDays(14).isBefore(endDate)) {
            throw new ApplicationException("El periodo de préstamo no puede exceder los 14 días");
        }

        // Validación de conflicto para el juego
        Specification<Loan> gameConflictSpec = Specification.where(
                new LoanSpecification(new SearchCriteria("game", "conflict", new LocalDate[] { startDate, endDate })));
        List<Loan> conflictingGameLoans = loanRepository.findAll(gameConflictSpec).stream()
                .filter(loan -> loan.getGame().getId().equals(loanDto.getGame().getId())).collect(Collectors.toList());

        if (!conflictingGameLoans.isEmpty()) {
            throw new ApplicationException("El juego ya está prestado en las fechas seleccionadas");
        }

        // Validación de conflicto para el cliente, máximo 2 juegos en fechas solapadas
        Specification<Loan> clientConflictSpec = Specification.where(new LoanSpecification(
                new SearchCriteria("client", "conflict", new LocalDate[] { startDate, endDate })));
        List<Loan> clientLoans = loanRepository.findAll(clientConflictSpec).stream()
                .filter(loan -> loan.getClient().getId().equals(loanDto.getClient().getId()))
                .collect(Collectors.toList());

        if (clientLoans.size() >= 2) {
            throw new ApplicationException("El cliente ya tiene dos juegos prestados en las fechas seleccionadas");
        }

        return true;
    }

    @Override
    public Page<Loan> findLoansFiltered(LoanSearchDto dto) {
        Specification<Loan> spec = Specification.where(null);

        if (dto.getGameId() != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("game.id", ":", dto.getGameId())));
        }

        if (dto.getClientId() != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("client.id", ":", dto.getClientId())));
        }

        if (dto.getSearchDate() != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("startDate", "dateRange", dto.getSearchDate())));
        }

        return loanRepository.findAll(spec, dto.getPageable().getPageable());
    }

    private void validateLoanDto(LoanDto loanDto) {
        if (loanDto.getClient() == null || loanDto.getClient().getId() == null) {
            throw new ApplicationException("El cliente es obligatorio");
        }

        if (loanDto.getGame() == null || loanDto.getGame().getId() == null) {
            throw new ApplicationException("El juego es obligatorio");
        }

        if (loanDto.getStartDate() == null) {
            throw new ApplicationException("La fecha de inicio es obligatoria");
        }

        if (loanDto.getEndDate() == null) {
            throw new ApplicationException("La fecha de fin es obligatoria");
        }

        if (loanDto.getEndDate().isBefore(loanDto.getStartDate())) {
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (loanDto.getStartDate().isBefore(LocalDate.now())) {
            throw new ApplicationException("No se puede crear un préstamo para una fecha pasada");
        }

        if (!validateLoan(loanDto)) {
            throw new ApplicationException("El juego ya está prestado en las fechas seleccionadas");
        }
    }
}
