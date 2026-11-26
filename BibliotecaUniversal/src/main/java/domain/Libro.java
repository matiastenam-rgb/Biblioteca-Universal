package domain;

public class Libro {
    private int id;
    private String titulo;
    private int anio;
    private int autorId;
    private int generoId;

    // Campos denormalizados para vistas rápidas
    private String autorNombre;
    private String generoNombre;

    public Libro() {}

    public Libro(int id, String titulo, int anio, int autorId, int generoId) {
        this.id = id;
        this.titulo = titulo;
        this.anio = anio;
        this.autorId = autorId;
        this.generoId = generoId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public int getGeneroId() { return generoId; }
    public void setGeneroId(int generoId) { this.generoId = generoId; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

    public String getGeneroNombre() { return generoNombre; }
    public void setGeneroNombre(String generoNombre) { this.generoNombre = generoNombre; }

    @Override
    public String toString() {
        return "Libro{" + "id=" + id + ", titulo=" + titulo + ", anio=" + anio + '}';
    }
}
