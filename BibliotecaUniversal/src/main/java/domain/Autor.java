package domain;

import java.util.Date;

public class Autor {
    private int id;
    private String nombre;
    private String nacionalidad;
    private Date fechaNacimiento;
    private Date fechaMuerte;

    public Autor() {}

    public Autor(int id, String nombre, String nacionalidad, Date fechaNacimiento, Date fechaMuerte) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaMuerte = fechaMuerte;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Date getFechaMuerte() { return fechaMuerte; }
    public void setFechaMuerte(Date fechaMuerte) { this.fechaMuerte = fechaMuerte; }

    @Override
    public String toString() {
        return "Autor{" + "id=" + id + ", nombre=" + nombre + ", nacionalidad=" + nacionalidad + '}';
    }
}
