package security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class AuthInterceptor extends Filter {

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {

        String path = exchange.getRequestURI().getPath();

        // 🔓 ROTAS LIVRES (não precisam de token)
        if (path.equals("/login")) {
            chain.doFilter(exchange);
            return;
        }

        try {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JWTVerificationException("Token ausente");
            }

            String token = authHeader.replace("Bearer ", "");

            String user = JwtUtil.validateToken(token);

            if (user == null) {
                throw new JWTVerificationException("Token inválido");
            }

            // 🔥 (opcional) guardar usuário na request
            exchange.setAttribute("user", user);

            chain.doFilter(exchange);

        } catch (Exception e) {

            String response = """
                    {
                        "success": false,
                        "message": "Acesso negado: token inválido ou ausente"
                    }
                    """;

            exchange.getResponseHeaders().set("Content-Type", "application/json");

            exchange.sendResponseHeaders(401, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    @Override
    public String description() {
        return "JWT Authentication Middleware";
    }
}