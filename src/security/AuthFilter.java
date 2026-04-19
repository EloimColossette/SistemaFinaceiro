package security;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class AuthFilter extends Filter {
    @Override
    public String description() {
        return "Filtro de autenticação JWT";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // 🔓 Rotas públicas
        if (
                path.equals("/login") ||
                        (path.equals("/usuarios") && method.equals("POST"))
        ) {
            chain.doFilter(exchange);
            return;
        }

        // 🔒 Rotas protegidas
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendResponse(exchange, "Token não informado", 401);
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        String email = JwtUtil.validateToken(token);

        if (email == null) {
            sendResponse(exchange, "Token inválido", 401);
            return;
        }

        chain.doFilter(exchange);
    }

    private void sendResponse(HttpExchange exchange, String message, int status) throws IOException{
        exchange.sendResponseHeaders(status, message.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }
}
