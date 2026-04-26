package repository;

import database.Database;
import model.UsuarioModel;

import java.sql.*;

public class PasswordResetRepository {

    public static void salvarToken(int userId, String token, Timestamp expiration) throws Exception {

        String sql = "INSERT INTO password_reset_tokens (user_id, token, expiration) VALUES (?, ?, ?)";

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            stmt.setTimestamp(3, expiration);

            stmt.executeUpdate();
        }
    }

    public static UsuarioModel buscarPorToken(String token) throws Exception {

        String sql = """
                SELECT u.* 
                FROM users u
                JOIN password_reset_tokens t ON u.id_user = t.user_id
                WHERE t.token = ?
        """;

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UsuarioModel user = new UsuarioModel();

                user.setId(rs.getInt("id_user"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));

                return user;
            }
        }

        return null;
    }

    public static boolean tokenValido(String token) throws Exception {

        String sql = "SELECT expiration FROM password_reset_tokens WHERE token = ?";

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expiration = rs.getTimestamp("expiration");
                return expiration.after(new Timestamp(System.currentTimeMillis()));
            }
        }

        return false;
    }

    public static void deletarToken(String token) throws Exception {

        String sql = "DELETE FROM password_reset_tokens WHERE token = ?";

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        }
    }
}