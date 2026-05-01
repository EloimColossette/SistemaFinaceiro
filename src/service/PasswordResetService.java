package service;

import exception.ApiException;
import model.UsuarioModel;
import repository.UsuarioRepository;
import repository.PasswordResetRepository;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

public class PasswordResetService {
    private static final Logger logger = Logger.getLogger(PasswordResetService.class.getName());

    public static void solicitarResetSenha(String email) throws Exception {
        UsuarioModel user = UsuarioRepository.buscarEmail(email);

        if (user == null) {
            throw new ApiException("Email não encontrado", 404);
        }

        String token = UUID.randomUUID().toString();
        Timestamp expiration = new Timestamp(System.currentTimeMillis() + (1000 * 60 * 15));

        PasswordResetRepository.salvarToken(user.getId(), token, expiration);
        
        EmailService.enviarEmailRecuperacao(email, token);
        logger.info("Email de recuperação enviado para : " + email);

    }

    public static void redefinirSenha(String token, String newPassword) throws Exception {
        if(!PasswordResetRepository.tokenValido(token)) {
            throw new ApiException("Token invalido ou expirado", 404);
        }

        UsuarioModel user = PasswordResetRepository.buscarPorToken(token);

        if(user == null){
            throw new ApiException("Token invalido", 404);
        }

        String passwordHash = UsuarioService.hashPassword(newPassword);

        UsuarioRepository.atualizarUsuario(
                user.getId(),
                user.getEmail(),
                passwordHash,
                user.getFirstName(),
                user.getLastName(),
                user.getCpf(),
                user.getPhoneNumber()
        );

        PasswordResetRepository.deletarToken(token);
    }
}
