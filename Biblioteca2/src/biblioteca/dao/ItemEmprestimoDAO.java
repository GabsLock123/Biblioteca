package biblioteca.dao;

import biblioteca.model.ItemEmprestimo;
import biblioteca.model.Livro;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemEmprestimoDAO implements GenericDAO<ItemEmprestimo> {

    @Override
    public ItemEmprestimo findById(int id) {
        String sql = "SELECT * FROM item_emprestimo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ItemEmprestimo item = new ItemEmprestimo();
                item.setId(rs.getInt("id"));
                item.setDataDevolucao(rs.getDate("data_devolucao"));
                item.setDataPrevista(rs.getDate("data_prevista"));
                item.setLivros(findLivrosByItemEmprestimoId(item.getId())); // Associa os livros
                return item;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca todos os itens de empréstimo associados a um empréstimo específico.
     * 
     * @param emprestimoId ID do empréstimo.
     * @return Lista de itens de empréstimo.
     */
    public List<ItemEmprestimo> findByEmprestimoId(int emprestimoId) {
        String sql = "SELECT * FROM item_emprestimo WHERE emprestimo_id = ?";
        List<ItemEmprestimo> itens = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItemEmprestimo item = new ItemEmprestimo();
                item.setId(rs.getInt("id"));
                item.setDataDevolucao(rs.getDate("data_devolucao"));
                item.setDataPrevista(rs.getDate("data_prevista"));
                item.setLivros(findLivrosByItemEmprestimoId(item.getId())); // Associa os livros
                itens.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itens;
    }

    @Override
    public List<ItemEmprestimo> findAll() {
        List<ItemEmprestimo> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_emprestimo";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ItemEmprestimo item = new ItemEmprestimo();
                item.setId(rs.getInt("id"));
                item.setDataDevolucao(rs.getDate("data_devolucao"));
                item.setDataPrevista(rs.getDate("data_prevista"));
                item.setLivros(findLivrosByItemEmprestimoId(item.getId())); // Associa os livros
                itens.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itens;
    }

    @Override
    public void save(ItemEmprestimo item) {
        String sql = "INSERT INTO item_emprestimo (data_devolucao, data_prevista, emprestimo_id) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, item.getDataDevolucao() != null ? new java.sql.Date(item.getDataDevolucao().getTime()) : null);
            stmt.setDate(2, new java.sql.Date(item.getDataPrevista().getTime()));
            stmt.setInt(3, item.getEmprestimo().getId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ItemEmprestimo item) {
        String sql = "UPDATE item_emprestimo SET data_devolucao = ?, data_prevista = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, item.getDataDevolucao() != null ? new java.sql.Date(item.getDataDevolucao().getTime()) : null);
            stmt.setDate(2, new java.sql.Date(item.getDataPrevista().getTime()));
            stmt.setInt(3, item.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM item_emprestimo WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca os livros associados a um item de empréstimo.
     *
     * @param itemId ID do item de empréstimo.
     * @return Lista de livros associados.
     */
    private List<Livro> findLivrosByItemEmprestimoId(int itemId) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT livro.id, livro.disponivel, livro.exemplar_biblioteca, titulo.id AS titulo_id, titulo.nome AS titulo_nome, titulo.isbn " +
                "FROM item_emprestimo_livro " +
                "JOIN livro ON item_emprestimo_livro.livro_id = livro.id " +
                "JOIN titulo ON livro.titulo_id = titulo.id " +
                "WHERE item_emprestimo_livro.item_emprestimo_id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setDisponivel(rs.getBoolean("disponivel"));
                livro.setExemplarBiblioteca(rs.getBoolean("exemplar_biblioteca"));
                // Criação do título associado ao livro
                livro.setTitulo(new biblioteca.model.Titulo(
                        rs.getInt("titulo_id"),
                        rs.getString("titulo_nome"),
                        rs.getString("isbn")
                ));
                livros.add(livro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return livros;
    }
}
