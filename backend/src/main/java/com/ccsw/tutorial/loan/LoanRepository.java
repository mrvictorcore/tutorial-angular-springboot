package com.ccsw.tutorial.loan;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ccsw.tutorial.loan.model.Loan;

/**
 * @author ccsw
 *
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l WHERE l.game.id = :gameId AND (" + "(:startDate BETWEEN l.startDate AND l.endDate OR "
            + ":endDate BETWEEN l.startDate AND l.endDate) OR " + "(l.startDate BETWEEN :startDate AND :endDate))")
    List<Loan> findConflictingLoans(@Param("gameId") Long gameId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
