package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {

    private final String basePath;

    public StaticFileHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/html/login.html";
        }

        // se for HTML sem pasta, assume /html
        if (path.endsWith(".html") && !path.startsWith("/html/")) {
            path = "/html" + path;
        }

        // garante que tudo está dentro de public
        File file = new File(basePath, path);

        System.out.println("Buscando arquivo em: " + file.getAbsolutePath());

        if (!file.exists()) {
            String response = "404 - Arquivo não encontrado";
            exchange.sendResponseHeaders(404, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
            return;
        }

        String contentType = Files.probeContentType(file.toPath());
        exchange.getResponseHeaders().add("Content-Type", contentType);

        byte[] bytes = Files.readAllBytes(file.toPath());

        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();

    }
}