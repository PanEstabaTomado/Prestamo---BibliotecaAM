package dfy1103.bibliotecaam.prestamo.config;

import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import dfy1103.bibliotecaam.prestamo.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PrestamoRepository prestamoRepository;

    @Override
    public void run(String... args){
        if (prestamoRepository.count() > 0){
            log.info(">>> DataInitializer: La Base de datos ya tiene registros guardados; no es necesaria la integracion de datos iniciales.");
        }

        log.info("DataInitializer: Base de Datos vacia, inicializando la carga de datos iniciales.");

        prestamoRepository.save(new Prestamo(null, LocalDate.of(2006,2,1),LocalDate.of(2006,5,1) ,false,1L));

        prestamoRepository.save(new Prestamo(null, LocalDate.of(2006,1,1),LocalDate.of(2006,4,1) ,false,1L));

        prestamoRepository.save(new Prestamo(null, LocalDate.of(2006,5,20),LocalDate.of(2007,8,20) ,false,2L));
    }
}
