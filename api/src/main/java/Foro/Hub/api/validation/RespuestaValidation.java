package Foro.Hub.api.validation;

import Foro.Hub.api.domain.respuesta.RespuestaDTO;

public class RespuestaValidation {
    public static void validar(RespuestaDTO dto) {
        // Validar topicoId
        if (dto.topicoId() == null) {
            throw new IllegalArgumentException("El id del tópico es obligatorio.");
        }

        // Validar mensaje
        if (dto.mensaje() == null || dto.mensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje de la respuesta no puede estar vacío.");
        }

        // Validar fecha si corresponde
        // if (dto.fechaCreacion() == null) { ... }
    }
}

