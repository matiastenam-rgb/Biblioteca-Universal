<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Catálogo · Biblioteca Universal</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
<header class="barra">
    <nav class="ancho" aria-label="Navegación principal">
        <a class="marca" href="controlador?accion=listar">Biblioteca Universal</a>
        <a href="controlador?accion=listar" aria-current="page">Catálogo</a>
    </nav>
</header>
<main class="ancho">
    <div class="encabezado">
        <h1>Catálogo de libros</h1>
        <a href="controlador?accion=nuevo" class="btn">+ Nuevo libro</a>
    </div>
    <c:if test="${not empty sessionScope.mensaje}">
        <p class="mensaje" role="status"><c:out value="${sessionScope.mensaje}"/></p>
        <c:remove var="mensaje" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error or not empty requestScope.error}">
        <p class="mensaje error" role="alert"><c:out value="${empty requestScope.error ? sessionScope.error : requestScope.error}"/></p>
        <c:remove var="error" scope="session"/>
    </c:if>
    <dl class="estadisticas">
        <div><dt>Libros</dt><dd>${totalLibros}</dd></div>
        <div><dt>Autores</dt><dd>${autores.size()}</dd></div>
        <div><dt>Géneros</dt><dd>${generos.size()}</dd></div>
    </dl>
    <section class="panel" aria-label="Resultados del catálogo">
        <form action="controlador" method="get" class="filtros">
            <input type="hidden" name="accion" value="buscar">
            <label>Título, autor o #ID
                <input type="search" name="texto" placeholder="Ej. 1984 o #24" value="${fn:escapeXml(texto)}">
            </label>
            <label>Autor
                <select name="autorId">
                    <option value="">Todos</option>
                    <c:forEach var="autor" items="${autores}">
                        <option value="${autor.id}" <c:if test="${autor.id eq filtroAutor}">selected</c:if>><c:out value="${autor.nombre}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Género
                <select name="generoId">
                    <option value="">Todos</option>
                    <c:forEach var="genero" items="${generos}">
                        <option value="${genero.id}" <c:if test="${genero.id eq filtroGenero}">selected</c:if>><c:out value="${genero.nombre}"/></option>
                    </c:forEach>
                </select>
            </label>
            <div class="acciones">
                <button class="btn" type="submit">Buscar</button>
                <a class="btn secundaria" href="controlador?accion=listar">Limpiar</a>
            </div>
        </form>
        <p class="resultados">${totalResultados} ${totalResultados eq 1 ? 'resultado' : 'resultados'}</p>
        <c:choose>
            <c:when test="${empty lista}">
                <p class="vacio">No se encontraron libros. Prueba con otros filtros.</p>
            </c:when>
            <c:otherwise>
                <div class="tabla" tabindex="0" role="region" aria-label="Libros encontrados">
                    <table>
                        <thead><tr><th scope="col">Título</th><th scope="col">ID</th><th scope="col">Autor</th><th scope="col">Género</th><th scope="col">Año</th><th scope="col">Acciones</th></tr></thead>
                        <tbody>
                        <c:forEach var="libro" items="${lista}">
                            <tr>
                                <th scope="row"><c:out value="${libro.titulo}"/></th>
                                <td><span class="referencia">#${libro.id}</span></td>
                                <td><c:out value="${libro.autorNombre}"/></td>
                                <td><span class="etiqueta"><c:out value="${libro.generoNombre}"/></span></td>
                                <td>${libro.anio}</td>
                                <td>
                                    <div class="acciones">
                                        <a class="btn secundaria" href="controlador?accion=editar&amp;id=${libro.id}">Editar</a>
                                        <form action="controlador" method="post" onsubmit="return confirm('¿Eliminar este libro del catálogo?');">
                                            <input type="hidden" name="accion" value="eliminar">
                                            <input type="hidden" name="id" value="${libro.id}">
                                            <button class="btn peligro" type="submit">Eliminar</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <c:if test="${totalPaginas gt 1}">
                    <nav class="paginas" aria-label="Páginas del catálogo">
                        <c:if test="${pagina gt 1}">
                            <c:url var="anterior" value="/controlador">
                                <c:param name="accion" value="buscar"/><c:param name="texto" value="${texto}"/>
                                <c:param name="autorId" value="${filtroAutor}"/><c:param name="generoId" value="${filtroGenero}"/>
                                <c:param name="pagina" value="${pagina - 1}"/>
                            </c:url>
                            <a class="btn secundaria" href="${fn:escapeXml(anterior)}">Anterior</a>
                        </c:if>
                        <span>Página ${pagina} de ${totalPaginas}</span>
                        <c:if test="${pagina lt totalPaginas}">
                            <c:url var="siguiente" value="/controlador">
                                <c:param name="accion" value="buscar"/><c:param name="texto" value="${texto}"/>
                                <c:param name="autorId" value="${filtroAutor}"/><c:param name="generoId" value="${filtroGenero}"/>
                                <c:param name="pagina" value="${pagina + 1}"/>
                            </c:url>
                            <a class="btn secundaria" href="${fn:escapeXml(siguiente)}">Siguiente</a>
                        </c:if>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </section>
</main>
<footer class="ancho">Biblioteca Universal · Acceso a Datos</footer>
</body>
</html>
