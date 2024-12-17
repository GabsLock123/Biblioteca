package biblioteca.dao;

import biblioteca.model.Area;
import biblioteca.model.Autor;
import biblioteca.model.Livro;
import biblioteca.model.Titulo;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO implements GenericDAO<Livro> {
	
	
	public List<Livro> findAllDisponiveis() {
	    List<Livro> livrosDisponiveis = new ArrayList<>();
	    String sql = "SELECT l.id AS livro_id, l.disponivel, l.exemplar_biblioteca, " +
	            "t.id AS titulo_id, t.nome AS titulo_nome, t.isbn, t.prazo, t.edicao, t.ano " +
	            "FROM livro l " +
	            "INNER JOIN titulo t ON l.titulo_id = t.id " +
	            "WHERE l.disponivel = TRUE";

	    try (Connection conn = ConnectionFactory.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Titulo titulo = new Titulo();
	            titulo.setId(rs.getInt("titulo_id"));
	            titulo.setNome(rs.getString("titulo_nome"));
	            titulo.setIsbn(rs.getString("isbn"));
	            titulo.setPrazo(rs.getInt("prazo"));
	            titulo.setEdicao(rs.getInt("edicao"));
	            titulo.setAno(rs.getInt("ano"));

	            Livro livro = new Livro();
	            livro.setId(rs.getInt("livro_id"));
	            livro.setDisponivel(rs.getBoolean("disponivel"));
	            livro.setExemplarBiblioteca(rs.getBoolean("exemplar_biblioteca"));
	            livro.setTitulo(titulo);

	            livrosDisponiveis.add(livro);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Erro ao buscar livros disponíveis.", e);
	    }

	    return livrosDisponiveis;
	}


    @Override
    public Livro findById(int id) {
        String sql = "SELECT livro.*, titulo.nome AS titulo_nome, titulo.prazo, titulo.isbn, titulo.edicao, titulo.ano, titulo.area_id, titulo.autor_id " +
                     "FROM livro " +
                     "JOIN titulo ON livro.titulo_id = titulo.id " +
                     "WHERE livro.id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Criação de objetos associados
                Area area = new Area(rs.getInt("area_id"), null, null); // Inicialização parcial de Area
                Autor autor = new Autor(rs.getInt("autor_id"), null, null, null); // Inicialização parcial de Autor
                Titulo titulo = new Titulo(
                    rs.getInt("titulo_id"),
                    rs.getString("titulo_nome"),
                    rs.getInt("prazo"),
                    rs.getString("isbn"),
                    rs.getInt("edicao"),
                    rs.getInt("ano"),
                    area,
                    autor
                );

                return new Livro(
                    rs.getInt("id"),
                    rs.getBoolean("disponivel"),
                    rs.getBoolean("exemplar_biblioteca"),
                    titulo
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Livro> findAll() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT livro.*, titulo.nome AS titulo_nome, titulo.isbn, titulo.prazo " +
                     "FROM livro " +
                     "INNER JOIN titulo ON livro.titulo_id = titulo.id";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
            throw new RuntimeException("Erro ao buscar todos os livros.", e);
        }

        return livros;
    }


    @Override
    public void save(Livro livro) {
        String sql = "INSERT INTO livro (disponivel, exemplar_biblioteca, titulo_id) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBoolean(1, livro.isDisponivel());
            stmt.setBoolean(2, livro.isExemplarBiblioteca());
            stmt.setInt(3, livro.getTitulo().getId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                livro.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Livro livro) {
        String sql = "UPDATE livro SET disponivel = ?, exemplar_biblioteca = ?, titulo_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, livro.isDisponivel());
            stmt.setBoolean(2, livro.isExemplarBiblioteca());
            stmt.setInt(3, livro.getTitulo().getId());
            stmt.setInt(4, livro.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM livro WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean existeExemplarBibliotecaParaTitulo(int tituloId) {
        String sql = "SELECT COUNT(*) AS total FROM livro WHERE titulo_id = ? AND exemplar_biblioteca = true";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tituloId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0; // Retorna true se já existe um exemplar fixo
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao verificar exemplares fixos no banco de dados.", e);
        }
        return false;
    }
    
    public boolean isLivroDuplicado(Livro livro) {
        String sql = "SELECT COUNT(*) FROM livro WHERE titulo_id = ? AND exemplar_biblioteca = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livro.getTitulo().getId());
            stmt.setBoolean(2, livro.isExemplarBiblioteca());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void atualizarDisponibilidade(int livroId, boolean disponivel) {
        String sql = "UPDATE livro SET disponivel = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, disponivel);
            stmt.setInt(2, livroId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar disponibilidade do livro.", e);
        }
    }

    
    public void associarLivroAItemEmprestimo(int itemEmprestimoId, int livroId) {
        String sql = "INSERT INTO item_emprestimo_livro (item_emprestimo_id, livro_id) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemEmprestimoId);
            stmt.setInt(2, livroId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao associar livro ao item de empréstimo.", e);
        }
    }




    
}
