package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import exception.ApiException;
import service.ServiceUsuario;

import java.io.OutputStream;

public class AuthController implements HttpHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) {

        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                ApiResponse response = new ApiResponse(
                        false,
                        "Método não permitido",
                        null
                );

                sendResponse(exchange, mapper.writeValueAsString(response), 405);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes());
            JsonNode json = mapper.readTree(body);

            String email = json.get("email").asText();
            String password = json.get("password").asText();

            String token = ServiceUsuario.login(email, password);

            ApiResponse response = new ApiResponse(
                    true,
                    "Login realizado com sucesso",
                    token
            );

            sendResponse(exchange, mapper.writeValueAsString(response), 200);

        } catch (ApiException e) {

            try {
                ApiResponse response = new ApiResponse(
                        false,
                        e.getMessage(),
                        null
                );

                sendResponse(exchange, mapper.writeValueAsString(response), e.getStatusCode());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();

            try {
                ApiResponse response = new ApiResponse(
                        false,
                        "Erro interno do servidor",
                        null
                );

                sendResponse(exchange, mapper.writeValueAsString(response), 500);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int status) throws Exception {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        byte[] bytes = response.getBytes("UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
