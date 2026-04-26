package service;

import exception.ApiException;
import model.UsuarioModel;
import repository.UsuarioRepository;
import dto.UsuarioRequest;
import java.util.Hashtable;
import java.util.List;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import javax.naming.directory.*;

public class UsuarioService {

    private static final Argon2 argon2 = Argon2Factory.create();

    // GET
    public static List<UsuarioModel> listarUsuarios() throws Exception {
        return UsuarioRepository.listarUsuarios();
    }

    // POST
    public static void criarUsuario(UsuarioRequest usuario) throws Exception {

        if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new ApiException("Password Obrigatorio", 400);
        }

        validarTelefone(usuario.getPhoneNumber());

        validarEmail(usuario.getEmail());

        String EncryptedPassword = hashPassword(usuario.getPassword());

        UsuarioRepository.criarUsuario(
                usuario.getEmail(),
                EncryptedPassword,
                usuario.getFirstName(),
                usuario.getLastName(),
                usuario.getCpf(),
                usuario.getPhoneNumber()
        );
    }

    // PUT
    public static void atualizarUsuario(int id, UsuarioRequest usuario) throws Exception {
        if(id <= 0) {
            throw new ApiException("ID inválido", 400);
        }

        validarTelefone(usuario.getPhoneNumber());

        validarEmail(usuario.getEmail());

        String password = usuario.getPassword();

        if(password != null && !password.isEmpty()) {
            password = hashPassword(password);
        }

        UsuarioRepository.atualizarUsuario(
                id,
                usuario.getEmail(),
                password,
                usuario.getFirstName(),
                usuario.getLastName(),
                usuario.getCpf(),
                usuario.getPhoneNumber()
        );
    }

    // PATCH
    public static void atualizarParcialmenteUsuario(int id, UsuarioRequest usuario) throws Exception {
        if(id< 0) {
            throw new ApiException("ID inválido", 400);
        }

        if(usuario.getPhoneNumber() != null && !usuario.getPhoneNumber().isEmpty()) {
            validarTelefone(usuario.getPhoneNumber());
        }

        String password = usuario.getPassword();

        if(password != null && !password.isEmpty()) {
            password = hashPassword(password);
        }

        if(usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            validarEmail(usuario.getEmail());
        }

        UsuarioRepository.atualizarParcialmenteUsuario(
                id,
                usuario.getEmail(),
                password,
                usuario.getFirstName(),
                usuario.getLastName(),
                usuario.getCpf(),
                usuario.getPhoneNumber()
        );

    }

    // DELETE
    public static void excluirUsuario(int id) throws Exception {
        if(id <= 0){
            throw new ApiException("ID inválido", 400);
        }

        UsuarioRepository.deletarUsuario(id);
    }

    private static void validarTelefone(String phone){
        if (phone == null || phone.isEmpty()) {
            throw new ApiException("Telefone obrigatório", 400);
        }

        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            throw new ApiException("Telefone inválido", 400);
        }
    }

    private static void validarEmail(String email){
        if (email == null || email.isEmpty()) {
            throw new ApiException("Email Obrigatorio", 400);
        }

        // Validação de formato
        if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            throw new ApiException("Email Inválido", 400);
        }

        // Validação de Domino (MX)
        if(!dominioExiste(email)){
            throw new ApiException("Domínio de email invalido", 400);
        }
    }

    private static boolean dominioExiste(String email){
        try{
            String domain = email.substring(email.indexOf("@") + 1);

            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            return attr != null && attr.size() > 0;

        }catch (Exception e){
            return false;
        }
    }

    public static String hashPassword(String password) throws Exception {

        // Parametros: iterations, memory, paralleism
        return argon2.hash(3,65536, 1, password.toCharArray());
    }
}
