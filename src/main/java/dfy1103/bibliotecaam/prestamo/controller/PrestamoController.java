package dfy1103.bibliotecaam.prestamo.controller;

import dfy1103.bibliotecaam.prestamo.dto.PrestamoRequestDTO;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import dfy1103.bibliotecaam.prestamo.service.PrestamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bibliotecaam/prestamo")
@RequiredArgsConstructor
@Tag(name = "Prestamos", description = "Operaciones asociadas a prestamos.")
public class PrestamoController {
    private final PrestamoService prestamoService;


    @GetMapping
    @Operation(summary = "Obtener todos los prestamos", description = "Obtiene una lista de todos los prestamos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado")
    })
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerTodos(){
        return ResponseEntity.ok(prestamoService.obtenerTodos());
    }

    @GetMapping("/atrasados")
    @Operation(summary = "Obtener todos los prestamos atrasados", description = "Obtiene una lista de todos los prestamos atrasados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado")
    })
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerLibrosAtrasados(){
        return ResponseEntity.ok(prestamoService.obtenerLibrosAtrasados());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener prestamo por id", description = "Obtiene prestamo por id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado")
    })
    public ResponseEntity<PrestamoResponseDTO> obtenerPorId(@PathVariable Long id){

        return prestamoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener prestamo por usuario", description = "Obtiene una lista de prestamo por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado")
    })
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerUsuarioId(Long id){
        return ResponseEntity.ok(prestamoService.obtenerPorIdUsuario(id));
    }

    @PostMapping
    @Operation(summary = "Guardar un prestamo", description = "Guarda un prestamo acorde a lo ingresado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa."),
            @ApiResponse(responseCode = "400", description = "Error al ingresar parametros. Revise si ingreso todos los parametros solicitados."),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para hacer el cambio.")
    })
    public ResponseEntity<PrestamoResponseDTO> guardar(@Valid @RequestBody PrestamoRequestDTO doto){
            return ResponseEntity.status(201).body(prestamoService.guardar(doto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar prestamo", description = "Actualiza un prestamo acorde a una id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prestamo actualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Prestamo.class))),
            @ApiResponse(responseCode = "404", description = "El id del prestamo no existe.")
    })
    public ResponseEntity<PrestamoResponseDTO> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody PrestamoRequestDTO doto) {
        return prestamoService.actualizar(id, doto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar prestamo", description = "Elimina un prestamo acorde a una id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "¡Prestamo eliminado con exito!"),
            @ApiResponse(responseCode = "404",description = "ERROR: ¡El id del prestamo ingresado no existe!")
    })
    public ResponseEntity<Map<String,String>> borrar(@PathVariable Long id){
        if (prestamoService.obtenerPorId(id).isEmpty()){
            Map<String, String> borrado = new LinkedHashMap<>();
            borrado.put("¡ERROR! ", "¡El prestamo con id "+id+" no fue encontrado!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
        }else {
            prestamoService.eliminar(id);
            Map<String, String> borrado = new LinkedHashMap<>();
            borrado.put("¡EXITO! ", "¡El prestamo fue eliminado con exito!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
        }
    }


}
