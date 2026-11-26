package web;

import dto.AutorDTO;
import dto.GeneroDTO;
import dto.LibroDTO;
import dto.LibroDetalleDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AutorService;
import service.GeneroService;
import service.LibroService;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/controlador"})
public class ServletController extends HttpServlet {

    private LibroService libroService = new LibroService();
    private AutorService autorService = new AutorService();
    private GeneroService generoService = new GeneroService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar" -> listarLibros(req, resp, null);
            case "buscar" -> listarLibros(req, resp, req.getParameter("texto"));
            case "nuevo" -> {
                cargarSelects(req);
                req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
            }
            case "editar" -> {
                String idParam = req.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador no proporcionado");
                    return;
                }
                try {
                    int id = Integer.parseInt(idParam);
                    LibroDTO libro = libroService.obtenerPorId(id);
                    if (libro == null) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro no encontrado");
                        return;
                    }
                    req.setAttribute("libro", libro);
                    cargarSelects(req);
                    req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador inválido");
                }
            }
            default -> listarLibros(req, resp, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String accion = req.getParameter("accion");
        HttpSession session = req.getSession();

        switch (accion) {
            case "insertar" -> {
                String titulo = req.getParameter("titulo");
                String anioStr = req.getParameter("anio");
                String autorIdStr = req.getParameter("autorId");
                String generoIdStr = req.getParameter("generoId");

                try {
                    int anio = parsearEntero(anioStr, "El año debe ser un número entero válido");
                    int autorId = parsearEntero(autorIdStr, "Debe seleccionar un autor válido");
                    int generoId = parsearEntero(generoIdStr, "Debe seleccionar un género válido");

                    LibroDTO libro = new LibroDTO();
                    libro.setTitulo(titulo != null ? titulo.trim() : "");
                    libro.setAnio(anio);
                    libro.setAutorId(autorId);
                    libro.setGeneroId(generoId);

                    libroService.crear(libro);
                    session.setAttribute("mensaje", "Libro añadido correctamente.");
                    resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    resp.sendRedirect("controlador?accion=listar");
                } catch (IllegalArgumentException e) {
                    session.setAttribute("error", e.getMessage());
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    cargarSelects(req);
                    req.setAttribute("titulo", titulo);
                    req.setAttribute("anio", anioStr);
                    req.setAttribute("autorId", autorIdStr);
                    req.setAttribute("generoId", generoIdStr);
                    req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
                } catch (Exception e) {
                    session.setAttribute("error", "Error interno al procesar el libro");
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    cargarSelects(req);
                    req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
                }
            }
            case "modificar" -> {
                String idStr = req.getParameter("id");
                String titulo = req.getParameter("titulo");
                String anioStr = req.getParameter("anio");
                String autorIdStr = req.getParameter("autorId");
                String generoIdStr = req.getParameter("generoId");

                try {
                    int id = parsearEntero(idStr, "Identificador de libro inválido");
                    int anio = parsearEntero(anioStr, "El año debe ser un número entero válido");
                    int autorId = parsearEntero(autorIdStr, "Debe seleccionar un autor válido");
                    int generoId = parsearEntero(generoIdStr, "Debe seleccionar un género válido");

                    LibroDTO libro = new LibroDTO();
                    libro.setId(id);
                    libro.setTitulo(titulo != null ? titulo.trim() : "");
                    libro.setAnio(anio);
                    libro.setAutorId(autorId);
                    libro.setGeneroId(generoId);

                    libroService.actualizar(libro);
                    session.setAttribute("mensaje", "Libro modificado correctamente.");
                    resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    resp.sendRedirect("controlador?accion=listar");
                } catch (IllegalArgumentException e) {
                    session.setAttribute("error", e.getMessage());
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    cargarSelects(req);
                    LibroDTO libroErr = new LibroDTO();
                    try {
                        libroErr.setId(Integer.parseInt(idStr));
                        libroErr.setAnio(Integer.parseInt(anioStr));
                        libroErr.setAutorId(Integer.parseInt(autorIdStr));
                        libroErr.setGeneroId(Integer.parseInt(generoIdStr));
                    } catch (Exception ignored) {}
                    libroErr.setTitulo(titulo);
                    req.setAttribute("libro", libroErr);
                    req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
                } catch (Exception e) {
                    session.setAttribute("error", "Error interno al modificar el libro");
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    cargarSelects(req);
                    req.getRequestDispatcher("/WEB-INF/formularioLibro.jsp").forward(req, resp);
                }
            }
            case "eliminar" -> {
                String idStr = req.getParameter("id");
                try {
                    int id = parsearEntero(idStr, "Identificador de libro inválido");
                    libroService.eliminar(id);
                    session.setAttribute("mensaje", "Libro eliminado correctamente.");
                } catch (IllegalArgumentException e) {
                    session.setAttribute("error", e.getMessage());
                } catch (data.DataAccessException e) {
                    session.setAttribute("error", "No se puede eliminar el libro porque está asociado a otros registros (ej. préstamos).");
                } catch (Exception e) {
                    session.setAttribute("error", "Error interno al eliminar el libro.");
                }
                resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
                resp.sendRedirect("controlador?accion=listar");
            }
            default -> resp.sendRedirect("controlador?accion=listar");
        }
    }

    private void listarLibros(HttpServletRequest req, HttpServletResponse resp, String texto)
            throws ServletException, IOException {

        List<LibroDetalleDTO> lista;
        if (texto != null && !texto.trim().isEmpty()) {
            lista = libroService.buscarPorTexto(texto);
            req.setAttribute("texto", texto);
        } else {
            lista = libroService.listarTodos();
        }
        req.setAttribute("lista", lista);
        req.getRequestDispatcher("/WEB-INF/libros.jsp").forward(req, resp);
    }

    private void cargarSelects(HttpServletRequest req) throws ServletException, IOException {
        req.setAttribute("autores", autorService.listarTodos());
        req.setAttribute("generos", generoService.listarTodos());
    }

    private int parsearEntero(String valor, String mensajeError) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }
}
