package data;

import domain.Prestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDaoJdbc implements PrestamoDao {

    @Override
    public List<Prestamo> select() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.libro_id, p.usuario_id, p.fecha_prestamo, " +
                     "p.fecha_devolucion, p.estado, " +
                     "l.titulo as libro_titulo, u.nombre as usuario_nombre " +
                     "FROM prestamos p " +
                     "LEFT JOIN libros l ON p.libro_id = l.id " +
                     "LEFT JOIN usuarios u ON p.usuario_id = u.id " +
                     "ORDER BY p.fecha_prestamo DESC";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar préstamos", e);
        }
        return lista;
    }

    @Override
    public Prestamo selectById(int id) {
        String sql = "SELECT p.id, p.libro_id, p.usuario_id, p.fecha_prestamo, " +
                     "p.fecha_devolucion, p.estado, " +
                     "l.titulo as libro_titulo, u.nombre as usuario_nombre " +
                     "FROM prestamos p " +
                     "LEFT JOIN libros l ON p.libro_id = l.id " +
                     "LEFT JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener préstamo con id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Prestamo> selectByUsuario(int usuarioId) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.libro_id, p.usuario_id, p.fecha_prestamo, " +
                     "p.fecha_devolucion, p.estado, " +
                     "l.titulo as libro_titulo, u.nombre as usuario_nombre " +
                     "FROM prestamos p " +
                     "LEFT JOIN libros l ON p.libro_id = l.id " +
                     "LEFT JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.usuario_id = ? " +
                     "ORDER BY p.fecha_prestamo DESC";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener préstamos por usuario: " + usuarioId, e);
        }
        return lista;
    }

    @Override
    public List<Prestamo> selectByLibro(int libroId) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.libro_id, p.usuario_id, p.fecha_prestamo, " +
                     "p.fecha_devolucion, p.estado, " +
                     "l.titulo as libro_titulo, u.nombre as usuario_nombre " +
                     "FROM prestamos p " +
                     "LEFT JOIN libros l ON p.libro_id = l.id " +
                     "LEFT JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.libro_id = ? " +
                     "ORDER BY p.fecha_prestamo DESC";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener préstamos por libro: " + libroId, e);
        }
        return lista;
    }

    @Override
    public List<Prestamo> selectByEstado(String estado) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.libro_id, p.usuario_id, p.fecha_prestamo, " +
                     "p.fecha_devolucion, p.estado, " +
                     "l.titulo as libro_titulo, u.nombre as usuario_nombre " +
                     "FROM prestamos p " +
                     "LEFT JOIN libros l ON p.libro_id = l.id " +
                     "LEFT JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.estado = ? " +
                     "ORDER BY p.fecha_prestamo DESC";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener préstamos por estado: " + estado, e);
        }
        return lista;
    }

    @Override
    public void insert(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getLibroId());
            ps.setInt(2, prestamo.getUsuarioId());
            ps.setDate(3, prestamo.getFechaPrestamo() != null ? new java.sql.Date(prestamo.getFechaPrestamo().getTime()) : new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(4, prestamo.getFechaDevolucion() != null ? new java.sql.Date(prestamo.getFechaDevolucion().getTime()) : null);
            ps.setString(5, prestamo.getEstado());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al insertar préstamo", e);
        }
    }

    @Override
    public void update(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET libro_id = ?, usuario_id = ?, fecha_prestamo = ?, fecha_devolucion = ?, estado = ? WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getLibroId());
            ps.setInt(2, prestamo.getUsuarioId());
            ps.setDate(3, prestamo.getFechaPrestamo() != null ? new java.sql.Date(prestamo.getFechaPrestamo().getTime()) : null);
            ps.setDate(4, prestamo.getFechaDevolucion() != null ? new java.sql.Date(prestamo.getFechaDevolucion().getTime()) : null);
            ps.setString(5, prestamo.getEstado());
            ps.setInt(6, prestamo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al actualizar préstamo con id: " + prestamo.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar préstamo con id: " + id, e);
        }
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo(
            rs.getInt("id"),
            rs.getInt("libro_id"),
            rs.getInt("usuario_id"),
            rs.getDate("fecha_prestamo"),
            rs.getDate("fecha_devolucion"),
            rs.getString("estado")
        );
        prestamo.setLibroTitulo(rs.getString("libro_titulo"));
        prestamo.setUsuarioNombre(rs.getString("usuario_nombre"));
        return prestamo;
    }
}
