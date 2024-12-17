package biblioteca.dao;

import biblioteca.model.Autor;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO implements GenericDAO<Autor> {

    @Override
    public Autor findById(int id) {
        String sql = "SELECT * FROM autor WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Autor(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("sobrenome"),
                    rs.getString("titulacao")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Autor> findAll() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT * FROM autor";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                autores.add(new Autor(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("sobrenome"),
                    rs.getString("titulacao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return autores;
    }

    @Override
    public void save(Autor autor) {
        String sql = "INSERT INTO autor (nome, sobrenome, titulacao) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getSobrenome());
            stmt.setString(3, autor.getTitulacao());

            stmt.executeUpdate();

            // Obter o ID gerado automaticamente
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                autor.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Autor autor) {
        String sql = "UPDATE autor SET nome = ?, sobrenome = ?, titulacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getSobrenome());
            stmt.setString(3, autor.getTitulacao());
            stmt.setInt(4, autor.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM autor WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar os títulos de um autor
    public List<Integer> findTitulosByAutorId(int autorId) {
        List<Integer> tituloIds = new ArrayList<>();
        String sql = "SELECT titulo_id FROM autor_titulo WHERE autor_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, autorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tituloIds.add(rs.getInt("titulo_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tituloIds;
    }
}
