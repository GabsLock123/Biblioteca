package biblioteca.dao;

import biblioteca.model.Aluno;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO implements GenericDAO<Aluno> {

    @Override
    public Aluno findById(int id) {
        String sql = "SELECT * FROM aluno WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Aluno(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("endereco"),
                    rs.getInt("matricula")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Aluno findByMatricula(int matricula) {
        String sql = "SELECT * FROM aluno WHERE matricula = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Aluno(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("endereco"),
                    rs.getInt("matricula")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Aluno findByCPF(String cpf) {
        String sql = "SELECT * FROM aluno WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setMatricula(rs.getInt("matricula"));
                aluno.setNome(rs.getString("nome"));
                aluno.setCpf(rs.getString("cpf"));
                aluno.setEndereco(rs.getString("endereco"));
                return aluno;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Aluno> findAll() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT * FROM aluno";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alunos.add(new Aluno(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("endereco"),
                    rs.getInt("matricula")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alunos;
    }

    @Override
    public void save(Aluno aluno) {
        String sql = "INSERT INTO aluno (nome, cpf, endereco, matricula) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getCpf());
            stmt.setString(3, aluno.getEndereco());
            stmt.setInt(4, aluno.getMatricula());

            stmt.executeUpdate();

            // Obter o ID gerado automaticamente
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                aluno.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Aluno aluno) {
        String sql = "UPDATE aluno SET nome = ?, cpf = ?, endereco = ?, matricula = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getCpf());
            stmt.setString(3, aluno.getEndereco());
            stmt.setInt(4, aluno.getMatricula());
            stmt.setInt(5, aluno.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM aluno WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean temPendencias(int matricula) {
        String sql = "SELECT COUNT(*) FROM debito WHERE aluno_id = (SELECT id FROM aluno WHERE matricula = ?)";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, matricula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
