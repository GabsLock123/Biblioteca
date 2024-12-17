package biblioteca.dao;

import biblioteca.model.Aluno;
import biblioteca.model.Debito;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebitoDAO implements GenericDAO<Debito> {

    @Override
    public Debito findById(int id) {
        String sql = "SELECT * FROM debito WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Debito(
                    rs.getInt("id"),
                    rs.getInt("valor"),
                    rs.getDate("data"),
                    new Aluno(rs.getInt("aluno_id"), null, null, null, 0) 
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Debito> findAll() {
        List<Debito> debitos = new ArrayList<>();
        String sql = "SELECT * FROM debito";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                debitos.add(new Debito(
                    rs.getInt("id"),
                    rs.getInt("valor"),
                    rs.getDate("data"),
                    new Aluno(rs.getInt("aluno_id"), null, null, null, 0) // Associação com Aluno
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return debitos;
    }

    @Override
    public void save(Debito debito) {
        String sql = "INSERT INTO debito (valor, data, aluno_id) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, debito.getValor());
            stmt.setDate(2, new java.sql.Date(debito.getData().getTime()));
            stmt.setInt(3, debito.getAluno().getId());

            stmt.executeUpdate();

            // Obter o ID gerado automaticamente
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                debito.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Debito debito) {
        String sql = "UPDATE debito SET valor = ?, data = ?, aluno_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, debito.getValor());
            stmt.setDate(2, new java.sql.Date(debito.getData().getTime()));
            stmt.setInt(3, debito.getAluno().getId());
            stmt.setInt(4, debito.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM debito WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar todos os débitos de um aluno
    public List<Debito> findByAlunoId(int alunoId) {
        List<Debito> debitos = new ArrayList<>();
        String sql = "SELECT * FROM debito WHERE aluno_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alunoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                debitos.add(new Debito(
                    rs.getInt("id"),
                    rs.getInt("valor"),
                    rs.getDate("data"),
                    new Aluno(alunoId, null, null, null, 0) // Associação com Aluno
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return debitos;
    }
}
