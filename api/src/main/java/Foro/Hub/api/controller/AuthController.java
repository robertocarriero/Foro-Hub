package Foro.Hub.api.controller;

import Foro.Hub.api.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())

        );
        String token = jwtService.generateToken(authentication.getName()); // Generar token JWT
        System.out.println("Intentando autenticar usuario: " + authRequest.username());
        System.out.println("Token generado: " + token);
        return ResponseEntity.ok(token);

    }

}
record AuthRequest(String username, String password) {}