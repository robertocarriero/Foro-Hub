package Foro.Hub.api.validation;

import Foro.Hub.api.domain.topico.TopicoDTO;

public class TopicoValidation {


    public static void validar(TopicoDTO dto) {
        // Validar título
        if (dto.titulo() == null || dto.titulo().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }

        // Validar mensaje
        if (dto.mensaje() == null || dto.mensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío.");
        }

        // Validar status (si es un enum, podrías verificar si coincide con tus valores)
        if (dto.status() == null ||
                (!dto.status().equals("NO_RESPONDIDO")
                        && !dto.status().equals("RESPONDIDO")
                        && !dto.status().equals("CERRADO"))) {
            throw new IllegalArgumentException("Status no válido.");
        }

        // Validación adicional en caso de que se quiera forzar fechaCreacion
        // if (dto.fechaCreacion() == null) {
        //     throw new IllegalArgumentException("La fecha de creación es obligatoria.");
        // }
    }
}


