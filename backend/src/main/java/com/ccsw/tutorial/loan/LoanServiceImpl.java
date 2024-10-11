package com.ccsw.tutorial.loan;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;

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
    public Page<Loan> findPaginated(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }

    @Override
    public void save(Long id, LoanDto loanDto) {
        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
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
        List<Loan> conflictingLoans = loanRepository.findConflictingLoans(loanDto.getGame().getId(),
                loanDto.getStartDate(), loanDto.getEndDate());

        return conflictingLoans.isEmpty();
    }
}
