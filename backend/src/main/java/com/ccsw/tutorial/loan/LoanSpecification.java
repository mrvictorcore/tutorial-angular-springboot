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
        // Filtro por ID o comparación exacta
        if (criteria.getOperation().equalsIgnoreCase(":") && criteria.getValue() != null) {
            Path<String> path = getPath(root);

            if (path.getJavaType() == String.class) {
                return builder.like(path, "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(path, criteria.getValue());
            }
        }

        // Verificación de solapamiento de fechas (para evitar conflictos en nuevos
        // préstamos)
        if (criteria.getOperation().equalsIgnoreCase("conflict") && criteria.getValue() != null) {
            LocalDate[] dates = (LocalDate[]) criteria.getValue();
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];

            // Verificar solapamiento de fechas
            return builder.and(builder.equal(root.get("game").get("id"), criteria.getKey()), // Mismo juego
                    builder.or(builder.between(root.get("startDate"), startDate, endDate),
                            builder.between(root.get("endDate"), startDate, endDate),
                            builder.and(builder.lessThanOrEqualTo(root.get("startDate"), startDate),
                                    builder.greaterThanOrEqualTo(root.get("endDate"), endDate))));
        }

        // Filtro de rango de fechas (para buscar préstamos activos en una fecha
        // específica)
        if (criteria.getOperation().equalsIgnoreCase("dateRange") && criteria.getValue() != null) {
            LocalDate searchDate = (LocalDate) criteria.getValue();

            // Verificar si la fecha de búsqueda coincide con las fechas de inicio, fin o
            // está entre ellas
            return builder.or(builder.equal(root.get("startDate"), searchDate), // Coincide con la fecha de inicio
                    builder.equal(root.get("endDate"), searchDate), // Coincide con la fecha de fin
                    builder.between(builder.literal(searchDate), root.get("startDate"), root.get("endDate")) // Entre
                                                                                                             // inicio y
                                                                                                             // fin
            );
        }

        return null;
    }

    // Método auxiliar para obtener el Path del criterio
    private Path<String> getPath(Root<Loan> root) {
        String key = criteria.getKey();
        String[] split = key.split("\\.");

        Path<String> expression = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            expression = expression.get(split[i]);
        }

        return expression;
    }
}
