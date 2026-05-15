package dfy1103.bibliotecaam.prestamo.service;

import dfy1103.bibliotecaam.prestamo.dto.PrestamoRequestDTO;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import dfy1103.bibliotecaam.prestamo.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;

    private final WebClient webClient;

    /* ------------------------------------------------------------------
     * * CREATE
     * READ
     * UPDATE
     * DELETE
     * Featuring Doto from (Uma Musume) CompraResponseDTO - CompraRequestDTO

     */
    private PrestamoResponseDTO mapToDTO(Prestamo prestamo){
        String prestamoDevuelto;
        if (prestamo.isDevuelto()) {
            prestamoDevuelto = "Libro devuelto";
        }else {
            prestamoDevuelto = "Libro no devuelto";
        }
        return new PrestamoResponseDTO(
                prestamo.getIdPresta(),
                prestamo.getFechaIniPresta(),
                prestamo.getFechaVencPresta(),
                prestamoDevuelto,
                prestamo.getUsuarioId()
        );
    }

    private void validarUsuario(Long idUsuario) {
        try {
            webClient.get()
                    .uri("/api/bibliotecaam/usuario/{id}", idUsuario)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(">>> Usuario {} validado correctamente (WebClient)", idUsuario);

        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException(
                    "El Usuario con id " + idUsuario + " no existe en la BD de Usuario.");
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se puede conectar con usuario: " + e.getMessage());
        }
    }

    public List<PrestamoResponseDTO> obtenerTodos(){
        return prestamoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrestamoResponseDTO> obtenerPorId(Long id){
        return prestamoRepository.findById(id)
                .map(this::mapToDTO);
    }

    public PrestamoResponseDTO guardar(PrestamoRequestDTO doto){
        validarUsuario(doto.getUsuarioId());
        Prestamo prestamo = new Prestamo(
                null,
                doto.getFechaIniPresta(),
                doto.getFechaVencPresta(),
                doto.isDevuelto(),
                doto.getUsuarioId()
        );
        return mapToDTO(prestamoRepository.save(prestamo));
    }

    public Optional<PrestamoResponseDTO> actualizar(Long id, PrestamoRequestDTO doto){
        return prestamoRepository.findById(id).map(existente-> {
            existente.setFechaIniPresta(doto.getFechaIniPresta());
            existente.setFechaVencPresta(doto.getFechaVencPresta());
            existente.setDevuelto(doto.isDevuelto());
            return mapToDTO(prestamoRepository.save(existente));
                });
    }

    public void eliminar(Long id){
        prestamoRepository.deleteById(id);
    }

    /* ------------------------------------------------------------------
     * * FUNCIONES EXTRAS
     */


    public List<PrestamoResponseDTO> obtenerLibrosAtrasados(){
        return prestamoRepository.obtenerLibrosPorDevolver()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrestamoResponseDTO> actualizarCheck(Long id, PrestamoRequestDTO doto){
        return prestamoRepository.findById(id).map(existente-> {
            existente.setDevuelto(doto.isDevuelto());
            return mapToDTO(prestamoRepository.save(existente));
        });
    }

    public List<PrestamoResponseDTO> obtenerPorIdUsuario(Long id){
        return prestamoRepository.findByUsuarioId(id)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
