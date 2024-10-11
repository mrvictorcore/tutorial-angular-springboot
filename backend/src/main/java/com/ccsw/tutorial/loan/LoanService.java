package com.ccsw.tutorial.loan;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

/**
 * @author ccsw
 *
 */
public interface LoanService {

    List<Loan> findAll();

    void save(Long id, LoanDto loanDto);

    void delete(Long id);

    boolean validateLoan(LoanDto loanDto);

    Page<Loan> findPage(LoanSearchDto dto);
}
