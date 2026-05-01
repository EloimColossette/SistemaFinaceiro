package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import exception.ApiException;
import service.PasswordResetService;

import javax.xml.stream.events.StartDocument;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class PasswordResetController implements HttpHandler {
    private static final  ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange){
        try{
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if(!exchange.getRequestMethod().equalsIgnoreCase("POST")){
                sendJson(exchange, new ApiResponse(false, "Metodo não permitido", null),405);
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if(path.endsWith("/forgot-password")){
                handleForgotPassword(exchange);
            } else if (path.endsWith("/reset-password")){
                handleResetPassword(exchange);
            } else{
                sendJson(exchange, new ApiResponse(false, "Rota não encontrada", null), 404);
            }

        } catch (ApiException e) {

            sendJson(exchange, new ApiResponse(false, e.getMessage(), null), e.getStatusCode());

        } catch (Exception e) { // 🔥 FALTAVA ISSO

            e.printStackTrace();
            sendJson(exchange, new ApiResponse(false, "Erro interno do servidor", null), 500);
        }
    }

    // Esqueci minha senha
    public void handleForgotPassword(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(body);

            if(!json.has("email")){
                throw new ApiException("Email obrigatorio", 400);
            }

            String email = json.get("email").asText();

            PasswordResetService.solicitarResetSenha(email);

            sendJson(exchange, new ApiResponse(true, "Link de recuperação enviado (ver console)", null), 200);

        } catch (ApiException e) {
            sendJson(exchange, new ApiResponse(false, e.getMessage(), null), e.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, new ApiResponse(false, "Erro interno do servidor", null), 500);
        }
    }

    // Resetar senha
    private void handleResetPassword(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(body);

            if (!json.has("token") || !json.has("newPassword")) {
                throw new ApiException("Token e nova senha são obrigatórios", 400);
            }

            String token = json.get("token").asText();
            String newPassword = json.get("newPassword").asText();

            PasswordResetService.redefinirSenha(token, newPassword);

            sendJson(exchange,
                    new ApiResponse(true, "Senha redefinida com sucesso", null),
                    200
            );

        } catch (ApiException e) {
            sendJson(exchange, new ApiResponse(false, e.getMessage(), null), e.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, new ApiResponse(false, "Erro interno do servidor", null), 500);
        }
    }

    //helper
    private static void sendJson(HttpExchange exchange, ApiResponse response, int status) {
        try {
            String json = objectMapper.writeValueAsString(response);

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(status, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
