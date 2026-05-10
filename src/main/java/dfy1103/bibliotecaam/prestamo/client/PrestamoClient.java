package dfy1103.bibliotecaam.prestamo.client;

// ═══════════════════════════════════════════════════
// ESTE ARCHIVO NO EXISTE EN PARTE A (WebClient).
// Es el núcleo del cambio en la Parte B.
//
// @FeignClient declara que esta interfaz es un cliente HTTP.
//   name: identificador lógico del servicio destino.
//   url:  dirección del servicio. Se lee de application.properties
//         igual que @Value pero dentro de la anotación.
//
// Spring Cloud genera automáticamente la implementación:
//   1. Recibe la llamada  obtenerPorId(id)
//   2. Construye  GET http://localhost:8081/api/especialidades/{id}
//   3. Envía la petición HTTP
//   4. Deserializa el JSON a String
//   5. Devuelve el resultado
//   6. Si 404 → lanza FeignException.NotFound
//
// Todo sin que escribamos ni una línea de código HTTP.
// ═══════════════════════════════════════════════════
// @FeignClient(name = "ms-especialidades", url = "${ms.especialidades.url}")
public class PrestamoClient {
}
