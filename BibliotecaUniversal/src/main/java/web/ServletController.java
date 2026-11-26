package web;

import data.DataAccessException;
import dto.LibroDTO;
import dto.LibroDetalleDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AutorService;
import service.GeneroService;
import service.LibroService;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Year;
import java.util.List;

@WebServlet("/controlador")
public class ServletController extends HttpServlet {
    private static final int LIBROS_POR_PAGINA = 20;
    private final LibroService libros = new LibroService();
    private final AutorService autores = new AutorService();
    private final GeneroService generos = new GeneroService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        configurarCodificacion(req, resp);
        String accion = req.getParameter("accion");
        try {
            switch (accion == null ? "listar" : accion) {
                case "nuevo" -> mostrarFormulario(req, resp, null);
                case "editar" -> {
                    int id = entero(req.getParameter("id"), "Identificador de libro inválido.");
                    if (id <= 0) {
                        throw new IllegalArgumentException("Identificador de libro inválido.");
                    }
                    LibroDTO libro = libros.obtenerPorId(id);
                    if (libro == null) {
                        resp.sendError(404, "Libro no encontrado.");
                        return;
                    }
                    mostrarFormulario(req, resp, libro);
                }
                default -> listar(req, resp);
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(400, e.getMessage());
        } catch (DataAccessException e) {
            registrarError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        configurarCodificacion(req, resp);
        String accion = req.getParameter("accion");
        if (accion == null) {
            resp.sendError(400, "Falta la operación solicitada.");
            return;
        }
        try {
            switch (accion) {
                case "insertar", "modificar" -> guardar(req, resp, "modificar".equals(accion));
                case "eliminar" -> eliminar(req, resp);
                default -> resp.sendError(400, "Operación desconocida.");
            }
        } catch (DataAccessException e) {
            registrarError(resp, e);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int autor = filtro(req, "autorId", "Filtro de autor inválido.");
        int genero = filtro(req, "generoId", "Filtro de género inválido.");
        int solicitada = filtro(req, "pagina", "Número de página inválido.");
        String texto = req.getParameter("texto");
        List<LibroDetalleDTO> catalogo = libros.listarTodos();
        List<LibroDetalleDTO> candidatos = texto == null || texto.isBlank()
                ? catalogo : libros.buscarPorTexto(texto);
        List<LibroDetalleDTO> resultados = libros.filtrar(candidatos, autor, genero);
        int paginas = Math.max(1, (resultados.size() + LIBROS_POR_PAGINA - 1) / LIBROS_POR_PAGINA);
        int pagina = Math.max(1, Math.min(solicitada, paginas));
        int inicio = (pagina - 1) * LIBROS_POR_PAGINA;

        cargarSelectores(req);
        req.setAttribute("totalLibros", catalogo.size());
        req.setAttribute("totalResultados", resultados.size());
        req.setAttribute("totalPaginas", paginas);
        req.setAttribute("pagina", pagina);
        req.setAttribute("texto", texto);
        req.setAttribute("filtroAutor", autor);
        req.setAttribute("filtroGenero", genero);
        req.setAttribute("lista", resultados.subList(inicio, Math.min(inicio + LIBROS_POR_PAGINA, resultados.size())));
        req.getRequestDispatcher("/WEB-INF/libros.jsp").forward(req, resp);
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp, boolean edicion)
            throws ServletException, IOException {
        int id = 0;
        if (edicion) {
            try {
                id = entero(req.getParameter("id"), "Identificador de libro inválido.");
                if (id <= 0) {
                    throw new IllegalArgumentException("Identificador de libro inválido.");
                }
            } catch (IllegalArgumentException e) {
                resp.sendError(400, e.getMessage());
                return;
            }
            if (libros.obtenerPorId(id) == null) {
                resp.sendError(404, "Libro no encontrado.");
                return;
            }
        }

        String titulo = req.getParameter("titulo");
        try {
            int anio = entero(req.getParameter("anio"), "Introduce un año entero válido.");
            int autor = entero(req.getParameter("autorId"), "Selecciona un autor válido.");
            int genero = entero(req.getParameter("generoId"), "Selecciona un género válido.");
            LibroDTO libro = new LibroDTO(id, titulo == null ? "" : titulo.strip(), anio, autor, genero);
            if (edicion) {
                libros.actualizar(libro);
            } else {
                libros.crear(libro);
            }
            redirigirAlCatalogo(req, resp, edicion ? "Libro modificado correctamente." : "Libro añadido correctamente.");
        } catch (IllegalArgumentException e) {
            mostrarErrorFormulario(req, resp, id, e.getMessage());
        } catch (DataAccessException e) {
            if (!(e.getCause() instanceof SQLIntegrityConstraintViolationException)) {
                throw e;
            }
            mostrarErrorFormulario(req, resp, id, "Selecciona un autor y un género que existan en el catálogo.");
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = entero(req.getParameter("id"), "Identificador de libro inválido.");
            libros.eliminar(id);
            redirigirAlCatalogo(req, resp, "Libro eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        } catch (DataAccessException e) {
            if (!(e.getCause() instanceof SQLIntegrityConstraintViolationException)) {
                throw e;
            }
            resp.setStatus(409);
            req.setAttribute("error", "El libro tiene préstamos asociados y no se puede eliminar.");
            listar(req, resp);
        }
    }

    private void mostrarErrorFormulario(HttpServletRequest req, HttpServletResponse resp, int id, String error)
            throws ServletException, IOException {
        resp.setStatus(400);
        req.setAttribute("error", error);
        req.setAttribute("edicion", id > 0);
        req.setAttribute("libroId", id);
        req.setAttribute("titulo", req.getParameter("titulo"));
        req.setAttribute("anio", req.getParameter("anio"));
        req.setAttribute("autorSeleccionado", seleccion(req.getParameter("autorId")));
        req.setAttribute("generoSeleccionado", seleccion(req.getParameter("generoId")));
        abrirFormulario(req, resp);
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, LibroDTO libro)
            throws ServletException, IOException {
        if (libro != null) {
            req.setAttribute("edicion", true);
            req.setAttribute("libroId", libro.getId());
            req.setAttribute("titulo", libro.getTitulo());
            req.setAttribute("anio", libro.getAnio());
            req.setAttribute("autorSeleccionado", libro.getAutorId());
            req.setAttribute("generoSeleccionado", libro.getGeneroId());
        }
        abrirFormulario(req, resp);
    }

    private void abrirFormulario(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        cargarSelectores(req);
        req.setAttribute("maxAnio", Year.now().getValue() + 1);
        req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
    }

    private void cargarSelectores(HttpServletRequest req) {
        req.setAttribute("autores", autores.listarTodos());
        req.setAttribute("generos", generos.listarTodos());
    }

    private static int seleccion(String valor) {
        try {
            return Integer.parseInt(valor == null ? "" : valor.strip());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int filtro(HttpServletRequest req, String nombre, String error) {
        String valor = req.getParameter(nombre);
        if (valor == null || valor.isBlank()) {
            return 0;
        }
        int numero = entero(valor, error);
        if (numero < 0) {
            throw new IllegalArgumentException(error);
        }
        return numero;
    }

    private static int entero(String valor, String error) {
        try {
            return Integer.parseInt(valor == null ? "" : valor.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(error);
        }
    }

    private static void configurarCodificacion(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
    }

    private static void redirigirAlCatalogo(HttpServletRequest req, HttpServletResponse resp, String mensaje) {
        req.getSession().setAttribute("mensaje", mensaje);
        resp.setStatus(303);
        resp.setHeader("Location", resp.encodeRedirectURL(req.getContextPath() + "/controlador?accion=listar"));
    }

    private void registrarError(HttpServletResponse resp, DataAccessException e) throws IOException {
        log("Error de acceso a datos del catálogo", e);
        resp.sendError(500, "No se pudo acceder al catálogo. Inténtalo de nuevo en unos minutos.");
    }
}
