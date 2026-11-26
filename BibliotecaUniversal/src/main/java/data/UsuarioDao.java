package data;

import domain.Usuario;
import java.util.List;

public interface UsuarioDao extends BaseDao<Usuario> {
    Usuario selectByEmail(String email);
}
