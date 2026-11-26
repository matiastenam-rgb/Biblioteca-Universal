package data;

import domain.Prestamo;
import java.util.List;

public interface PrestamoDao extends BaseDao<Prestamo> {
    List<Prestamo> selectByUsuario(int usuarioId);
    List<Prestamo> selectByLibro(int libroId);
    List<Prestamo> selectByEstado(String estado);
}
