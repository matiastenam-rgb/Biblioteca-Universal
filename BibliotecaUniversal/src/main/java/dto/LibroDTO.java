package dto;

public class LibroDTO {
    private int id;
    private String titulo;
    private int anio;
    private int autorId;
    private int generoId;

    public LibroDTO() {}

    public LibroDTO(int id, String titulo, int anio, int autorId, int generoId) {
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
}
