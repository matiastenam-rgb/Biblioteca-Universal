package service;

import data.AutorDao;
import data.AutorDaoJdbc;
import domain.Autor;
import dto.AutorDTO;
import java.util.ArrayList;
import java.util.List;

public class AutorService {

    private AutorDao dao = new AutorDaoJdbc();

    public AutorService() {}

    public AutorService(AutorDao dao) {
        this.dao = dao;
    }

    public List<AutorDTO> listarTodos() {
        List<Autor> lista = dao.select();
        List<AutorDTO> dtos = new ArrayList<>();
        for (Autor a : lista) {
            dtos.add(toDTO(a));
        }
        return dtos;
    }

    public AutorDTO obtenerPorId(int id) {
        Autor autor = dao.selectById(id);
        return autor != null ? toDTO(autor) : null;
    }

    public List<AutorDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        List<Autor> lista = dao.selectByNombre(nombre.trim());
        List<AutorDTO> dtos = new ArrayList<>();
        for (Autor a : lista) {
            dtos.add(toDTO(a));
        }
        return dtos;
    }

    public void crear(AutorDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        dao.insert(toEntity(dto));
    }

    public void actualizar(AutorDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        dao.update(toEntity(dto));
    }

    public void eliminar(int id) {
        dao.delete(id);
    }

    public AutorDTO toDTO(Autor autor) {
        if (autor == null) return null;
        return new AutorDTO(autor.getId(), autor.getNombre(), autor.getNacionalidad());
    }

    public Autor toEntity(AutorDTO dto) {
        if (dto == null) return null;
        Autor a = new Autor();
        a.setId(dto.getId());
        a.setNombre(dto.getNombre());
        a.setNacionalidad(dto.getNacionalidad());
        return a;
    }
}
