package service;

import exception.ApiException;
import model.UsuarioModel;
import repository.UsuarioRepository;
import security.JwtUtil;
import dto.UsuarioRequest;
import java.util.List;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class ServiceUsuario {

    // GET
    public static List<UsuarioModel> listarUsuarios() throws Exception {
        return UsuarioRepository.listarUsuarios();
    }

    // POST
    public static void criarUsuario(UsuarioRequest usuario) throws Exception {
        // Validação Simples
        if(usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new ApiException("Email Obrigatorio", 400);
        }
        if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new ApiException("Password Obrigatorio", 400);
        }

        String EncryptedPassword = hashPassword(usuario.getPassword());

        UsuarioRepository.criarUsuario(
                usuario.getEmail(),
                EncryptedPassword,
                usuario.getFirstName(),
                usuario.getLastName(),
                usuario.getCpf()
        );
    }

    // PUT
    public static void atualizarUsuario(int id, UsuarioRequest usuario) throws Exception {
        if(id < 0) {
            throw new ApiException("ID inválido", 400);
        }

        String EncryptedPassword = hashPassword(usuario.getPassword());

        UsuarioRepository.atualizarUsuario(
                id,
                usuario.getEmail(),
                EncryptedPassword,
                usuario.getFirstName(),
                usuario.getLastName(),
                usuario.getCpf()
        );
    }

    // PATCH
    public static void atualizarParcialmenteUsuario(int id, UsuarioRequest usuario) throws Exception {
        if(id< 0) {
            throw new ApiException("ID inválido", 400);
        }

        String password = usuario.getPassword();

        if(password != null && !password.isEmpty()) {
            password = hashPassword(password);
        }

        UsuarioRepository.atualizarParcialmenteUsuario(
                id,
                usuario.getEmail(),
                password,
                usuario.getFirstName(),
                usuario.getLastName(),
                usuario.getCpf()
        );

    }

    // DELETE
    public static void excluirUsuario(int id) throws Exception {
        if(id <= 0){
            throw new ApiException("ID inválido", 400);
        }

        UsuarioRepository.deletarUsuario(id);
    }

    public static String hashPassword(String password) throws Exception {
        Argon2 argon2 = Argon2Factory.create();

        // Parametros: iterations, memory, paralleism
        return argon2.hash(3,65536, 1, password.toCharArray());
    }

    public static String login(String email, String password) throws Exception {
        // validação basica
        if(email == null || email.isEmpty()) {
            throw new ApiException("Email Obrigatorio", 400);
        }

        if(password == null || password.isEmpty()) {
            throw new ApiException("Password Obrigatorio", 400);
        }

        //busca usuario
        UsuarioModel user = UsuarioRepository.buscarEmail(email);

        if(user == null) {
            throw new ApiException("Usuário não encontrado", 404);
        }

        Argon2 argon2 = Argon2Factory.create();

        boolean valido = argon2.verify(user.getPassword(), password.toCharArray());

        // argon2
        if(!valido) {
            throw new ApiException("Usuário ou senha inválidos", 401);
        }

        // JWT
        return JwtUtil.generateToken(user.getEmail());

    }
}
