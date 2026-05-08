package dfy1103.bibliotecaam.prestamo.service;

import dfy1103.bibliotecaam.prestamo.dto.PrestamoRequestDTO;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import dfy1103.bibliotecaam.prestamo.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;

    // CRUD
    private PrestamoResponseDTO mapToDTO(Prestamo prestamo){
        return new PrestamoResponseDTO(
                prestamo.getIdPresta(),
                prestamo.getFechaIniPresta(),
                prestamo.getFechaVencPresta(),
                prestamo.isDevuelto()
        );
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
        Prestamo prestamo = new Prestamo(
                doto.getIdPresta(),
                doto.getFechaIniPresta(),
                doto.getFechaVencPresta(),
                doto.isDevuelto()
        );
        return mapToDTO(prestamoRepository.save(prestamo));
    }

    public Optional<PrestamoResponseDTO> actualizar(Long id, PrestamoRequestDTO doto){
        return prestamoRepository.findById(id).map(existente-> {
            existente.setIdPresta(doto.getIdPresta());
            existente.setFechaIniPresta(doto.getFechaIniPresta());
            existente.setFechaVencPresta(doto.getFechaVencPresta());
            existente.setDevuelto(doto.isDevuelto());
            return mapToDTO(prestamoRepository.save(existente));
                });
    }

    public void eliminar(Long id){
        prestamoRepository.deleteById(id);
    }

    // FUNCIONES EXTRAS
}
