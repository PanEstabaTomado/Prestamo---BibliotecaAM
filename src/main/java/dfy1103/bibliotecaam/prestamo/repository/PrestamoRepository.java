package dfy1103.bibliotecaam.prestamo.repository;

import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    @Query("SELECT p FROM Prestamo p WHERE p.fechaVencPresta > :fecha")
    List<Prestamo> obtenerLibrosAtrasados(@Param("fecha")LocalDate fecha);

    @Query("SELECT p FROM Prestamo p WHERE p.fechaVencPresta > :fecha AND p.devuelto = false")
    List<Prestamo> obtenerLibrosPorDevolver(@Param("fecha")LocalDate fecha);
}
