package Foro.Hub.api.domain.respuesta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByAsociadoTopicoId(Long topicoId);

}
