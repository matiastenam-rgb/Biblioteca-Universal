package dto;

public class LibroDetalleDTO {
    private int id;
    private String titulo;
    private int anio;
    private String autorNombre;
    private String autorNacionalidad;
    private String generoNombre;

    public LibroDetalleDTO() {}

    public LibroDetalleDTO(int id, String titulo, int anio, String autorNombre, String autorNacionalidad, String generoNombre) {
        this.id = id;
        this.titulo = titulo;
        this.anio = anio;
        this.autorNombre = autorNombre;
        this.autorNacionalidad = autorNacionalidad;
        this.generoNombre = generoNombre;
    }

    public LibroDetalleDTO(int id, String titulo, int anio, String autorNombre, String generoNombre) {
        this.id = id;
        this.titulo = titulo;
        this.anio = anio;
        this.autorNombre = autorNombre;
        this.generoNombre = generoNombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

    public String getAutorNacionalidad() { return autorNacionalidad; }
    public void setAutorNacionalidad(String autorNacionalidad) { this.autorNacionalidad = autorNacionalidad; }

    public String getGeneroNombre() { return generoNombre; }
    public void setGeneroNombre(String generoNombre) { this.generoNombre = generoNombre; }
}
