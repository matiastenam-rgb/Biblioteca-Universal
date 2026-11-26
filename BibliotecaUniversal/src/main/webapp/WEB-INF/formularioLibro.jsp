<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty libro ? 'Editar' : 'Nuevo'} libro</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>${not empty libro ? '✏️ Editar libro' : '📖 Nuevo libro'}</h1>

        <c:if test="${not empty error}">
            <div class="mensaje error">${error}</div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <div class="form-container">
            <form action="controlador" method="post">
                <c:if test="${not empty libro}">
                    <input type="hidden" name="accion" value="modificar">
                    <input type="hidden" name="id" value="${libro.id}">
                </c:if>
                <c:if test="${empty libro}">
                    <input type="hidden" name="accion" value="insertar">
                </c:if>

                <div class="form-group">
                    <label for="titulo">Título:</label>
                    <input type="text" id="titulo" name="titulo"
                           value="${not empty libro ? libro.titulo : param.titulo}" required>
                </div>

                <div class="form-group">
                    <label for="autorId">Autor:</label>
                    <select id="autorId" name="autorId" required>
                        <option value="">-- Seleccionar autor --</option>
                        <c:forEach var="autor" items="${autores}">
                            <option value="${autor.id}"
                                <c:if test="${not empty libro and libro.autorId == autor.id}">selected</c:if>>
                                ${autor.nombre}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="generoId">Género:</label>
                    <select id="generoId" name="generoId" required>
                        <option value="">-- Seleccionar género --</option>
                        <c:forEach var="genero" items="${generos}">
                            <option value="${genero.id}"
                                <c:if test="${not empty libro and libro.generoId == genero.id}">selected</c:if>>
                                ${genero.nombre}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="anio">Año de publicación:</label>
                    <input type="number" id="anio" name="anio"
                           value="${not empty libro ? libro.anio : param.anio}" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn">
                        ${not empty libro ? 'Guardar cambios' : 'Añadir libro'}
                    </button>
                    <a href="controlador?accion=listar" class="btn btn-cancelar">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
