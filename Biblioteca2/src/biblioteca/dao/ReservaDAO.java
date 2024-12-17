package biblioteca.dao;

import biblioteca.model.Aluno;
import biblioteca.model.Livro;
import biblioteca.model.Reserva;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservaDAO implements GenericDAO<Reserva> {

    @Override
    public Reserva findById(int id) {
        String sql = "SELECT * FROM reserva WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Reserva(
                    rs.getInt("id"),
                    rs.getDate("data"),
                    new Aluno(rs.getInt("aluno_id"), null, null, null, 0), // Associação com Aluno
                    new Livro(rs.getInt("livro_id"), false, false, null)  // Associação com Livro
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Reserva> findAll() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reservas.add(new Reserva(
                    rs.getInt("id"),
                    rs.getDate("data"),
                    new Aluno(rs.getInt("aluno_id"), null, null, null, 0),
                    new Livro(rs.getInt("livro_id"), false, false, null)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservas;
    }

    @Override
    public void save(Reserva reserva) {
        String sql = "INSERT INTO reserva (data, aluno_id, livro_id, ativa) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, new java.sql.Date(reserva.getData().getTime()));
            stmt.setInt(2, reserva.getAluno().getId());
            stmt.setInt(3, reserva.getLivro().getId());
            stmt.setBoolean(4, reserva.isAtiva());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                reserva.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reserva reserva) {
        String sql = "UPDATE reserva SET data = ?, aluno_id = ?, livro_id = ?, ativa = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(reserva.getData().getTime()));
            stmt.setInt(2, reserva.getAluno().getId());
            stmt.setInt(3, reserva.getLivro().getId());
            stmt.setBoolean(4, reserva.isAtiva());
            stmt.setInt(5, reserva.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reserva WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void marcarReservaComoConcluida(int reservaId) {
        String sql = "UPDATE reserva SET ativa = false WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao concluir reserva.", e);
        }
    }
    
    public List<Reserva> findReservasAtivasPorLivro(int livroId) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE livro_id = ? AND ativa = TRUE";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livroId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setId(rs.getInt("id"));
                reserva.setData(rs.getDate("data"));
                reserva.setAtiva(true);

                reservas.add(reserva);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar reservas ativas para o livro.", e);
        }
        return reservas;
    }
    
    public void updateLivro(Livro livro) {
        String sql = "UPDATE livro SET disponivel = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, livro.isDisponivel());
            stmt.setInt(2, livro.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar disponibilidade do livro na reserva.", e);
        }
    }
}
