package data;

import domain.Autor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDaoJdbc implements AutorDao {

    @Override
    public List<Autor> select() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autores ORDER BY nombre";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar autores", e);
        }
        return lista;
    }

    @Override
    public Autor selectById(int id) {
        String sql = "SELECT * FROM autores WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener autor con id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Autor> selectByNombre(String nombre) {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autores WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar autores por nombre: " + nombre, e);
        }
        return lista;
    }

    @Override
    public void insert(Autor autor) {
        String sql = "INSERT INTO autores (nombre, nacionalidad, fecha_nacimiento, fecha_muerte) VALUES (?, ?, ?, ?)";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getNacionalidad());
            ps.setDate(3, autor.getFechaNacimiento() != null ? new java.sql.Date(autor.getFechaNacimiento().getTime()) : null);
            ps.setDate(4, autor.getFechaMuerte() != null ? new java.sql.Date(autor.getFechaMuerte().getTime()) : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al insertar autor", e);
        }
    }

    @Override
    public void update(Autor autor) {
        String sql = "UPDATE autores SET nombre = ?, nacionalidad = ?, fecha_nacimiento = ?, fecha_muerte = ? WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getNacionalidad());
            ps.setDate(3, autor.getFechaNacimiento() != null ? new java.sql.Date(autor.getFechaNacimiento().getTime()) : null);
            ps.setDate(4, autor.getFechaMuerte() != null ? new java.sql.Date(autor.getFechaMuerte().getTime()) : null);
            ps.setInt(5, autor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al actualizar autor con id: " + autor.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM autores WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar autor con id: " + id, e);
        }
    }

    private Autor mapear(ResultSet rs) throws SQLException {
        return new Autor(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("nacionalidad"),
            rs.getDate("fecha_nacimiento"),
            rs.getDate("fecha_muerte")
        );
    }
}
