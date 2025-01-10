package Foro.Hub.api.domain.respuesta;

import java.time.LocalDateTime;

public record RespuestaDTO(
        Long topicoId,
        String mensaje,
        LocalDateTime fechaCreacion) {
}
