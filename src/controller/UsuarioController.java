package controller;

import security.JwtUtil;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import model.UsuarioModel;
import service.ServiceUsuario;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioController implements HttpHandler{

    private static final Logger logger = Logger.getLogger(UsuarioController.class.getName());

    // GET
    private void handleGet(HttpExchange exchange) throws Exception{
        logger.info("Recebendo Requisicao GET /Usuario");

        if(!validarToken(exchange)) return;

        List<String> users = ServiceUsuario.listarUsuarios();

        String response = String.join("\n", users);

        sendResponse(exchange, response, 200);
    }

    // POST
    private void handlePost(HttpExchange exchange) throws Exception{
        logger.info("Criando requisição POST /Usuario");


        String body = lerBody(exchange);

        UsuarioModel user = new UsuarioModel();

        user.setEmail(getValorJson(body, "email"));
        user.setPassword(getValorJson(body, "senha"));
        user.setFirst_name(getValorJson(body, "first_name"));
        user.setLast_name(getValorJson(body, "last_name"));
        user.setCpf(getValorJson(body, "cpf"));

        ServiceUsuario.criarUsuario(user);

        sendResponse(exchange, "Usuario criado com sucesso!", 201);
    }

    // PUT
    private void handlePut(HttpExchange exchange) throws Exception{

        logger.info("Recebendo a requisição PUT /Usuario");

        if(!validarToken(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        int id = getIdFrompath(path);

        if(id <= 0){
            sendResponse(exchange, "ID Invalido!", 400);
            return;
        }

        String body = lerBody(exchange);

        UsuarioModel user = new UsuarioModel();

        user.setId(Integer.parseInt(getValorJson(body, "id")));
        user.setEmail(getValorJson(body, "email"));
        user.setPassword(getValorJson(body, "senha"));
        user.setFirst_name(getValorJson(body, "first_name"));
        user.setLast_name(getValorJson(body, "last_name"));
        user.setCpf(getValorJson(body, "cpf"));

        ServiceUsuario.atualizarUsuario(user);

        sendResponse(exchange, "Usuário atualizado com sucesso!", 200);
    }

    // PATCH
    private void handlerPatch(HttpExchange exchange) throws Exception{
        logger.info("Recebendo requisição PARTCH /usuario");

        if(!validarToken(exchange)) return;

        String body = lerBody(exchange);

        UsuarioModel user = new UsuarioModel();

        user.setId(Integer.parseInt(getValorJson(body, "id")));
        user.setEmail(getValorJson(body, "email"));
        user.setPassword(getValorJson(body, "senha"));
        user.setFirst_name(getValorJson(body, "first_name"));
        user.setLast_name(getValorJson(body, "last_name"));
        user.setCpf(getValorJson(body, "cpf"));

        ServiceUsuario.atualizarParcialmenteUsuario(user);

        sendResponse(exchange, "Usuario atualizado parcialmente com sucesso!", 200);
    }

    // DELETE
    private void handleDelete(HttpExchange exchange) throws Exception{

        logger.info("Recebendo requisição DELETE /Usuario/{id}");

        if(!validarToken(exchange)) return;

        String path = exchange.getRequestURI().getPath();

        int id = getIdFrompath(path);

        if(id <= 0){
            sendResponse(exchange, "ID Invalido", 400);
            return;
        }

        ServiceUsuario.excluirUsuario(id);

        sendResponse(exchange, "Usuario Deletado com sucesso !", 200);
    }

    // UTIL
    private String lerBody(HttpExchange exchange) throws Exception{
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes());
        is.close();

        return body;
    }

    private int getIdFrompath(String path){
        String[] partes = path.split("/");

        if(partes.length < 3){
            return -1;
        }

        return Integer.parseInt(partes[2]);
    }

    private void sendResponse(HttpExchange exchange, String resposta, int status) throws Exception{
        exchange.getResponseHeaders().add("Content-Type", "text/plain");

        exchange.sendResponseHeaders(status, resposta.getBytes().length);

        OutputStream os = exchange.getResponseBody();

        os.write(resposta.getBytes());

        os.close();
    }

    private String getValorJson(String json, String chave) {

        String busca = "\"" + chave + "\"";
        int inicio = json.indexOf(busca);

        if (inicio == -1) return "";

        inicio = json.indexOf(":", inicio) + 1;

        // pula espaços e aspas
        while (json.charAt(inicio) == ' ' || json.charAt(inicio) == '\"') {
            inicio++;
        }

        int fim = inicio;

        while (json.charAt(fim) != '\"') {
            fim++;
        }

        return json.substring(inicio, fim);
    }

    @Override
    public void handle(HttpExchange exchange){
        System.out.println("PATH: " + exchange.getRequestURI().getPath());
        try{
            String metodo = exchange.getRequestMethod();

            switch (metodo) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    String path = exchange.getRequestURI().getPath();

                    if(path.startsWith("/login")){
                        handlerLogin(exchange); // ❗ NÃO valida token aqui
                    } else {
                        handlePost(exchange); // aqui sim pode validar
                    }
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "PATCH":
                    handlerPatch(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendResponse(exchange, "Metodo não suportado", 404);
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Erro ao processar requisição", e);
        }
    }

    private boolean validarToken(HttpExchange exchange) throws Exception {

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        // 1. Verifica se veio o header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendResponse(exchange, "Token não informado", 401);
            return false;
        }

        // 2. Remove "Bearer "
        String token = authHeader.replace("Bearer ", "");

        try {
            // 3. Valida token
            JwtUtil.validateToken(token); // ⚠️ veja o nome correto no seu JwtUtil
            return true;
        } catch (Exception e) {
            sendResponse(exchange, "Token inválido", 401);
            return false;
        }
    }

    private void handlerLogin(HttpExchange exchange) throws Exception{
        logger.info("Recebendo requisição POST/login");

        String body = lerBody(exchange);

        String email = getValorJson(body, "email");
        String password = getValorJson(body, "password");

        String result = ServiceUsuario.login(email, password);

        sendResponse(exchange, result, 200);
    }
}