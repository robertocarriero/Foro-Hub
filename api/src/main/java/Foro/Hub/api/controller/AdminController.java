package Foro.Hub.api.controller;

import Foro.Hub.api.domain.usuario.Usuario;
import Foro.Hub.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Listar todos los usuarios.
     * Solo accesible para ADMIN (según SecurityConfig).
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Cambiar el rol de un usuario.
     * Ej: PATCH /admin/usuarios/5/rol?nuevoRol=ADMIN
     */
    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<Usuario> cambiarRol(
            @PathVariable Long id,
            @RequestParam String nuevoRol
    ) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setRol(nuevoRol); // "USER" o "ADMIN"
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un usuario.
     * Ej: DELETE /admin/usuarios/5
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
