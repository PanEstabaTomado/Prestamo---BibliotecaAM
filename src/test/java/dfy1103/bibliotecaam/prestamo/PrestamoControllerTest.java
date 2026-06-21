package dfy1103.bibliotecaam.prestamo;

import dfy1103.bibliotecaam.prestamo.assembler.PrestamoModelAssembler;
import dfy1103.bibliotecaam.prestamo.controller.PrestamoController;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoRequestDTO;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import dfy1103.bibliotecaam.prestamo.service.PrestamoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrestamoController.class)
@ActiveProfiles("test")
@DisplayName("Tests Unitarios - PrestamoController")
class PrestamoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrestamoService prestamoService;

    @MockitoBean
    private PrestamoModelAssembler assembler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private PrestamoResponseDTO p1;
    private PrestamoResponseDTO p2;

    @BeforeEach
    void setUp() {
        // Inicialización usando los DTOs reales de Préstamos
        p1 = new PrestamoResponseDTO(1L, LocalDate.now(), LocalDate.now().plusDays(7), "No Devuelto", 100L, 500L);
        p2 = new PrestamoResponseDTO(2L, LocalDate.now(), LocalDate.now().plusDays(7), "Devuelto", 101L, 501L);

        // Mockear el comportamiento del Assembler HATEOAS para envolver las respuestas
        Mockito.when(assembler.toModel(eq(p1))).thenReturn(
                EntityModel.of(p1, linkTo(methodOn(PrestamoController.class).obtenerPorId(1L)).withSelfRel())
        );
        Mockito.when(assembler.toModel(eq(p2))).thenReturn(
                EntityModel.of(p2, linkTo(methodOn(PrestamoController.class).obtenerPorId(2L)).withSelfRel())
        );
    }

    @Test
    @DisplayName("GIVEN: Existen prestamos WHEN: GET /api/bibliotecaam/prestamo THEN: Retorna 200 OK y HAL-JSON con links")
    void shouldReturnAllPrestamos() throws Exception {
        List<PrestamoResponseDTO> lista = Arrays.asList(p1, p2);
        Mockito.when(prestamoService.obtenerTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/bibliotecaam/prestamo")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.prestamoResponseDTOList[0].idPresta").value(1L))
                .andExpect(jsonPath("$._embedded.prestamoResponseDTOList[0].devuelto").value("No Devuelto"))
                .andExpect(jsonPath("$._embedded.prestamoResponseDTOList[1].idPresta").value(2L))
                .andExpect(jsonPath("$._embedded.prestamoResponseDTOList[1].usuarioId").value(101L))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GIVEN: Préstamos atrasados WHEN: GET /api/bibliotecaam/prestamo/atrasados THEN: Retorna la colección en formato HAL-JSON")
    void shouldReturnPrestamosAtrasados() throws Exception {
        Mockito.when(prestamoService.obtenerLibrosAtrasados()).thenReturn(Arrays.asList(p1));

        mockMvc.perform(get("/api/bibliotecaam/prestamo/atrasados")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prestamoResponseDTOList.length()").value(1))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/bibliotecaam/prestamo/atrasados"));
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: GET /api/bibliotecaam/prestamo/{id} THEN: Retorna el modelo del préstamo solicitado")
    void shouldReturnPrestamoById() throws Exception {
        Long id = 1L;
        Mockito.when(prestamoService.obtenerPorId(id)).thenReturn(Optional.of(p1));

        mockMvc.perform(get("/api/bibliotecaam/prestamo/{id}", id)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPresta").value(id))
                .andExpect(jsonPath("$.idLibro").value(500L))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: GET /api/bibliotecaam/prestamo/{id} THEN: Retorna 404 Not Found")
    void shouldReturnNotFoundWhenPrestamoDoesNotExist() throws Exception {
        Long id = 99L;
        Mockito.when(prestamoService.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bibliotecaam/prestamo/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GIVEN: Request válido WHEN: POST /api/bibliotecaam/prestamo THEN: Guarda y retorna 201 Created")
    void shouldCreatePrestamo() throws Exception {
        PrestamoRequestDTO request = new PrestamoRequestDTO(LocalDate.now(), LocalDate.now().plusDays(7), false, 100L, 500L);
        Mockito.when(prestamoService.guardar(any(PrestamoRequestDTO.class))).thenReturn(p1);

        mockMvc.perform(post("/api/bibliotecaam/prestamo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPresta").value(1L))
                .andExpect(jsonPath("$.devuelto").value("No Devuelto"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GIVEN: ID y Request válidos WHEN: PUT /api/bibliotecaam/prestamo/{id} THEN: Modifica exitosamente")
    void shouldUpdatePrestamo() throws Exception {
        Long id = 1L;
        PrestamoRequestDTO request = new PrestamoRequestDTO(LocalDate.now(), LocalDate.now().plusDays(7), true, 100L, 500L);
        Mockito.when(prestamoService.actualizar(eq(id), any(PrestamoRequestDTO.class))).thenReturn(Optional.of(p1));

        mockMvc.perform(put("/api/bibliotecaam/prestamo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPresta").value(id));
    }

    @Test
    @DisplayName("GIVEN: ID existente WHEN: DELETE /api/bibliotecaam/prestamo/{id} THEN: Borra y retorna Map con éxito")
    void shouldDeletePrestamoSuccessfully() throws Exception {
        Long id = 1L;
        Mockito.when(prestamoService.obtenerPorId(id)).thenReturn(Optional.of(p1));
        Mockito.doNothing().when(prestamoService).eliminar(id);

        mockMvc.perform(delete("/api/bibliotecaam/prestamo/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.['¡EXITO! ']").value("¡El prestamo fue eliminado con exito!"));
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: DELETE /api/bibliotecaam/prestamo/{id} THEN: Retorna Map con mensaje de error")
    void shouldReturnErrorWhenDeletingNonExistentPrestamo() throws Exception {
        Long id = 99L;
        Mockito.when(prestamoService.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/bibliotecaam/prestamo/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.['¡ERROR! ']").value("¡El prestamo con id 99 no fue encontrado!"));
    }
}