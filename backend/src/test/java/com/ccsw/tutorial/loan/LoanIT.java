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
import org.springframework.data.domain.PageImpl;
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

    // Se espera una respuesta de tipo PageImpl
    private static final ParameterizedTypeReference<PageImpl<LoanDto>> responseType = new ParameterizedTypeReference<PageImpl<LoanDto>>() {
    };

    @Test
    public void findAllShouldReturnAllLoans() {
        // Realizar la petición al servidor
        ResponseEntity<PageImpl<LoanDto>> response = restTemplate
                .exchange(LOCALHOST + port + SERVICE_PATH + "/paginated", HttpMethod.POST, null, responseType);

        // Verificar que la respuesta no es nula
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verificar el contenido de la página
        PageImpl<LoanDto> loansPage = response.getBody();
        assertNotNull(loansPage);
        assertEquals(5, loansPage.getTotalElements()); // Ajusta según lo esperado en tus datos de prueba
    }

    @Test
    public void createLoanShouldCreateNewLoan() {
        // Crear el préstamo (LoanDto)
        LoanDto loanDto = new LoanDto();

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setName("Client1");
        loanDto.setClient(clientDto);

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        gameDto.setTitle("Game1");
        loanDto.setGame(gameDto);

        loanDto.setStartDate(LocalDate.now());
        loanDto.setEndDate(LocalDate.now().plusDays(7));

        // Realizar la petición de creación de préstamo (usamos POST en lugar de PUT)
        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST,
                new HttpEntity<>(loanDto), Void.class);

        // Verificar que la creación fue exitosa
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
