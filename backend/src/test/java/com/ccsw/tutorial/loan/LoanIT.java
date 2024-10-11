package com.ccsw.tutorial.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

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
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final ParameterizedTypeReference<List<LoanDto>> responseType = new ParameterizedTypeReference<List<LoanDto>>() {
    };

    @Test
    public void findAllShouldReturnAllLoans() {
        ResponseEntity<List<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET,
                null, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Método actualizado
    }

    @Test
    public void createLoanShouldCreateNewLoan() {
        LoanDto loanDto = new LoanDto();

        // Configurar el cliente
        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setName("Client1");
        loanDto.setClient(clientDto);

        // Configurar el juego
        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        gameDto.setTitle("Game1");
        loanDto.setGame(gameDto);

        // Configurar las fechas del préstamo
        loanDto.setStartDate(LocalDate.now());
        loanDto.setEndDate(LocalDate.now().plusDays(7));

        // Realizar la solicitud PUT para crear el préstamo
        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT,
                new HttpEntity<>(loanDto), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
