package dfy1103.bibliotecaam.prestamo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrestamoRequestDTO {

    @NotNull(message = "La fecha de inicio del prestamo no puede estar vacia.")
    private LocalDate fechaIniPresta;

    @NotNull(message = "La fecha de vencimiento del prestamo no puede estar vacia.")
    private LocalDate fechaVencPresta;

    @NotNull(message = "Se debe saber si ha sido devuelto o no (true o false).")
    private boolean devuelto;

    @NotNull(message = "El usuarioId no puede estar vacio.")
    private Long usuarioId;
}
