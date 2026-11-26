package data;

import domain.Autor;
import java.util.List;

public interface AutorDao extends BaseDao<Autor> {
    List<Autor> selectByNombre(String nombre);
}
