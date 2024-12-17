package biblioteca.dao;

import biblioteca.model.Aluno;
import biblioteca.model.Emprestimo;
import biblioteca.model.ItemEmprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Titulo;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmprestimoDAO implements GenericDAO<Emprestimo> {

	@Override
    public Emprestimo findById(int id) {
        String sql = "SELECT * FROM emprestimo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setId(rs.getInt("id"));
                emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
                emprestimo.setDataPrevista(rs.getDate("data_prevista"));
                // Não precisamos carregar `aluno` neste método, pois o foco é salvar
                return emprestimo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Emprestimo> findAll() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimo";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Emprestimo emprestimo = new Emprestimo(
                    rs.getInt("id"),
                    rs.getDate("data_emprestimo"),
                    rs.getDate("data_prevista")
                );

                // Carregar itens associados ao emprestimo
                emprestimo.setItensEmprestimo(findItensByEmprestimoId(emprestimo.getId()));
                emprestimos.add(emprestimo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emprestimos;
    }

    @Override
    public void save(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimo (data_emprestimo, data_prevista, aluno_id) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));
            stmt.setDate(2, new java.sql.Date(emprestimo.getDataPrevista().getTime()));
            stmt.setInt(3, emprestimo.getAluno().getId()); // Adicionar ID do aluno corretamente

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                emprestimo.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar o empréstimo.", e);
        }
    }


    @Override
    public void update(Emprestimo emprestimo) {
        String sql = "UPDATE emprestimo SET data_emprestimo = ?, data_prevista = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));
            stmt.setDate(2, new java.sql.Date(emprestimo.getDataPrevista().getTime()));
            stmt.setInt(4, emprestimo.getId());

            stmt.executeUpdate();

            // Atualizar itens associados (remoção e reinserção simplificada)
            deleteItensByEmprestimoId(emprestimo.getId());
            if (emprestimo.getItensEmprestimo() != null) {
                saveItens(emprestimo.getItensEmprestimo(), emprestimo.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM emprestimo WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Remover itens associados antes de deletar o emprestimo
            deleteItensByEmprestimoId(id);

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Métodos auxiliares

    private List<ItemEmprestimo> findItensByEmprestimoId(int emprestimoId) {
        List<ItemEmprestimo> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_emprestimo WHERE emprestimo_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(new ItemEmprestimo(
                    rs.getInt("id"),
                    rs.getDate("data_devolucao"),
                    rs.getDate("data_prevista")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itens;
    }

    private void saveItens(List<ItemEmprestimo> itens, int emprestimoId) {
        String sql = "INSERT INTO item_emprestimo (id, data_devolucao, data_prevista, emprestimo_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (ItemEmprestimo item : itens) {
                stmt.setInt(1, item.getId());
                stmt.setDate(2, new java.sql.Date(item.getDataDevolucao().getTime()));
                stmt.setDate(3, new java.sql.Date(item.getDataPrevista().getTime()));
                stmt.setInt(4, emprestimoId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItensByEmprestimoId(int emprestimoId) {
        String sql = "DELETE FROM item_emprestimo WHERE emprestimo_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Emprestimo> findEmprestimosPendentesPorAluno(int alunoId) {
        List<Emprestimo> emprestimosPendentes = new ArrayList<>();
        String sql = "SELECT e.* FROM emprestimo e " +
                     "WHERE e.aluno_id = ? AND NOT EXISTS (" +
                     "    SELECT 1 FROM devolucao d WHERE d.emprestimo_id = e.id" +
                     ")";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alunoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setId(rs.getInt("id"));
                emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
                emprestimo.setDataPrevista(rs.getDate("data_prevista"));

                emprestimosPendentes.add(emprestimo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar empréstimos pendentes.", e);
        }
        return emprestimosPendentes;
    }
    
    public Emprestimo findEmprestimoPorLivro(int livroId) {
        String sql = "SELECT e.* FROM emprestimo e " +
                     "INNER JOIN item_emprestimo ie ON e.id = ie.emprestimo_id " +
                     "INNER JOIN item_emprestimo_livro iel ON ie.id = iel.item_emprestimo_id " +
                     "WHERE iel.livro_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livroId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setId(rs.getInt("id"));
                emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
                emprestimo.setDataPrevista(rs.getDate("data_prevista"));
                return emprestimo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar empréstimo associado ao livro.", e);
        }
        return null;
    }
    
    public void marcarComoConcluido(int emprestimoId) {
        String sql = "UPDATE emprestimo SET concluido = TRUE WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emprestimoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
  
    public List<Emprestimo> findAllAtivos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT e.id AS emprestimo_id, e.data_emprestimo, e.data_prevista, " +
                     "a.id AS aluno_id, a.nome AS aluno_nome, a.matricula AS aluno_matricula " +
                     "FROM emprestimo e " +
                     "INNER JOIN aluno a ON e.aluno_id = a.id " +
                     "WHERE e.concluido = false";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Criação do objeto Aluno
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("aluno_id"));
                aluno.setNome(rs.getString("aluno_nome"));
                aluno.setMatricula(rs.getInt("aluno_matricula"));

                // Criação do objeto Emprestimo
                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setId(rs.getInt("emprestimo_id"));
                emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
                emprestimo.setDataPrevista(rs.getDate("data_prevista"));
                emprestimo.setAluno(aluno); // Associa o aluno ao empréstimo

                emprestimos.add(emprestimo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar empréstimos ativos.", e);
        }

        return emprestimos;
    }


    public void concluirEmprestimo(int emprestimoId) {
        String sql = "UPDATE emprestimo SET concluido = true WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao concluir o empréstimo.", e);
        }
    }

    public List<Livro> findLivrosByEmprestimoId(int emprestimoId) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT livro.id, livro.disponivel, livro.exemplar_biblioteca, " +
                "titulo.id AS titulo_id, titulo.nome AS titulo_nome, titulo.isbn, titulo.prazo " +
                "FROM livro " +
                "INNER JOIN item_emprestimo_livro ON livro.id = item_emprestimo_livro.livro_id " +
                "INNER JOIN item_emprestimo ON item_emprestimo_livro.item_emprestimo_id = item_emprestimo.id " +
                "WHERE item_emprestimo.emprestimo_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setDisponivel(rs.getBoolean("disponivel"));
                livro.setExemplarBiblioteca(rs.getBoolean("exemplar_biblioteca"));

                Titulo titulo = new Titulo();
                titulo.setId(rs.getInt("titulo_id"));
                titulo.setNome(rs.getString("titulo_nome"));
                titulo.setIsbn(rs.getString("isbn"));
                titulo.setPrazo(rs.getInt("prazo"));
                livro.setTitulo(titulo);

                livros.add(livro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar livros associados ao empréstimo.", e);
        }

        return livros;
    }

}
