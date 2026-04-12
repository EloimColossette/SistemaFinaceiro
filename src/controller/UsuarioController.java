package controller;

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

        String body = lerBody(exchange);

        UsuarioModel user = new UsuarioModel();

        user.setId(Integer.parseInt(getValorJson(body, "id")));
        user.setEmail(getValorJson(body, "email"));
        user.setPassword(getValorJson(body, "senha"));
        user.setFirst_name(getValorJson(body, "first_name"));
        user.setLast_name(getValorJson(body, "last_name"));
        user.setCpf(getValorJson(body, "cpf"));

        ServiceUsuario.criarUsuario(user);

        sendResponse(exchange, "Usuário atualizado com sucesso!", 200);
    }

    // PARTCH
    private void handlerPartch(HttpExchange exchange) throws Exception{
        logger.info("Recebendo requisição PARTCH /usuario");

        String body = lerBody(exchange);

        UsuarioModel user = new UsuarioModel();

        user.setId(Integer.parseInt(getValorJson(body, "id")));
        user.setEmail(getValorJson(body, "email"));
        user.setPassword(getValorJson(body, "senha"));
        user.setFirst_name(getValorJson(body, "first_name"));
        user.setLast_name(getValorJson(body, "last_name"));
        user.setCpf(getValorJson(body, "cpf"));

        ServiceUsuario.criarUsuario(user);

        sendResponse(exchange, "Usuario atualizado parcialmente com sucesso!", 200);
    }

    // DELETE
    private void handleDelete(HttpExchange exchange) throws Exception{

        logger.info("Recebendo requisição DELETE /Usuario");

        String query = exchange.getRequestURI().getQuery();

        int id = Integer.parseInt(query.split("=")[1]);

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

                    if(path.equals("/login")){
                        handlerLogin(exchange);
                    }else{
                        handlePost(exchange);
                    }
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "PATCH":
                    handlerPartch(exchange);
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

    private void handlerLogin(HttpExchange exchange) throws Exception{
        logger.info("Recebendo requisição POST/login");

        String body = lerBody(exchange);

        String email = getValorJson(body, "email");
        String password = getValorJson(body, "password");

        String result = ServiceUsuario.login(email, password);

        sendResponse(exchange, result, 200);
    }
}