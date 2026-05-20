package dfy1103.bibliotecaam.prestamo.controller;

import dfy1103.bibliotecaam.prestamo.dto.PrestamoRequestDTO;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import dfy1103.bibliotecaam.prestamo.service.PrestamoService;
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
public class PrestamoController {
    private final PrestamoService prestamoService;


    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerTodos(){
        return ResponseEntity.ok(prestamoService.obtenerTodos());
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerLibrosAtrasados(){
        return ResponseEntity.ok(prestamoService.obtenerLibrosAtrasados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> obtenerPorId(@PathVariable Long id){

        return prestamoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerUsuarioId(Long id){
        return ResponseEntity.ok(prestamoService.obtenerPorIdUsuario(id));
    }

    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> guardar(@Valid @RequestBody PrestamoRequestDTO doto){
            return ResponseEntity.status(201).body(prestamoService.guardar(doto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody PrestamoRequestDTO doto) {
        return prestamoService.actualizar(id, doto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
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
