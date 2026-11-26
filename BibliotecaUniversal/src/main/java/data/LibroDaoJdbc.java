package data;

import domain.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDaoJdbc implements LibroDao {

    @Override
    public List<Libro> select() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.id, l.titulo, l.anio, l.autor_id, l.genero_id, " +
                     "a.nombre as autor_nombre, g.nombre as genero_nombre " +
                     "FROM libros l " +
                     "LEFT JOIN autores a ON l.autor_id = a.id " +
                     "LEFT JOIN generos g ON l.genero_id = g.id " +
                     "ORDER BY l.titulo";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar libros", e);
        }
        return lista;
    }

    @Override
    public Libro selectById(int id) {
        String sql = "SELECT l.id, l.titulo, l.anio, l.autor_id, l.genero_id " +
                     "FROM libros l WHERE l.id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearBasic(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener libro por id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Libro> selectByTexto(String texto) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.id, l.titulo, l.anio, l.autor_id, l.genero_id, " +
                     "a.nombre as autor_nombre, g.nombre as genero_nombre " +
                     "FROM libros l " +
                     "LEFT JOIN autores a ON l.autor_id = a.id " +
                     "LEFT JOIN generos g ON l.genero_id = g.id " +
                     "WHERE l.titulo LIKE ? OR a.nombre LIKE ? " +
                     "ORDER BY l.titulo";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String busqueda = "%" + texto + "%";
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar libros por texto: " + texto, e);
        }
        return lista;
    }

    @Override
    public List<Libro> selectWithDetails() {
        return select();
    }

    @Override
    public Libro selectWithDetailsById(int id) {
        String sql = "SELECT l.id, l.titulo, l.anio, l.autor_id, l.genero_id, " +
                     "a.nombre as autor_nombre, a.nacionalidad as autor_nacionalidad, " +
                     "g.nombre as genero_nombre " +
                     "FROM libros l " +
                     "LEFT JOIN autores a ON l.autor_id = a.id " +
                     "LEFT JOIN generos g ON l.genero_id = g.id " +
                     "WHERE l.id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener detalle de libro con id: " + id, e);
        }
        return null;
    }

    @Override
    public void insert(Libro libro) {
        String sql = "INSERT INTO libros (titulo, anio, autor_id, genero_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAnio());
            ps.setInt(3, libro.getAutorId());
            ps.setInt(4, libro.getGeneroId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al insertar libro", e);
        }
    }

    @Override
    public void update(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, anio = ?, autor_id = ?, genero_id = ? WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAnio());
            ps.setInt(3, libro.getAutorId());
            ps.setInt(4, libro.getGeneroId());
            ps.setInt(5, libro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al actualizar libro con id: " + libro.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar libro con id: " + id, e);
        }
    }

    // Mapeo con JOINs (incluye nombres de autor y género)
    private Libro mapear(ResultSet rs) throws SQLException {
        Libro libro = mapearBasic(rs);
        libro.setAutorNombre(rs.getString("autor_nombre"));
        libro.setGeneroNombre(rs.getString("genero_nombre"));
        return libro;
    }

    // Mapeo básico sin JOINs
    private Libro mapearBasic(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getInt("anio"),
            rs.getInt("autor_id"),
            rs.getInt("genero_id")
        );
    }
}
