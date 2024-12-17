package biblioteca.dao;

import biblioteca.model.Area;
import biblioteca.model.Autor;
import biblioteca.model.Titulo;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TituloDAO implements GenericDAO<Titulo> {
	
	private final AutorDAO autorDAO;
    private final AreaDAO areaDAO;

    public TituloDAO() {
        this.autorDAO = new AutorDAO(); // Inicializa o AutorDAO
        this.areaDAO = new AreaDAO();   // Inicializa o AreaDAO
    }

    @Override
    public Titulo findById(int id) {
        String sql = "SELECT * FROM titulo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Titulo(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("prazo"),
                    rs.getString("isbn"),
                    rs.getInt("edicao"),
                    rs.getInt("ano"),
                    new Area(rs.getInt("area_id"), null, null),
                    new Autor(rs.getInt("autor_id"), null, null, null)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Titulo> findAll() {
        String sql = "SELECT t.id, t.nome, t.isbn, t.edicao, t.ano, t.prazo, " +
                     "a.id AS autor_id, a.nome AS autor_nome, a.sobrenome AS autor_sobrenome, " +
                     "ar.id AS area_id, ar.nome AS area_nome " +
                     "FROM titulo t " +
                     "INNER JOIN autor a ON t.autor_id = a.id " +
                     "INNER JOIN area ar ON t.area_id = ar.id";

        List<Titulo> titulos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Criação e mapeamento do Título
                Titulo titulo = new Titulo();
                titulo.setId(rs.getInt("id"));
                titulo.setNome(rs.getString("nome"));
                titulo.setIsbn(rs.getString("isbn"));
                titulo.setEdicao(rs.getInt("edicao"));
                titulo.setAno(rs.getInt("ano"));
                titulo.setPrazo(rs.getInt("prazo"));

                // Criação e mapeamento do Autor
                Autor autor = new Autor();
                autor.setId(rs.getInt("autor_id"));
                autor.setNome(rs.getString("autor_nome"));
                autor.setSobrenome(rs.getString("autor_sobrenome"));
                titulo.setAutor(autor);

                // Criação e mapeamento da Área
                Area area = new Area();
                area.setId(rs.getInt("area_id"));
                area.setNome(rs.getString("area_nome"));
                titulo.setArea(area);

                // Adiciona o Título à lista
                titulos.add(titulo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar todos os títulos.", e);
        }

        return titulos;
    }
    
    public Titulo findByTitulo(String nome, String isbn, int edicao, int ano, int autorId, int areaId) {
        String sql = "SELECT * FROM titulo WHERE nome = ? AND isbn = ? AND edicao = ? AND ano = ? AND autor_id = ? AND area_id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, isbn);
            stmt.setInt(3, edicao);
            stmt.setInt(4, ano);
            stmt.setInt(5, autorId);
            stmt.setInt(6, areaId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Titulo titulo = new Titulo();
                titulo.setId(rs.getInt("id"));
                titulo.setNome(rs.getString("nome"));
                titulo.setIsbn(rs.getString("isbn"));
                titulo.setEdicao(rs.getInt("edicao"));
                titulo.setAno(rs.getInt("ano"));
                titulo.setPrazo(rs.getInt("prazo"));

                // Associa autor e área
                Autor autor = autorDAO.findById(rs.getInt("autor_id"));
                Area area = areaDAO.findById(rs.getInt("area_id"));
                titulo.setAutor(autor);
                titulo.setArea(area);

                return titulo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Titulo titulo) {
        String sql = "INSERT INTO titulo (nome, prazo, isbn, edicao, ano, area_id, autor_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, titulo.getNome());
            stmt.setInt(2, titulo.getPrazo());
            stmt.setString(3, titulo.getIsbn());
            stmt.setInt(4, titulo.getEdicao());
            stmt.setInt(5, titulo.getAno());
            stmt.setInt(6, titulo.getArea().getId());
            stmt.setInt(7, titulo.getAutor().getId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                titulo.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Titulo titulo) {
        String sql = "UPDATE titulo SET nome = ?, prazo = ?, isbn = ?, edicao = ?, ano = ?, area_id = ?, autor_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo.getNome());
            stmt.setInt(2, titulo.getPrazo());
            stmt.setString(3, titulo.getIsbn());
            stmt.setInt(4, titulo.getEdicao());
            stmt.setInt(5, titulo.getAno());
            stmt.setInt(6, titulo.getArea().getId());
            stmt.setInt(7, titulo.getAutor().getId());
            stmt.setInt(8, titulo.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM titulo WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isIsbnExistente(String isbn) {
        String sql = "SELECT COUNT(*) FROM titulo WHERE isbn = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
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
