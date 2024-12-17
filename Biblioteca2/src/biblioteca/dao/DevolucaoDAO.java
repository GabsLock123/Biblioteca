package biblioteca.dao;

import biblioteca.model.Devolucao;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucaoDAO implements GenericDAO<Devolucao> {

    @Override
    public Devolucao findById(int id) {
        String sql = "SELECT devolucao.*, emprestimo.data_emprestimo, emprestimo.data_prevista, emprestimo.valor " +
                     "FROM devolucao " +
                     "JOIN emprestimo ON devolucao.emprestimo_id = emprestimo.id " +
                     "WHERE devolucao.id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Criar o objeto Emprestimo com dados completos
                Emprestimo emprestimo = new Emprestimo(
                    rs.getInt("emprestimo_id"),
                    rs.getDate("data_emprestimo"),
                    rs.getDate("data_prevista")
                );

                // Criar o objeto Devolucao com os dados obtidos
                return new Devolucao(
                    rs.getInt("id"),
                    rs.getBoolean("atraso"),
                    rs.getDate("data_devolucao"),
                    rs.getDouble("multa"),
                    emprestimo
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Devolucao> findAll() {
        List<Devolucao> devolucoes = new ArrayList<>();
        String sql = "SELECT devolucao.*, emprestimo.data_emprestimo, emprestimo.data_prevista, emprestimo.valor " +
                     "FROM devolucao " +
                     "JOIN emprestimo ON devolucao.emprestimo_id = emprestimo.id";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Criar o objeto Emprestimo com dados completos
                Emprestimo emprestimo = new Emprestimo(
                    rs.getInt("emprestimo_id"),
                    rs.getDate("data_emprestimo"),
                    rs.getDate("data_prevista")
                );

                // Criar o objeto Devolucao com os dados obtidos
                devolucoes.add(new Devolucao(
                    rs.getInt("id"),
                    rs.getBoolean("atraso"),
                    rs.getDate("data_devolucao"),
                    rs.getDouble("multa"),
                    emprestimo
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return devolucoes;
    }

    @Override
    public void update(Devolucao devolucao) {
        String sql = "UPDATE devolucao SET atraso = ?, data_devolucao = ?, multa = ?, emprestimo_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, devolucao.isAtraso());
            stmt.setDate(2, new java.sql.Date(devolucao.getDataDevolucao().getTime()));
            stmt.setDouble(3, devolucao.getMulta());
            stmt.setInt(4, devolucao.getEmprestimo().getId());
            stmt.setInt(5, devolucao.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM devolucao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	@Override
	public void save(Devolucao devolucao) {
	    String sql = "INSERT INTO devolucao (data_devolucao, emprestimo_id) VALUES (?, ?)";

	    try (Connection conn = ConnectionFactory.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setDate(1, new java.sql.Date(devolucao.getDataDevolucao().getTime()));
	        stmt.setInt(2, devolucao.getEmprestimo().getId());

	        stmt.executeUpdate();

	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	            devolucao.setId(rs.getInt(1));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Erro ao salvar a devolução.", e);
	    }
	}
}
