package data;

import domain.Libro;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibroDaoTest {

    private LibroDao dao;

    @BeforeEach
    void setUp() throws Exception {
        // Crear pool H2 en memoria con MODE=MySQL
        org.apache.commons.dbcp2.BasicDataSource pool =
            new org.apache.commons.dbcp2.BasicDataSource();
        pool.setDriverClassName("org.h2.Driver");
        pool.setUrl("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setInitialSize(1);
        pool.setMaxTotal(5);

        // Inyectar el pool H2 en Connect
        java.lang.reflect.Field field = Connect.class.getDeclaredField("pool");
        field.setAccessible(true);
        field.set(null, pool);

        // Crear tablas y datos de ejemplo
        try (Connection conn = pool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS autores (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100), nacionalidad VARCHAR(50), fecha_nacimiento DATE, fecha_muerte DATE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS generos (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(50), descripcion TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS libros (id INT AUTO_INCREMENT PRIMARY KEY, titulo VARCHAR(150), anio INT, autor_id INT, genero_id INT, nombre_autor VARCHAR(100), nombre_genero VARCHAR(50), FOREIGN KEY (autor_id) REFERENCES autores(id), FOREIGN KEY (genero_id) REFERENCES generos(id))");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM generos");
            stmt.execute("DELETE FROM autores");
            stmt.execute("INSERT INTO autores (id, nombre, nacionalidad) VALUES (1, 'Cervantes', 'Española')");
            stmt.execute("INSERT INTO generos (id, nombre, descripcion) VALUES (1, 'Novela clásica', 'Clásicos')");
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor_id, genero_id, nombre_autor, nombre_genero) VALUES (1, 'Don Quijote', 1605, 1, 1, 'Cervantes', 'Novela clásica')");
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor_id, genero_id, nombre_autor, nombre_genero) VALUES (2, 'Cien años de soledad', 1967, 1, 1, 'Cervantes', 'Novela clásica')");
        }

        dao = new LibroDaoJdbc();
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
    void selectDevuelveListaDeLibros() {
        List<Libro> lista = dao.select();
        assertNotNull(lista);
        assertTrue(lista.size() >= 2, "Debe devolver al menos 2 libros");
    }

    @Test
    void selectByIdDevuelveLibroExistente() {
        Libro libro = dao.selectById(1);
        assertNotNull(libro);
        assertEquals(1, libro.getId());
        assertEquals("Don Quijote", libro.getTitulo());
        assertEquals(1605, libro.getAnio());
    }

    @Test
    void selectByIdDevuelveNullParaIdInexistente() {
        Libro libro = dao.selectById(999);
        assertNull(libro);
    }

    @Test
    void selectByTextoEncuentraLibroPorTitulo() {
        List<Libro> lista = dao.selectByTexto("Quijote");
        assertNotNull(lista);
        assertTrue(lista.size() >= 1);
        assertEquals("Don Quijote", lista.get(0).getTitulo());
    }

    @Test
    void selectByTextoNoEncuentraLibroInexistente() {
        List<Libro> lista = dao.selectByTexto("LibroInexistente");
        assertNotNull(lista);
        assertEquals(0, lista.size());
    }

    @Test
    void insertAadeNuevoLibro() {
        Libro nuevo = new Libro();
        nuevo.setTitulo("Hamlet");
        nuevo.setAnio(1603);
        nuevo.setAutorId(1);
        nuevo.setGeneroId(1);

        dao.insert(nuevo);

        List<Libro> lista = dao.select();
        boolean encontrado = lista.stream()
            .anyMatch(l -> l.getTitulo().equals("Hamlet"));
        assertTrue(encontrado, "Hamlet debe estar en la lista después de insertar");
    }

    @Test
    void updateModificaLibroExistente() {
        // Verificar valor antes
        Libro antes = dao.selectById(1);
        assertEquals(1605, antes.getAnio());

        // Modificar
        Libro libro = new Libro();
        libro.setId(1);
        libro.setTitulo("Don Quijote de la Mancha");
        libro.setAnio(1615);
        libro.setAutorId(1);
        libro.setGeneroId(1);

        dao.update(libro);

        // Verificar valor después
        Libro despues = dao.selectById(1);
        assertEquals(1615, despues.getAnio());
        assertEquals("Don Quijote de la Mancha", despues.getTitulo());
    }

    @Test
    void deleteEliminaLibro() {
        // Insertar un libro para eliminar
        Libro temp = new Libro();
        temp.setTitulo("Temp");
        temp.setAnio(2024);
        temp.setAutorId(1);
        temp.setGeneroId(1);
        dao.insert(temp);

        Libro inserted = dao.selectByTexto("Temp").get(0);
        int idEliminar = inserted.getId();

        // Eliminar
        dao.delete(idEliminar);

        // Verificar que ya no existe
        Libro eliminado = dao.selectById(idEliminar);
        assertNull(eliminado);
    }

    @Test
    void integridadReferencialAutorInexistenteLanzaExcepcion() {
        Libro libroInvalido = new Libro();
        libroInvalido.setTitulo("Libro Sin Autor Real");
        libroInvalido.setAnio(2024);
        libroInvalido.setAutorId(9999); // No existe en tabla autores
        libroInvalido.setGeneroId(1);

        assertThrows(DataAccessException.class, () -> dao.insert(libroInvalido));
    }

    @Test
    void integridadReferencialGeneroInexistenteLanzaExcepcion() {
        Libro libroInvalido = new Libro();
        libroInvalido.setTitulo("Libro Sin Genero Real");
        libroInvalido.setAnio(2024);
        libroInvalido.setAutorId(1);
        libroInvalido.setGeneroId(9999); // No existe en tabla generos

        assertThrows(DataAccessException.class, () -> dao.insert(libroInvalido));
    }
}
