package Foro.Hub.api.controller;

import Foro.Hub.api.domain.topico.StatusTopico;
import Foro.Hub.api.domain.topico.Topico;
import Foro.Hub.api.domain.topico.TopicoDTO;
import Foro.Hub.api.domain.topico.TopicoRepository;
import Foro.Hub.api.domain.usuario.Usuario;
import Foro.Hub.api.domain.usuario.UsuarioRepository;
import Foro.Hub.api.validation.TopicoValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // =======================
    // CREAR TÓPICO
    // =======================
    @PostMapping
    public ResponseEntity<Topico> crearTopico(@RequestBody TopicoDTO dto) {

        // Validaciones
        TopicoValidation.validar(dto);

        // 1) Obtener el usuario logueado a partir del token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();  // userDetailsService asignó como username = el email
        Usuario autor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2) Crear el tópico con la info del DTO
        Topico topico = new Topico();
        topico.setAutor(autor);
        topico.setTitulo(dto.titulo());
        topico.setMensaje(dto.mensaje());
        topico.setFechaCreacion(dto.fechaCreacion());
        topico.setStatus(StatusTopico.valueOf(dto.status()));

        // 3) Guardar en BD
        Topico guardado = topicoRepository.save(topico);
        return ResponseEntity.ok(guardado);
    }

    // =======================
    // LISTAR TÓPICOS
    // =======================
    @GetMapping
    public ResponseEntity<List<Topico>> listarTopicos() {
        List<Topico> topicos = topicoRepository.findAll();
        return ResponseEntity.ok(topicos);
    }

    // =======================
    // OBTENER TÓPICO POR ID
    // =======================
    @GetMapping("/{id}")
    public ResponseEntity<Topico> obtenerTopicoPorId(@PathVariable Long id) {
        return topicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =======================
    // ACTUALIZAR TÓPICO
    // =======================
    @PutMapping("/{id}")
    public ResponseEntity<Topico> actualizarTopico(@PathVariable Long id, @RequestBody TopicoDTO dto) {
        // 1) Buscamos si existe
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Topico topico = optionalTopico.get();

        // 2) Obtenemos el usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioLogueado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3) (Opcional) Verificar si el logueado es el autor del tópico
        if (!topico.getAutor().getId().equals(usuarioLogueado.getId())) {
            // No es el mismo autor => 403
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4) Actualizamos los campos
        topico.setTitulo(dto.titulo());
        topico.setMensaje(dto.mensaje());
        topico.setStatus(StatusTopico.valueOf(dto.status()));

        // 5) Guardar
        Topico actualizado = topicoRepository.save(topico);
        return ResponseEntity.ok(actualizado);
    }

    // =======================
    // ELIMINAR TÓPICO
    // =======================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {
        // 1) Buscamos si existe
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Topico topico = optionalTopico.get();

        // 2) Obtenemos el usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioLogueado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3) Verificamos si es su autor
        if (!topico.getAutor().getId().equals(usuarioLogueado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4) Borramos
        topicoRepository.delete(topico);
        return ResponseEntity.noContent().build();
    }
}
