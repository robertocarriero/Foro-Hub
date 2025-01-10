package Foro.Hub.api.service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key getSigningKey() {
        System.out.println("Clave JWT cargada: " + jwtSecret); // Agregar este log
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Genera un token JWT para un usuario.
     *
     * @param username Nombre de usuario para el que se genera el token.
     * @return Token JWT generado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token válido por 24 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Valida un token JWT y extrae el nombre de usuario.
     *
     * @param token Token JWT a validar.
     * @return Nombre de usuario extraído del token si es válido.
     * @throws RuntimeException Si el token es inválido o ha expirado.
     */
    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            System.err.println("Error al validar token: " + e.getMessage()); // Log del error
            throw new RuntimeException("Token inválido o expirado", e);
        }
    }
}