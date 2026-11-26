package domain;

import java.util.Date;

public class Prestamo {
    private int id;
    private int libroId;
    private int usuarioId;
    private Date fechaPrestamo;
    private Date fechaDevolucion;
    private String estado;

    // Campos denormalizados para vistas
    private String libroTitulo;
    private String usuarioNombre;

    public Prestamo() {}

    public Prestamo(int id, int libroId, int usuarioId, Date fechaPrestamo, Date fechaDevolucion, String estado) {
        this.id = id;
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Date getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(Date fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public Date getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(Date fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getLibroTitulo() { return libroTitulo; }
    public void setLibroTitulo(String libroTitulo) { this.libroTitulo = libroTitulo; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    @Override
    public String toString() {
        return "Prestamo{" + "id=" + id + ", estado=" + estado + '}';
    }
}
