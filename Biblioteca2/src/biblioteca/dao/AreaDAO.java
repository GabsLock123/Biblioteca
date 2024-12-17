package biblioteca.dao;

import biblioteca.model.Area;
import biblioteca.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO implements GenericDAO<Area> {

    @Override
    public Area findById(int id) {
        String sql = "SELECT * FROM area WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Area(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Area> findAll() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT * FROM area";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                areas.add(new Area(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return areas;
    }

    @Override
    public void save(Area area) {
        String sql = "INSERT INTO area (nome, descricao) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, area.getNome());
            stmt.setString(2, area.getDescricao());

            stmt.executeUpdate();

            // Obter o ID gerado automaticamente
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                area.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Area area) {
        String sql = "UPDATE area SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, area.getNome());
            stmt.setString(2, area.getDescricao());
            stmt.setInt(3, area.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM area WHERE id = ?";

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
