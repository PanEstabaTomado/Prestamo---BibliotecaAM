package dfy1103.bibliotecaam.prestamo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${usuario.url}")
    private String usuarioUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(usuarioUrl)   // http://localhost:8085 (Cambialo al de prestamo)
                .build();
    }
}
