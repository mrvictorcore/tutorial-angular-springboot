package com.ccsw.tutorial.loan;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author ccsw
 *
 */
@Tag(name = "Loan", description = "API of Loan")
@RequestMapping(value = "/loan")
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LoanController {

    @Autowired
    LoanService loanService;

    @Autowired
    ModelMapper mapper;

    @Operation(summary = "Find Page", description = "Method that return a page of Loans")
    @RequestMapping(path = "/paginated", method = RequestMethod.POST)
    public Page<LoanDto> findPage(@RequestBody LoanSearchDto dto) {
        Page<Loan> page = this.loanService.findPage(dto);
        return new PageImpl<>(
                page.getContent().stream().map(e -> mapper.map(e, LoanDto.class)).collect(Collectors.toList()),
                page.getPageable(), page.getTotalElements());
    }

    /**
     * Método para crear o actualizar un préstamo (Loan).
     *
     * @param id      PK del préstamo (opcional).
     * @param loanDto Datos del préstamo.
     */
    @Operation(summary = "Save or Update Loan", description = "Method to save or update a Loan")
    @RequestMapping(path = { "", "/{id}" }, method = { RequestMethod.POST, RequestMethod.PUT })
    public void saveLoan(@PathVariable(name = "id", required = false) Long id, @RequestBody LoanDto loanDto) {
        loanService.save(id, loanDto);
    }

    @Operation(summary = "Delete Loan", description = "Method to delete a Loan")
    @DeleteMapping("/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.delete(id);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateLoan(@RequestBody LoanDto loanDto) {
        boolean isValid = loanService.validateLoan(loanDto);
        return ResponseEntity.ok(isValid);
    }
}
