package service;

import data.LibroDao;
import data.LibroDaoJdbc;
import domain.Libro;
import dto.LibroDTO;
import dto.LibroDetalleDTO;
import java.util.ArrayList;
import java.util.List;

public class LibroService {

    private LibroDao dao = new LibroDaoJdbc();

    public LibroService() {}

    public LibroService(LibroDao dao) {
        this.dao = dao;
    }

    public List<LibroDetalleDTO> listarTodos() {
        List<Libro> libros = dao.selectWithDetails();
        List<LibroDetalleDTO> dtos = new ArrayList<>();
        for (Libro l : libros) {
            dtos.add(toDetalleDTO(l));
        }
        return dtos;
    }

    public List<LibroDetalleDTO> filtrar(List<LibroDetalleDTO> candidatos, int autorId, int generoId) {
        return candidatos.stream()
                .filter(libro -> autorId == 0 || libro.getAutorId() == autorId)
                .filter(libro -> generoId == 0 || libro.getGeneroId() == generoId)
                .toList();
    }

    public LibroDTO obtenerPorId(int id) {
        Libro libro = dao.selectById(id);
        return libro != null ? toDTO(libro) : null;
    }

    public LibroDetalleDTO obtenerDetallePorId(int id) {
        Libro libro = dao.selectWithDetailsById(id);
        return libro != null ? toDetalleDTO(libro) : null;
    }

    public List<LibroDetalleDTO> buscarPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        String busqueda = texto.trim();
        if (busqueda.matches("#[0-9]+")) {
            int id;
            try {
                id = Integer.parseInt(busqueda.substring(1));
            } catch (NumberFormatException e) {
                return List.of();
            }
            LibroDetalleDTO libro = obtenerDetallePorId(id);
            return libro == null ? List.of() : List.of(libro);
        }
        List<Libro> libros = dao.selectByTexto(busqueda);
        List<LibroDetalleDTO> dtos = new ArrayList<>();
        for (Libro l : libros) {
            dtos.add(toDetalleDTO(l));
        }
        return dtos;
    }

    public void crear(LibroDTO dto) {
        validarLibroDTO(dto);
        Libro libro = toEntity(dto);
        dao.insert(libro);
    }

    public void actualizar(LibroDTO dto) {
        if (dto == null || dto.getId() <= 0) {
            throw new IllegalArgumentException("Identificador de libro inválido");
        }
        if (dao.selectById(dto.getId()) == null) {
            throw new IllegalArgumentException("El libro que intenta modificar no existe");
        }
        validarLibroDTO(dto);
        Libro libro = toEntity(dto);
        dao.update(libro);
    }

    public void eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador de libro inválido");
        }
        if (dao.selectById(id) == null) {
            throw new IllegalArgumentException("El libro que intenta eliminar no existe");
        }
        dao.delete(id);
    }

    // Métodos de compatibilidad con domain.Libro (para tests existentes)
    public void crear(Libro libro) {
        validarLibro(libro);
        dao.insert(libro);
    }

    public void actualizar(Libro libro) {
        if (libro == null || libro.getId() <= 0) {
            throw new IllegalArgumentException("Identificador de libro inválido");
        }
        validarLibro(libro);
        dao.update(libro);
    }

    // Mapeos Entidad <-> DTO
    public LibroDTO toDTO(Libro libro) {
        if (libro == null) return null;
        return new LibroDTO(
            libro.getId(),
            libro.getTitulo(),
            libro.getAnio(),
            libro.getAutorId(),
            libro.getGeneroId()
        );
    }

    public LibroDetalleDTO toDetalleDTO(Libro libro) {
        if (libro == null) return null;
        LibroDetalleDTO detalle = new LibroDetalleDTO(
            libro.getId(),
            libro.getTitulo(),
            libro.getAnio(),
            libro.getAutorNombre(),
            libro.getGeneroNombre()
        );
        detalle.setAutorId(libro.getAutorId());
        detalle.setGeneroId(libro.getGeneroId());
        return detalle;
    }

    public Libro toEntity(LibroDTO dto) {
        if (dto == null) return null;
        Libro libro = new Libro(
            dto.getId(),
            dto.getTitulo(),
            dto.getAnio(),
            dto.getAutorId(),
            dto.getGeneroId()
        );
        return libro;
    }

    private void validarLibroDTO(LibroDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (dto.getTitulo().codePointCount(0, dto.getTitulo().length()) > 150) {
            throw new IllegalArgumentException("El título admite hasta 150 caracteres");
        }
        if (dto.getAnio() < -1000 || dto.getAnio() > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 1) {
            throw new IllegalArgumentException("El año no es válido");
        }
        if (dto.getAutorId() <= 0) {
            throw new IllegalArgumentException("Se debe seleccionar un autor");
        }
        if (dto.getGeneroId() <= 0) {
            throw new IllegalArgumentException("Se debe seleccionar un género");
        }
    }

    private void validarLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (libro.getTitulo().codePointCount(0, libro.getTitulo().length()) > 150) {
            throw new IllegalArgumentException("El título admite hasta 150 caracteres");
        }
        if (libro.getAnio() < -1000 || libro.getAnio() > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + 1) {
            throw new IllegalArgumentException("El año no es válido");
        }
        if (libro.getAutorId() <= 0) {
            throw new IllegalArgumentException("Se debe seleccionar un autor");
        }
        if (libro.getGeneroId() <= 0) {
            throw new IllegalArgumentException("Se debe seleccionar un género");
        }
    }
}
