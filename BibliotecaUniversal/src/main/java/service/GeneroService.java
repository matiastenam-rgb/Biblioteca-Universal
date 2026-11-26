package service;

import data.GeneroDao;
import data.GeneroDaoJdbc;
import domain.Genero;
import dto.GeneroDTO;
import java.util.ArrayList;
import java.util.List;

public class GeneroService {

    private GeneroDao dao = new GeneroDaoJdbc();

    public GeneroService() {}

    public GeneroService(GeneroDao dao) {
        this.dao = dao;
    }

    public List<GeneroDTO> listarTodos() {
        List<Genero> lista = dao.select();
        List<GeneroDTO> dtos = new ArrayList<>();
        for (Genero g : lista) {
            dtos.add(toDTO(g));
        }
        return dtos;
    }

    public GeneroDTO obtenerPorId(int id) {
        Genero g = dao.selectById(id);
        return g != null ? toDTO(g) : null;
    }

    public void crear(GeneroDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        dao.insert(toEntity(dto));
    }

    public void actualizar(GeneroDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        dao.update(toEntity(dto));
    }

    public void eliminar(int id) {
        dao.delete(id);
    }

    public GeneroDTO toDTO(Genero g) {
        if (g == null) return null;
        return new GeneroDTO(g.getId(), g.getNombre(), g.getDescripcion());
    }

    public Genero toEntity(GeneroDTO dto) {
        if (dto == null) return null;
        return new Genero(dto.getId(), dto.getNombre(), dto.getDescripcion());
    }
}
