package dfy1103.bibliotecaam.prestamo;

import dfy1103.bibliotecaam.prestamo.model.Prestamo;
import dfy1103.bibliotecaam.prestamo.repository.PrestamoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private PrestamoRepository prestamoRepository;

    @Override
    public void run (String... args) throws Exception{
        Faker faker = new Faker();

        for (int i = 0; i < 6; i++) {
            Prestamo prestamo = new Prestamo();
            prestamo.setFechaIniPresta(faker.timeAndDate().birthday());
            prestamo.setFechaVencPresta(faker.timeAndDate().birthday());
            prestamo.setDevuelto(faker.bool().bool());
            prestamo.setUsuarioId((long) faker.number().numberBetween(1,3));
            prestamo.setIdLibro((long) faker.number().numberBetween(1,3));

            prestamoRepository.save(prestamo);
        }
    }
}
