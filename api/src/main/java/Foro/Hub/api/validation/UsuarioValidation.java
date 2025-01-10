package Foro.Hub.api.validation;

import Foro.Hub.api.domain.usuario.Usuario;
import Foro.Hub.api.domain.usuario.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioValidation {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioValidation(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validarUsuarioUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
    }

    public void validarDatosUsuario(Usuario usuario) {
        if (usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        if (usuario.getEmail().isBlank() || !usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email debe tener un formato válido.");
        }

        if (usuario.getContraseña().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
    }

    /**
     * Verifica que la contraseña no coincida con la de otros usuarios .
     */
    public void validarContraseñaUnica(String contraseñaPlana) {
        for (Usuario usuario : usuarioRepository.findAll()) {
            // Compara la contraseña en texto plano con la encriptada
            if (passwordEncoder.matches(contraseñaPlana, usuario.getContraseña())) {
                throw new IllegalArgumentException("La contraseña ya está en uso.");
            }
        }
    }
}
