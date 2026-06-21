package dfy1103.bibliotecaam.prestamo;

import dfy1103.bibliotecaam.prestamo.dto.PrestamoRequestDTO;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import dfy1103.bibliotecaam.prestamo.repository.PrestamoRepository;
import dfy1103.bibliotecaam.prestamo.service.PrestamoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = PrestamoService.class)
@ActiveProfiles("test")
@DisplayName("Tests Unitarios - PrestamoService")
class PrestamoServiceTest {

    @Autowired
    private PrestamoService prestamoService;

    @MockitoBean
    private PrestamoRepository prestamoRepository;

    @MockitoBean(name = "webClientUsuario")
    private WebClient webClientUsuario;

    @MockitoBean(name = "webClientLibro")
    private WebClient webClientLibro;

    // Mocks genéricos para simular la interfaz fluida de WebClient
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientSuccess(WebClient webClientMock, String uri, Long id) {
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(eq(uri), eq(id))).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just("OK"));
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientException(WebClient webClientMock, String uri, Long id, Throwable exception) {
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(eq(uri), eq(id))).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(exception));
    }

    @Test
    @DisplayName("GIVEN: Existen prestamos WHEN: obtenerTodos THEN: Retorna la lista mapeada con texto devuelto correcto")
    void shouldReturnAllPrestamos() {
        List<Prestamo> mockList = Arrays.asList(
                new Prestamo(1L, LocalDate.now(), LocalDate.now().plusDays(5), true, 10L, 50L),
                new Prestamo(2L, LocalDate.now(), LocalDate.now().plusDays(5), false, 11L, 51L)
        );
        Mockito.when(prestamoRepository.findAll()).thenReturn(mockList);

        List<PrestamoResponseDTO> resultado = prestamoService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Libro devuelto", resultado.get(0).getDevuelto());
        assertEquals("Libro no devuelto", resultado.get(1).getDevuelto());
    }

    @Test
    @DisplayName("GIVEN: Existe prestamo WHEN: obtenerPorId THEN: Retorna el Optional con el DTO correspondiente")
    void shouldReturnPrestamoById() {
        Long id = 1L;
        Prestamo prestamo = new Prestamo(id, LocalDate.now(), LocalDate.now().plusDays(5), false, 10L, 50L);
        Mockito.when(prestamoRepository.findById(id)).thenReturn(Optional.of(prestamo));

        Optional<PrestamoResponseDTO> resultado = prestamoService.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("Libro no devuelto", resultado.get().getDevuelto());
        assertEquals(10L, resultado.get().getUsuarioId());
    }

    @Test
    @DisplayName("GIVEN: Datos válidos WHEN: guardar THEN: Valida remotamente y almacena el registro")
    void shouldSavePrestamoSuccessfully() {
        PrestamoRequestDTO request = new PrestamoRequestDTO(LocalDate.now(), LocalDate.now().plusDays(5), false, 10L, 50L);
        Prestamo prestamoGuardado = new Prestamo(1L, LocalDate.now(), LocalDate.now().plusDays(5), false, 10L, 50L);

        mockWebClientSuccess(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 10L);
        mockWebClientSuccess(webClientLibro, "/api/bibliotecaam/libro/{id}", 50L);
        Mockito.when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoGuardado);

        PrestamoResponseDTO resultado = prestamoService.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPresta());
        assertEquals("Libro no devuelto", resultado.getDevuelto());
        Mockito.verify(prestamoRepository, Mockito.times(1)).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("GIVEN: Usuario inexistente WHEN: guardar THEN: Falla validación de usuario y lanza RuntimeException sin guardar")
    void shouldThrowExceptionWhenUsuarioNotFound() {
        PrestamoRequestDTO request = new PrestamoRequestDTO(LocalDate.now(), LocalDate.now().plusDays(5), false, 99L, 50L);

        WebClientResponseException notFoundException = Mockito.mock(WebClientResponseException.NotFound.class);
        mockWebClientException(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 99L, notFoundException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> prestamoService.guardar(request));
        assertTrue(exception.getMessage().contains("El Usuario con id 99 no existe"));
        Mockito.verify(prestamoRepository, Mockito.never()).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("GIVEN: Libro inexistente WHEN: guardar THEN: Pasa usuario, falla en libro y lanza RuntimeException")
    void shouldThrowExceptionWhenLibroNotFound() {
        PrestamoRequestDTO request = new PrestamoRequestDTO(LocalDate.now(), LocalDate.now().plusDays(5), false, 10L, 99L);

        mockWebClientSuccess(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 10L);
        WebClientResponseException notFoundException = Mockito.mock(WebClientResponseException.NotFound.class);
        mockWebClientException(webClientLibro, "/api/bibliotecaam/libro/{id}", 99L, notFoundException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> prestamoService.guardar(request));
        assertTrue(exception.getMessage().contains("El Libro con id 99 no existe"));
        Mockito.verify(prestamoRepository, Mockito.never()).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("GIVEN: ID y Request válidos WHEN: actualizar THEN: Actualiza los campos del registro existente")
    void shouldUpdatePrestamoSuccessfully() {
        Long id = 1L;
        Prestamo existente = new Prestamo(id, LocalDate.now(), LocalDate.now().plusDays(5), false, 10L, 50L);
        PrestamoRequestDTO request = new PrestamoRequestDTO(LocalDate.now(), LocalDate.now().plusDays(10), true, 10L, 50L);
        Prestamo modificado = new Prestamo(id, LocalDate.now(), LocalDate.now().plusDays(10), true, 10L, 50L);

        Mockito.when(prestamoRepository.findById(id)).thenReturn(Optional.of(existente));
        mockWebClientSuccess(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 10L);
        mockWebClientSuccess(webClientLibro, "/api/bibliotecaam/libro/{id}", 50L);
        Mockito.when(prestamoRepository.save(any(Prestamo.class))).thenReturn(modificado);

        Optional<PrestamoResponseDTO> resultado = prestamoService.actualizar(id, request);

        assertTrue(resultado.isPresent());
        assertEquals("Libro devuelto", resultado.get().getDevuelto());
        assertEquals(modificado.getFechaVencPresta(), resultado.get().getFechaVencPresta());
    }

    @Test
    @DisplayName("GIVEN: ID de préstamo WHEN: eliminar THEN: Invoca al repositorio de borrado")
    void shouldDeletePrestamo() {
        Long id = 1L;
        Mockito.doNothing().when(prestamoRepository).deleteById(id);

        assertDoesNotThrow(() -> prestamoService.eliminar(id));
        Mockito.verify(prestamoRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("GIVEN: Préstamos vencidos WHEN: obtenerLibrosAtrasados THEN: Retorna lista filtrada del repositorio")
    void shouldReturnLibrosAtrasados() {
        List<Prestamo> mockAtrasados = Arrays.asList(
                new Prestamo(5L, LocalDate.now().minusDays(10), LocalDate.now().minusDays(3), false, 12L, 60L)
        );
        Mockito.when(prestamoRepository.obtenerLibrosPorDevolver()).thenReturn(mockAtrasados);

        List<PrestamoResponseDTO> resultado = prestamoService.obtenerLibrosAtrasados();

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdPresta());
        assertEquals("Libro no devuelto", resultado.get(0).getDevuelto());
    }

    @Test
    @DisplayName("GIVEN: ID de usuario WHEN: obtenerPorIdUsuario THEN: Filtra los préstamos de ese usuario")
    void shouldReturnPrestamosByUsuarioId() {
        Long idUsuario = 15L;
        List<Prestamo> mockUsuarioList = Arrays.asList(
                new Prestamo(1L, LocalDate.now(), LocalDate.now().plusDays(7), false, idUsuario, 70L)
        );
        Mockito.when(prestamoRepository.findByUsuarioId(idUsuario)).thenReturn(mockUsuarioList);

        List<PrestamoResponseDTO> resultado = prestamoService.obtenerPorIdUsuario(idUsuario);

        assertEquals(1, resultado.size());
        assertEquals(idUsuario, resultado.get(0).getUsuarioId());
    }
}