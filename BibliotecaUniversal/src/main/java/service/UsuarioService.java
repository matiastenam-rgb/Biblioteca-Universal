package service;

import data.UsuarioDao;
import data.UsuarioDaoJdbc;
import domain.Usuario;
import dto.UsuarioDTO;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private UsuarioDao dao = new UsuarioDaoJdbc();

    public UsuarioService() {}

    public UsuarioService(UsuarioDao dao) {
        this.dao = dao;
    }

    public List<UsuarioDTO> listarTodos() {
        List<Usuario> lista = dao.select();
        List<UsuarioDTO> dtos = new ArrayList<>();
        for (Usuario u : lista) {
            dtos.add(toDTO(u));
        }
        return dtos;
    }

    public UsuarioDTO obtenerPorId(int id) {
        Usuario u = dao.selectById(id);
        return u != null ? toDTO(u) : null;
    }

    public UsuarioDTO obtenerPorEmail(String email) {
        Usuario u = dao.selectByEmail(email);
        return u != null ? toDTO(u) : null;
    }

    public void crear(UsuarioDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (obtenerPorEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        dao.insert(toEntity(dto));
    }

    public void actualizar(UsuarioDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        dao.update(toEntity(dto));
    }

    public void eliminar(int id) {
        dao.delete(id);
    }

    public UsuarioDTO toDTO(Usuario u) {
        if (u == null) return null;
        return new UsuarioDTO(u.getId(), u.getNombre(), u.getEmail());
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        Usuario u = new Usuario();
        u.setId(dto.getId());
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        return u;
    }
}
