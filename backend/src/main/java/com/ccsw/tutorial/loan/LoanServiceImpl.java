package com.ccsw.tutorial.loan;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

/**
 * @author ccsw
 *
 */
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
        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        }

        if (loanDto.getClient() == null || loanDto.getClient().getId() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        if (loanDto.getGame() == null || loanDto.getGame().getId() == null) {
            throw new RuntimeException("El juego es obligatorio");
        }

        if (loanDto.getStartDate() == null) {
            throw new RuntimeException("La fecha de inicio es obligatoria");
        }

        if (loanDto.getEndDate() == null) {
            throw new RuntimeException("La fecha de fin es obligatoria");
        }

        if (loanDto.getEndDate().isBefore(loanDto.getStartDate())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        loan.setClient(clientService.get(loanDto.getClient().getId()));
        loan.setGame(gameService.get(loanDto.getGame().getId()));

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

        // Crear especificación para buscar solapamientos
        LoanSpecification conflictSpec = new LoanSpecification(
                new SearchCriteria(loanDto.getGame().getId(), "conflict", new LocalDate[] { startDate, endDate }));

        // Buscar préstamos conflictivos
        List<Loan> conflictingLoans = loanRepository.findAll(Specification.where(conflictSpec));

        // Si no hay préstamos conflictivos, la validación es exitosa
        return conflictingLoans.isEmpty();
    }

    @Override
    public Page<Loan> findLoansFiltered(LoanSearchDto dto) {
        if (dto.getPageable() == null) {
            dto.setPageable(new PageableRequest(0, 10));
        }

        LoanSpecification gameSpec = new LoanSpecification(new SearchCriteria("game.id", ":", dto.getGameId()));
        LoanSpecification clientSpec = new LoanSpecification(new SearchCriteria("client.id", ":", dto.getClientId()));
        LoanSpecification dateSpec = new LoanSpecification(
                new SearchCriteria("searchDate", "dateRange", dto.getSearchDate()));

        Specification<Loan> spec = Specification.where(gameSpec).and(clientSpec).and(dateSpec);

        return loanRepository.findAll(spec, dto.getPageable().getPageable());
    }
}
