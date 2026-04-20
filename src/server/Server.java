package server;

import com.sun.net.httpserver.HttpServer;
import controller.UsuarioController;
import security.AuthFilter;
import security.CorsFilter;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void start() throws Exception {
        try {
            logger.info("Criando servidor na porta 8080...");

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // 🔐 /usuarios (com Auth + CORS)
            var usuariosContext = server.createContext("/usuarios", new UsuarioController());
            usuariosContext.getFilters().add(new CorsFilter());
            usuariosContext.getFilters().add(new AuthFilter());

            // 🔓 /login (sem Auth, só CORS)
            var loginContext = server.createContext("/login", new UsuarioController());
            loginContext.getFilters().add(new CorsFilter());

            server.setExecutor(null);
            server.start();

            logger.info("Servidor rodando em http://localhost:8080");

        } catch (Exception e) {
            logger.severe(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}