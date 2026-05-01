package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;


public class EmailService {

    private static final String EMAIL_REMETENTE = "eloimxrl19@gmail.com";
    private static final String EMAIL_SENHA = "meyuvqxvhejbkasi";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static void enviarEmailRecuperacao(String destinatario, String token) throws Exception {
        String link = "http://localhost:8080/reset-password.html?token=" + token;

        String corpo = """
                <html>
                  <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Recuperação de Senha</h2>
                    <p>Recebemos uma solicitação para redefinir sua senha.</p>
                    <p>Clique no botão abaixo ou use o token manualmente:</p>
                    <a href="%s"
                       style="background:#4F46E5;color:white;padding:12px 24px;
                              border-radius:6px;text-decoration:none;display:inline-block;
                              margin:16px 0;">
                      Redefinir Senha
                    </a>
                    <p>Ou copie o token: <strong>%s</strong></p>
                    <p style="color:#888;font-size:12px;">
                      Este link expira em 15 minutos. Se não foi você, ignore este email.
                    </p>
                  </body>
                </html>
                """.formatted(link, token);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_REMETENTE, EMAIL_SENHA);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_REMETENTE));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject("Recuperação de Senha");
        message.setContent(corpo, "text/html; charset=utf-8");


        Transport.send(message);
    }
}
