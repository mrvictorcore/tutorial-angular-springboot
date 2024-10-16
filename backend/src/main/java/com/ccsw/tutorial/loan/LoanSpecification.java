package com.ccsw.tutorial.loan;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.loan.model.Loan;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class LoanSpecification implements Specification<Loan> {

    private static final long serialVersionUID = 1L;

    private final SearchCriteria criteria;

    public LoanSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Loan> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria.getOperation().equalsIgnoreCase("conflict") && criteria.getValue() != null) {
            LocalDate[] dates = (LocalDate[]) criteria.getValue();
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];

            // Verificar solapamiento de fechas
            return builder.and(builder.equal(root.get("game").get("id"), criteria.getKey()), // Verificamos el ID del
                                                                                             // juego
                    builder.or(builder.between(root.get("startDate"), startDate, endDate), // El préstamo existente
                                                                                           // comienza entre las fechas
                                                                                           // seleccionadas
                            builder.between(root.get("endDate"), startDate, endDate), // El préstamo existente termina
                                                                                      // entre las fechas seleccionadas
                            builder.and(builder.lessThanOrEqualTo(root.get("startDate"), startDate), // El préstamo
                                                                                                     // existente
                                                                                                     // engloba
                                                                                                     // completamente
                                                                                                     // las fechas
                                                                                                     // seleccionadas
                                    builder.greaterThanOrEqualTo(root.get("endDate"), endDate))));
        }
        return null;
    }
}
