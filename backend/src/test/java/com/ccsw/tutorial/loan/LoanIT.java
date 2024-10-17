package com.ccsw.tutorial.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    public static final Long EXISTS_CLIENT_ID = 1L;
    public static final Long EXISTS_GAME_ID = 1L;
    public static final Long NOT_EXISTS_LOAN_ID = 999L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    @Test
    public void findAllShouldReturnAllLoans() {
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, 5));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/paginated", HttpMethod.POST, new HttpEntity<>(searchDto),
                responseTypePage);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getTotalElements());
    }

    @Test
    public void createLoanShouldCreateNewLoan() {
        LoanDto loanDto = createValidLoanDto();

        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST,
                new HttpEntity<>(loanDto), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateLoanShouldUpdateExistingLoan() {
        LoanDto loanDto = createValidLoanDto();
        loanDto.setEndDate(LocalDate.now().plusDays(10));

        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(loanDto), Void.class, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateNonExistentLoanShouldReturnError() {
        LoanDto loanDto = createValidLoanDto();

        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(loanDto), Void.class, NOT_EXISTS_LOAN_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void deleteLoanShouldDeleteExistingLoan() {
        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/{id}",
                HttpMethod.DELETE, null, Void.class, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private LoanDto createValidLoanDto() {
        LoanDto loanDto = new LoanDto();

        ClientDto clientDto = new ClientDto();
        clientDto.setId(EXISTS_CLIENT_ID);
        loanDto.setClient(clientDto);

        GameDto gameDto = new GameDto();
        gameDto.setId(EXISTS_GAME_ID);
        loanDto.setGame(gameDto);

        loanDto.setStartDate(LocalDate.now());
        loanDto.setEndDate(LocalDate.now().plusDays(7));

        return loanDto;
    }
}
