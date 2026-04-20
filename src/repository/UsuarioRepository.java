package repository;

import database.Database;
import model.UsuarioModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    public static List<UsuarioModel> listarUsuarios() throws Exception {

        String sql = "SELECT id_user, first_name, last_name, cpf, email FROM users";

        List<UsuarioModel> users = new ArrayList<>();

        try (
                Connection connection = Database.conectar();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            while (resultSet.next()) {

                UsuarioModel user = new UsuarioModel();

                user.setId(resultSet.getInt("id_user"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setCpf(resultSet.getString("cpf"));
                user.setEmail(resultSet.getString("email"));

                users.add(user);
            }
        }

        return users;
    }

    public static void criarUsuario(String email, String password, String firstName, String lastName, String cpf) throws Exception {

        String sql = "INSERT INTO users (email, password, first_name, last_name, cpf) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, cpf);

            stmt.executeUpdate();
        }
    }

    public static void atualizarUsuario(int id, String email, String password, String firstName, String lastName, String cpf) throws Exception {

        String sql = "UPDATE users SET email = ?, password = ?, first_name = ?, last_name = ?, cpf = ? WHERE id_user = ?";

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, cpf);
            stmt.setInt(6, id);

            stmt.executeUpdate();
        }
    }

    public static void atualizarParcialmenteUsuario(int id, String email, String password, String firstName, String lastName, String cpf) throws Exception {

        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> values = new ArrayList<>();

        if (email != null && !email.isEmpty()) {
            sql.append("email = ?, ");
            values.add(email);
        }

        if (password != null && !password.isEmpty()) {
            sql.append("password = ?, ");
            values.add(password);
        }

        if (firstName != null && !firstName.isEmpty()) {
            sql.append("first_name = ?, ");
            values.add(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            sql.append("last_name = ?, ");
            values.add(lastName);
        }

        if (cpf != null && !cpf.isEmpty()) {
            sql.append("cpf = ?, ");
            values.add(cpf);
        }

        if (values.isEmpty()) {
            throw new Exception("Nenhum campo informado para atualização");
        }

        // remove última vírgula
        sql.setLength(sql.length() - 2);

        sql.append(" WHERE id_user = ?");
        values.add(id);

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            stmt.executeUpdate();
        }
    }

    public static void deletarUsuario(int id) throws Exception {

        String sql = "DELETE FROM users WHERE id_user = ?";

        try (
                Connection conn = Database.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static UsuarioModel buscarEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ?";

        try(Connection conn = Database.conectar();
        PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            stmt.setString(1, email);

            ResultSet resultSet = stmt.executeQuery();

            if(resultSet.next()){
                UsuarioModel user = new UsuarioModel();

                user.setId(resultSet.getInt("id_user"));
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setCpf(resultSet.getString("cpf"));
                user.setPassword(resultSet.getString("password"));

                return user;
            }

        }

        return null;
    }
}