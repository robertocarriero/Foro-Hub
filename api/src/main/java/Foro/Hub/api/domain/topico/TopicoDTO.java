package Foro.Hub.api.domain.topico;

import java.time.LocalDateTime;

public record TopicoDTO (
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        String status
) {}
