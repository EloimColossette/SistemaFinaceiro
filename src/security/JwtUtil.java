package security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {

    // 🔥 ideal: vir de variável de ambiente (.env)
    private static final String SECRET = "MINHA_CHAVE_SUPER_SEGURA_123456";

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    // gerar token
    public static String generateToken(String email) {

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withIssuer("SistemaCompras")
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .sign(ALGORITHM);
    }

    // validar token (RETORNA EMAIL OU NULL)
    public static String validateToken(String token) {

        try {
            DecodedJWT jwt = JWT.require(ALGORITHM)
                    .withIssuer("SistemaCompras")
                    .build()
                    .verify(token);

            return jwt.getSubject();

        } catch (JWTVerificationException e) {
            return null; // token inválido
        }
    }
}