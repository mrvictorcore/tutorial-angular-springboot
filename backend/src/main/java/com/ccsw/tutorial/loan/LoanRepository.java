package com.ccsw.tutorial.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ccsw.tutorial.loan.model.Loan;

/**
 * @author ccsw
 *
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);
}
