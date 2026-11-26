<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${edicion ? 'Editar libro' : 'Nuevo libro'} · Biblioteca Universal</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
<header class="barra">
    <nav class="ancho" aria-label="Navegación principal">
        <a class="marca" href="controlador?accion=listar">Biblioteca Universal</a>
        <a href="controlador?accion=listar">Catálogo</a>
    </nav>
</header>
<main class="ancho">
    <div class="encabezado"><h1>${edicion ? 'Editar libro' : 'Nuevo libro'}</h1></div>
    <div class="form-container">
        <p class="ayuda">Todos los campos son obligatorios.</p>
        <c:if test="${not empty requestScope.error}">
            <p class="mensaje error" role="alert"><c:out value="${requestScope.error}"/></p>
        </c:if>
        <form action="controlador" method="post">
            <input type="hidden" name="accion" value="${edicion ? 'modificar' : 'insertar'}">
            <c:if test="${edicion}"><input type="hidden" name="id" value="${libroId}"></c:if>
            <div class="form-group">
                <label for="titulo">Título</label>
                <input type="text" id="titulo" name="titulo" maxlength="150" value="${fn:escapeXml(titulo)}" required>
            </div>
            <div class="form-group">
                <label for="autorId">Autor</label>
                <select id="autorId" name="autorId" required>
                    <option value="">Selecciona un autor</option>
                    <c:forEach var="autor" items="${autores}">
                        <option value="${autor.id}" <c:if test="${autor.id eq autorSeleccionado}">selected</c:if>><c:out value="${autor.nombre}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="generoId">Género</label>
                <select id="generoId" name="generoId" required>
                    <option value="">Selecciona un género</option>
                    <c:forEach var="genero" items="${generos}">
                        <option value="${genero.id}" <c:if test="${genero.id eq generoSeleccionado}">selected</c:if>><c:out value="${genero.nombre}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="anio">Año de publicación</label>
                <input type="number" id="anio" name="anio" min="-1000" max="${maxAnio}" value="${fn:escapeXml(anio)}" required>
            </div>
            <div class="form-actions">
                <button class="btn" type="submit">${edicion ? 'Guardar cambios' : 'Añadir libro'}</button>
                <a class="btn secundaria" href="controlador?accion=listar">Cancelar</a>
            </div>
        </form>
    </div>
</main>
<footer class="ancho">Biblioteca Universal · Acceso a Datos</footer>
</body>
</html>
