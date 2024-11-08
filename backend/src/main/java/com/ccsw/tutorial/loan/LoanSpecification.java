package com.ccsw.tutorial.loan;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.loan.model.Loan;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
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
        if (criteria.getOperation().equalsIgnoreCase(":") && criteria.getValue() != null) {
            Path<?> path = getPath(root);
            return builder.equal(path, criteria.getValue());
        }

        if (criteria.getOperation().equalsIgnoreCase("conflict") && criteria.getValue() instanceof LocalDate[]) {
            LocalDate[] dates = (LocalDate[]) criteria.getValue();
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];

            // Conflicto de fechas para el mismo juego o cliente según la clave
            return builder.and(builder.equal(
                    criteria.getKey().equals("client") ? root.get("client").get("id") : root.get("game").get("id"),
                    criteria.getKey().equals("client") ? root.get("client").get("id") : root.get("game").get("id")),
                    builder.or(builder.between(root.get("startDate"), startDate, endDate),
                            builder.between(root.get("endDate"), startDate, endDate),
                            builder.and(builder.lessThanOrEqualTo(root.get("startDate"), startDate),
                                    builder.greaterThanOrEqualTo(root.get("endDate"), endDate))));
        }

        if (criteria.getOperation().equalsIgnoreCase("dateRange") && criteria.getValue() instanceof LocalDate) {
            LocalDate searchDate = (LocalDate) criteria.getValue();
            return builder.or(builder.equal(root.get("startDate"), searchDate),
                    builder.equal(root.get("endDate"), searchDate),
                    builder.between(builder.literal(searchDate), root.get("startDate"), root.get("endDate")));
        }

        return null;
    }

    private Path<?> getPath(Root<Loan> root) {
        String[] split = criteria.getKey().split("\\.");
        Path<?> path = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            path = path.get(split[i]);
        }
        return path;
    }
}
