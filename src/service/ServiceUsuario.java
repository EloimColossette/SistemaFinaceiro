package service;

import model.UsuarioModel;
import repository.UsuarioRepository;
import security.JwtUtil;

import java.util.List;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class ServiceUsuario {

    // GET
    public static List<String> listarUsuarios() throws Exception {
        return UsuarioRepository.listarUsuarios();
    }

    // POST
    public static void criarUsuario(UsuarioModel usuario) throws Exception {
        // Validação Simples
        if(usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new Exception("Email Obrigatorio");
        }
        if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new Exception("Password Obrigatorio");
        }

        String EncryptedPassword = hashPassword(usuario.getPassword());

        UsuarioRepository.criarUsuario(
                usuario.getEmail(),
                EncryptedPassword,
                usuario.getFirst_name(),
                usuario.getLast_name(),
                usuario.getCpf()
        );
    }

    // PUT
    public static void atualizarUsuario(UsuarioModel usuario) throws Exception {
        if(usuario.getId() < 0) {
            throw new Exception("ID Invalido");
        }

        String EncryptedPassword = hashPassword(usuario.getPassword());

        UsuarioRepository.atualizarUsuario(
                usuario.getId(),
                usuario.getEmail(),
                EncryptedPassword,
                usuario.getFirst_name(),
                usuario.getLast_name(),
                usuario.getCpf()
        );
    }

    // PATCH
    public static void atualizarParcialmenteUsuario(UsuarioModel usuario) throws Exception {
        if(usuario.getId() < 0) {
            throw new Exception("ID Invalido");
        }

        String password = usuario.getPassword();

        if(password != null && !password.isEmpty()) {
            password = hashPassword(password);
        }

        UsuarioRepository.atualizarParcialmenteUsuario(
                usuario.getId(),
                usuario.getEmail(),
                password,
                usuario.getFirst_name(),
                usuario.getLast_name(),
                usuario.getCpf()
        );

    }

    // DELETE
    public static void excluirUsuario(int id) throws Exception {
        if(id <= 0){
            throw new Exception("ID Invalido");
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
            throw new Exception("Email Obrigatorio");
        }

        if(password == null || password.isEmpty()) {
            throw new Exception("Password Obrigatorio");
        }

        //busca usuario
        UsuarioModel user = UsuarioRepository.buscarEmail(email);

        if(user == null) {
            throw new Exception("Usuario nao encontrado");
        }

        Argon2 argon2 = Argon2Factory.create();

        boolean valido = argon2.verify(user.getPassword(), password.toCharArray());

        // argon2
        if(!valido) {
            throw new Exception("Usuario ou Senha Invalidos");
        }

        // JWT
        return JwtUtil.generateToken(user.getEmail());

    }
}
