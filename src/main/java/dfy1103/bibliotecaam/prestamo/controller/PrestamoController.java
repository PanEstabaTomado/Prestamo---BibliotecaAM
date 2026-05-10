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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/biblioteca/prestamo")
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

    @DeleteMapping("({id}")
    public ResponseEntity<?> borrar(@PathVariable Long id){
        if (prestamoService.obtenerPorId(id).isEmpty()){
            Map<String, String> noEncontrado = new HashMap<>();
            noEncontrado.put("ERROR: ","¡El Prestamo con el id "+ id + " no fue encontrado!");
            return ResponseEntity.status(404).body(noEncontrado);
        }
        prestamoService.eliminar(id);
        Map<String, String> eliminado = new HashMap<>();
        eliminado.put("¡EXITO! ","¡El Prestamo fue eliminado con exito!");
        return ResponseEntity.ok(eliminado);
    }


}
