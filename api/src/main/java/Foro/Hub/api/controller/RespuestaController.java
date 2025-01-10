package Foro.Hub.api.controller;

import Foro.Hub.api.domain.respuesta.Respuesta;
import Foro.Hub.api.domain.respuesta.RespuestaRepository;
import Foro.Hub.api.domain.respuesta.RespuestaDTO;
import Foro.Hub.api.domain.topico.Topico;
import Foro.Hub.api.domain.topico.TopicoRepository;
import Foro.Hub.api.domain.usuario.Usuario;
import Foro.Hub.api.domain.usuario.UsuarioRepository;
import Foro.Hub.api.validation.RespuestaValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    // =========================
    // CREAR RESPUESTA
    // =========================
    @PostMapping
    public ResponseEntity<Respuesta> crearRespuesta(@RequestBody RespuestaDTO respuestaDTO) {
        // 1) Verificar que el tópico exista
        if (!topicoRepository.existsById(respuestaDTO.topicoId())) {
            return ResponseEntity.badRequest().build();
        }

        RespuestaValidation.validar(respuestaDTO);

        // 2) Obtener el usuario logueado desde el token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario autor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3) Buscar el tópico
        Topico topico = topicoRepository.findById(respuestaDTO.topicoId())
                .orElseThrow(() -> new RuntimeException("Tópico no encontrado"));

        // 4) Crear la respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setAutor(autor);
        respuesta.setAsociadoTopico(topico);
        respuesta.setMensaje(respuestaDTO.mensaje());
        respuesta.setFechaCreacion(
                respuestaDTO.fechaCreacion() != null ?
                        respuestaDTO.fechaCreacion() : LocalDateTime.now()
        );

        // 5) Guardar en la base de datos
        Respuesta respuestaGuardada = respuestaRepository.save(respuesta);
        return ResponseEntity.ok(respuestaGuardada);
    }

    // =========================
    // LISTAR TODAS LAS RESPUESTAS
    // =========================
    @GetMapping
    public ResponseEntity<List<Respuesta>> listarRespuestas() {
        List<Respuesta> respuestas = respuestaRepository.findAll();
        return ResponseEntity.ok(respuestas);
    }

    // =========================
    // LISTAR RESPUESTAS DE UN TÓPICO ESPECÍFICO
    // =========================
    @GetMapping("/topico/{topicoId}")
    public ResponseEntity<List<Respuesta>> listarRespuestasPorTopico(@PathVariable Long topicoId) {
        if (!topicoRepository.existsById(topicoId)) {
            return ResponseEntity.notFound().build();
        }
        List<Respuesta> respuestas = respuestaRepository.findByAsociadoTopicoId(topicoId);
        return ResponseEntity.ok(respuestas);
    }

    // =========================
    // OBTENER UNA RESPUESTA POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Respuesta> obtenerRespuestaPorId(@PathVariable Long id) {
        return respuestaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ACTUALIZAR UNA RESPUESTA
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<Respuesta> actualizarRespuesta(@PathVariable Long id,
                                                         @RequestBody RespuestaDTO respuestaDTO) {
        // 1) Buscar si existe
        Optional<Respuesta> opt = respuestaRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Respuesta respuesta = opt.get();

        // 2) Obtener el usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioLogueado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3) Verificar si ES el autor de esta respuesta
        if (!respuesta.getAutor().getId().equals(usuarioLogueado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4) Actualizar los campos
        if (respuestaDTO.topicoId() != null && topicoRepository.existsById(respuestaDTO.topicoId())) {
            Topico topico = topicoRepository.findById(respuestaDTO.topicoId()).get();
            respuesta.setAsociadoTopico(topico);
        }

        // Actualizar mensaje
        respuesta.setMensaje(respuestaDTO.mensaje());
        // Actualizar fecha si viene en el body
        if (respuestaDTO.fechaCreacion() != null) {
            respuesta.setFechaCreacion(respuestaDTO.fechaCreacion());
        }

        // 5) Guardar la respuesta actualizada
        Respuesta respuestaGuardada = respuestaRepository.save(respuesta);
        return ResponseEntity.ok(respuestaGuardada);
    }

    // =========================
    // ELIMINAR UNA RESPUESTA
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id) {
        // 1) Buscar si existe
        Optional<Respuesta> opt = respuestaRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Respuesta respuesta = opt.get();

        // 2) Obtener el usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioLogueado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3) Verificar si es el autor
        if (!respuesta.getAutor().getId().equals(usuarioLogueado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4) Borramos
        respuestaRepository.delete(respuesta);
        return ResponseEntity.noContent().build();
    }
}
