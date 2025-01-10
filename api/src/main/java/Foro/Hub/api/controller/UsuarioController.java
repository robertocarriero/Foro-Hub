package Foro.Hub.api.controller;

import Foro.Hub.api.domain.usuario.Usuario;
import Foro.Hub.api.domain.usuario.UsuarioRepository;
import Foro.Hub.api.service.UsuarioService;
import Foro.Hub.api.validation.UsuarioValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioValidation usuarioValidation;

    // ****************
    // [1] Crear un usuario sin validaciones
    // ****************
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario nuevoUsuario) {
        // En este ejemplo NO encriptamos la contraseña,
        // porque es un método "alternativo" de creación (no el principal).
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(usuarioGuardado);
    }

    // ****************
    // [2] Registrar un usuario con validaciones (principal)
    // ****************
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Validaciones de email, datos y contraseña
            usuarioValidation.validarUsuarioUnico(usuario.getEmail());
            usuarioValidation.validarDatosUsuario(usuario);
            usuarioValidation.validarContraseñaUnica(usuario.getContraseña());

            // NO encriptamos acá -> dejamos la contraseña tal cual se recibe
            // (porque se encriptará en UsuarioService)
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);

            return ResponseEntity.ok(usuarioRegistrado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // Obtener un usuario específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
