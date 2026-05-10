package dfy1103.bibliotecaam.prestamo.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrestamoResponseDTO {
    private Long idPresta;

    private LocalDate fechaIniPresta;

    private LocalDate fechaVencPresta;

    private boolean devuelto;

    private Long usuarioId;
}
