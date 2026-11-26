package service;

import data.PrestamoDao;
import data.PrestamoDaoJdbc;
import domain.Prestamo;
import dto.PrestamoDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrestamoService {

    private PrestamoDao dao = new PrestamoDaoJdbc();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public PrestamoService() {}

    public PrestamoService(PrestamoDao dao) {
        this.dao = dao;
    }

    public List<PrestamoDTO> listarTodos() {
        List<Prestamo> lista = dao.select();
        List<PrestamoDTO> dtos = new ArrayList<>();
        for (Prestamo p : lista) {
            dtos.add(toDTO(p));
        }
        return dtos;
    }

    public PrestamoDTO obtenerPorId(int id) {
        Prestamo p = dao.selectById(id);
        return p != null ? toDTO(p) : null;
    }

    public List<PrestamoDTO> obtenerPorUsuario(int usuarioId) {
        List<Prestamo> lista = dao.selectByUsuario(usuarioId);
        List<PrestamoDTO> dtos = new ArrayList<>();
        for (Prestamo p : lista) {
            dtos.add(toDTO(p));
        }
        return dtos;
    }

    public List<PrestamoDTO> obtenerPorLibro(int libroId) {
        List<Prestamo> lista = dao.selectByLibro(libroId);
        List<PrestamoDTO> dtos = new ArrayList<>();
        for (Prestamo p : lista) {
            dtos.add(toDTO(p));
        }
        return dtos;
    }

    public List<PrestamoDTO> obtenerPorEstado(String estado) {
        List<Prestamo> lista = dao.selectByEstado(estado);
        List<PrestamoDTO> dtos = new ArrayList<>();
        for (Prestamo p : lista) {
            dtos.add(toDTO(p));
        }
        return dtos;
    }

    public List<PrestamoDTO> obtenerPrestados() {
        return obtenerPorEstado("Prestado");
    }

    public void crear(PrestamoDTO dto) {
        if (dto.getLibroId() <= 0) {
            throw new IllegalArgumentException("Se debe seleccionar un libro");
        }
        if (dto.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("Se debe seleccionar un usuario");
        }
        if (dto.getEstado() == null || dto.getEstado().trim().isEmpty()) {
            dto.setEstado("Prestado");
        }
        Prestamo p = toEntity(dto);
        if (p.getFechaPrestamo() == null) {
            p.setFechaPrestamo(new Date());
        }
        dao.insert(p);
    }

    public void devolver(int prestamoId) {
        Prestamo prestamo = dao.selectById(prestamoId);
        if (prestamo != null) {
            prestamo.setEstado("Devuelto");
            prestamo.setFechaDevolucion(new Date());
            dao.update(prestamo);
        }
    }

    public void actualizar(PrestamoDTO dto) {
        dao.update(toEntity(dto));
    }

    public void eliminar(int id) {
        dao.delete(id);
    }

    public PrestamoDTO toDTO(Prestamo p) {
        if (p == null) return null;
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(p.getId());
        dto.setLibroId(p.getLibroId());
        dto.setUsuarioId(p.getUsuarioId());
        dto.setFechaPrestamo(p.getFechaPrestamo() != null ? DATE_FORMAT.format(p.getFechaPrestamo()) : null);
        dto.setFechaDevolucion(p.getFechaDevolucion() != null ? DATE_FORMAT.format(p.getFechaDevolucion()) : null);
        dto.setEstado(p.getEstado());
        dto.setLibroTitulo(p.getLibroTitulo());
        dto.setUsuarioNombre(p.getUsuarioNombre());
        return dto;
    }

    public Prestamo toEntity(PrestamoDTO dto) {
        if (dto == null) return null;
        Prestamo p = new Prestamo();
        p.setId(dto.getId());
        p.setLibroId(dto.getLibroId());
        p.setUsuarioId(dto.getUsuarioId());
        p.setEstado(dto.getEstado());
        p.setLibroTitulo(dto.getLibroTitulo());
        p.setUsuarioNombre(dto.getUsuarioNombre());
        try {
            if (dto.getFechaPrestamo() != null && !dto.getFechaPrestamo().trim().isEmpty()) {
                p.setFechaPrestamo(DATE_FORMAT.parse(dto.getFechaPrestamo()));
            }
            if (dto.getFechaDevolucion() != null && !dto.getFechaDevolucion().trim().isEmpty()) {
                p.setFechaDevolucion(DATE_FORMAT.parse(dto.getFechaDevolucion()));
            }
        } catch (Exception e) {
            // fallback
        }
        return p;
    }
}
