package service;

import model.UsuarioModel;
import repository.UsuarioRepository;

import java.util.List;
import java.security.MessageDigest;

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

        String EncryptedPassword = passwordEncrypted(usuario.getPassword());

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

        String EncryptedPassword = passwordEncrypted(usuario.getPassword());

        UsuarioRepository.atualizarUsuario(
                usuario.getId(),
                usuario.getEmail(),
                EncryptedPassword,
                usuario.getFirst_name(),
                usuario.getLast_name(),
                usuario.getCpf()
        );
    }

    // PARTCH

    public static void atualizarParcialmenteUsuario(UsuarioModel usuario) throws Exception {
        if(usuario.getId() < 0) {
            throw new Exception("ID Invalido");
        }

        String password = usuario.getPassword();

        if(password != null && !password.isEmpty()) {
            password = passwordEncrypted(password);
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

    public static String passwordEncrypted(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(password.getBytes());

        StringBuilder sb = new StringBuilder();

        for(byte b : hash) {
            String hex = Integer.toHexString(b & 0xff);

            if(hex.length() == 1){
                sb.append("0");
            }

            sb.append(hex);
        }

        return sb.toString();
    }


}
