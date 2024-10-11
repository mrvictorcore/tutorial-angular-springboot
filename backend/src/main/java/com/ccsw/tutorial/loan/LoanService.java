package com.ccsw.tutorial.loan;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;

/**
 * @author ccsw
 *
 */
public interface LoanService {

    List<Loan> findAll();

    Page<Loan> findPaginated(Pageable pageable);

    void save(Long id, LoanDto loanDto);

    void delete(Long id);

    boolean validateLoan(LoanDto loanDto);
}
