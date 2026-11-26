package service;

import data.Connect;
import domain.Libro;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibroServiceTest {

    private LibroService servicio;

    @BeforeEach
    void setUp() throws Exception {
        // Configurar H2 en memoria con MODE=MySQL
        System.setProperty("DB_HOST", "localhost");
        System.setProperty("DB_PORT", "0"); // no se usa, se sobrescribe la URL
        System.setProperty("DB_NAME", "testdb");
        System.setProperty("DB_USER", "sa");
        System.setProperty("DB_PASS", "");

        // Crear pool H2 en memoria
        org.apache.commons.dbcp2.BasicDataSource pool =
            new org.apache.commons.dbcp2.BasicDataSource();
        pool.setDriverClassName("org.h2.Driver");
        pool.setUrl("jdbc:h2:mem:libro_service_" + java.util.UUID.randomUUID() + ";MODE=MySQL");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setInitialSize(1);
        pool.setMaxTotal(5);

        // Inyectar el pool H2 en Connect (reflexión para test)
        java.lang.reflect.Field field = Connect.class.getDeclaredField("pool");
        field.setAccessible(true);
        field.set(null, pool);

        // Crear tablas en H2
        try (Connection conn = pool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS autores (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100), nacionalidad VARCHAR(50), fecha_nacimiento DATE, fecha_muerte DATE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS generos (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(50), descripcion TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS libros (id INT AUTO_INCREMENT PRIMARY KEY, titulo VARCHAR(150), anio INT, autor_id INT, genero_id INT, nombre_autor VARCHAR(100), nombre_genero VARCHAR(50), FOREIGN KEY (autor_id) REFERENCES autores(id), FOREIGN KEY (genero_id) REFERENCES generos(id))");
            stmt.execute("INSERT INTO autores (nombre, nacionalidad) VALUES ('Cervantes', 'Española')");
            stmt.execute("INSERT INTO generos (nombre, descripcion) VALUES ('Novela clásica', 'Clásicos')");
            stmt.execute("INSERT INTO libros (titulo, anio, autor_id, genero_id, nombre_autor, nombre_genero) VALUES ('Don Quijote', 1605, 1, 1, 'Cervantes', 'Novela clásica')");
        }

        servicio = new LibroService();
    }

    @AfterEach
    void tearDown() throws Exception {
        java.lang.reflect.Field field = Connect.class.getDeclaredField("pool");
        field.setAccessible(true);
        Object pool = field.get(null);
        if (pool != null) {
            ((org.apache.commons.dbcp2.BasicDataSource) pool).close();
            field.set(null, null);
        }
    }

    @Test
    void validarLibroSinTituloLanzaExcepcion() {
        Libro libro = new Libro();
        libro.setTitulo("");
        libro.setAnio(2024);
        libro.setAutorId(1);
        libro.setGeneroId(1);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(libro));
    }

    @Test
    void validarLibroSinAutorLanzaExcepcion() {
        Libro libro = new Libro();
        libro.setTitulo("Test");
        libro.setAnio(2024);
        libro.setAutorId(0);
        libro.setGeneroId(1);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(libro));
    }

    @Test
    void validarLibroSinGeneroLanzaExcepcion() {
        Libro libro = new Libro();
        libro.setTitulo("Test");
        libro.setAnio(2024);
        libro.setAutorId(1);
        libro.setGeneroId(0);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(libro));
    }

    @Test
    void validarLibroAnioInvalidoLanzaExcepcion() {
        Libro libro = new Libro();
        libro.setTitulo("Test");
        libro.setAnio(-2000);
        libro.setAutorId(1);
        libro.setGeneroId(1);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(libro));
    }

    @Test
    void buscarPorTextoVacioDevuelveTodos() {
        List<dto.LibroDetalleDTO> resultado = servicio.buscarPorTexto("");
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1, "Debe devolver al menos 1 libro");
        // Verificar que Don Quijote está en los resultados
        boolean encontrado = resultado.stream()
            .anyMatch(l -> l.getTitulo().equals("Don Quijote"));
        assertTrue(encontrado, "Don Quijote debe estar en los resultados");
    }

    @Test
    void crearLibroConDTOCorrecto() {
        dto.LibroDTO dto = new dto.LibroDTO();
        dto.setTitulo("Novela Ejemplar");
        dto.setAnio(1613);
        dto.setAutorId(1);
        dto.setGeneroId(1);

        assertDoesNotThrow(() -> servicio.crear(dto));
        List<dto.LibroDetalleDTO> resultado = servicio.buscarPorTexto("Novela Ejemplar");
        assertFalse(resultado.isEmpty());
    }

    @Test
    void modificarLibroInexistenteLanzaExcepcion() {
        dto.LibroDTO dto = new dto.LibroDTO();
        dto.setId(9999);
        dto.setTitulo("Inexistente");
        dto.setAnio(2020);
        dto.setAutorId(1);
        dto.setGeneroId(1);

        assertThrows(IllegalArgumentException.class, () -> servicio.actualizar(dto));
    }

    @Test
    void eliminarLibroInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminar(9999));
    }

    @Test
    void eliminarLibroIdInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminar(0));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminar(-5));
    }

    @Test
    void combinaBusquedaConAutorYGenero() {
        var resultados = servicio.buscarPorTexto("Quijote");
        assertEquals(1, servicio.filtrar(resultados, 1, 1).size());
        assertEquals(1, servicio.filtrar(resultados, 0, 1).size());
        assertTrue(servicio.filtrar(resultados, 2, 1).isEmpty());
        assertTrue(servicio.filtrar(resultados, 1, 2).isEmpty());
    }

    @Test
    void distingueUnaReferenciaExactaDeUnTituloNumerico() {
        servicio.crear(new dto.LibroDTO(0, "1984", 1949, 1, 1));
        var exactos = servicio.buscarPorTexto("#1");

        assertEquals(1, exactos.size());
        assertEquals("Don Quijote", exactos.get(0).getTitulo());
        assertEquals("1984", servicio.buscarPorTexto("1984").get(0).getTitulo());
        assertTrue(servicio.buscarPorTexto("#999999999999").isEmpty());
        assertTrue(servicio.buscarPorTexto("#9999").isEmpty());
    }

    @Test
    void rechazaTitulosMasLargosQueLaColumna() {
        var libro = new dto.LibroDTO(0, "á".repeat(151), 2020, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> servicio.crear(libro));
    }

}
