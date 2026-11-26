package data;

import domain.Genero;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneroDaoJdbc implements GeneroDao {

    @Override
    public List<Genero> select() {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT * FROM generos ORDER BY nombre";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar géneros", e);
        }
        return lista;
    }

    @Override
    public Genero selectById(int id) {
        String sql = "SELECT * FROM generos WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener género con id: " + id, e);
        }
        return null;
    }

    @Override
    public void insert(Genero genero) {
        String sql = "INSERT INTO generos (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genero.getNombre());
            ps.setString(2, genero.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al insertar género", e);
        }
    }

    @Override
    public void update(Genero genero) {
        String sql = "UPDATE generos SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genero.getNombre());
            ps.setString(2, genero.getDescripcion());
            ps.setInt(3, genero.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al actualizar género con id: " + genero.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM generos WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar género con id: " + id, e);
        }
    }

    private Genero mapear(ResultSet rs) throws SQLException {
        return new Genero(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        );
    }
}
