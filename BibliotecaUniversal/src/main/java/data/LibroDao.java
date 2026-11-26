package data;

import domain.Libro;
import java.util.List;

public interface LibroDao extends BaseDao<Libro> {
    List<Libro> selectByTexto(String texto);
    List<Libro> selectWithDetails();
    Libro selectWithDetailsById(int id);
}
