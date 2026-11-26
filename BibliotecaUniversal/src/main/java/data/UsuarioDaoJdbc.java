package data;

import domain.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoJdbc implements UsuarioDao {

    @Override
    public List<Usuario> select() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar usuarios", e);
        }
        return lista;
    }

    @Override
    public Usuario selectById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener usuario con id: " + id, e);
        }
        return null;
    }

    @Override
    public Usuario selectByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar usuario por email: " + email, e);
        }
        return null;
    }

    @Override
    public void insert(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, fecha_registro) VALUES (?, ?, ?)";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setDate(3, usuario.getFechaRegistro() != null ? new java.sql.Date(usuario.getFechaRegistro().getTime()) : new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al insertar usuario", e);
        }
    }

    @Override
    public void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ? WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setInt(3, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al actualizar usuario con id: " + usuario.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar usuario con id: " + id, e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getDate("fecha_registro")
        );
    }
}
