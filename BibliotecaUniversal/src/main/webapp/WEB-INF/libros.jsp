<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biblioteca Universal</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>📚 Biblioteca Universal</h1>

        <c:if test="${not empty mensaje}">
            <div class="mensaje">${mensaje}</div>
            <c:remove var="mensaje" scope="session"/>
        </c:if>

        <c:if test="${not empty error}">
            <div class="mensaje error">${error}</div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <div class="acciones">
            <form action="controlador" method="get" class="buscador">
                <input type="hidden" name="accion" value="buscar">
                <input type="text" name="texto" placeholder="Buscar por título o autor..."
                       value="${not empty texto ? texto : ''}">
                <button type="submit">Buscar</button>
            </form>
            <a href="controlador?accion=nuevo" class="btn">+ Nuevo libro</a>
            <c:if test="${not empty texto}">
                <a href="controlador?accion=listar" class="btn">Ver todos</a>
            </c:if>
        </div>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Título</th>
                    <th>Autor</th>
                    <th>Género</th>
                    <th>Año</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty lista}">
                        <c:forEach var="libro" items="${lista}">
                            <tr>
                                <td>${libro.id}</td>
                                <td>${libro.titulo}</td>
                                <td>${libro.autorNombre}</td>
                                <td>${libro.generoNombre}</td>
                                <td>${libro.anio}</td>
                                <td class="btn-celda">
                                    <a href="controlador?accion=editar&id=${libro.id}"
                                       class="btn-editar" title="Editar">✏️</a>
                                    <form action="controlador" method="post" style="display:inline;"
                                          onsubmit="return confirm('¿Eliminar este libro?');">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="id" value="${libro.id}">
                                        <button type="submit" class="btn-eliminar" title="Eliminar">🗑️</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="sin-resultados">No se encontraron libros.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</body>
</html>
