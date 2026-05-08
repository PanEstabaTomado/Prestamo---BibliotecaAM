package dfy1103.bibliotecaam.prestamo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPresta;

    @Column(nullable = false)
    private LocalDate fechaIniPresta;


    @Column(nullable = false)
    private LocalDate fechaVencPresta;

    @Column(nullable = false)
    private  boolean devuelto;
    // se conecta con id usuario y id libro
}
