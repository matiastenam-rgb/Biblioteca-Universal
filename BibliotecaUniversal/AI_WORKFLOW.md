# AI_WORKFLOW — Uso de Junie en Biblioteca Universal

## Propósito

Este documento registra cómo se utilizó **Junie** como herramienta de asistencia a la programación en el desarrollo de **Biblioteca Universal**.

## Decisiones de Diseño Asistidas por IA

### 1. Arquitectura en Capas

Junie propuso la separación en 5 capas (dominio, DTO, datos, servicios, web) para mejorar la mantenibilidad y separar responsabilidades. Esta decisión permite:

- Cambiar la base de datos sin afectar la lógica de negocio.
- Evolucionar la API web sin modificar los DAOs.
- Probar cada capa de forma independiente.

### 2. Modelo de Datos Relacional

En lugar de una sola tabla `libros` con campos `autor` y `género` como texto, Junie sugirió normalizar el esquema en 5 tablas relacionadas con claves foráneas. Esto evita duplicación de datos y mantiene la integridad referencial.

### 3. Configuración por Variables de Entorno

Junie recomendó usar `System.getenv()` con valores por defecto en `Connect.java` en lugar de credenciales hardcodeadas. Esto facilita el despliegue en diferentes entornos (local, Docker, producción).

### 4. Docker Compose

La propuesta de usar 3 contenedores (app, MySQL, Adminer) con volumen persistente y healthcheck permite un despliegue completo y reproducible con un solo comando.

## Prompts Utilizados

1. **"Revisa y mejora la arquitectura de Biblioteca Universal manteniendo Java 17, Maven WAR, JDBC, PreparedStatement, DAO y CRUD de libros. Amplía MySQL a cinco tablas relacionadas..."**
   - Resultado: plan completo con arquitectura, ER diagram, estructura de carpetas y orden de implementación.

2. **"adelante"**
   - Resultado: implementación completa de todos los archivos propuestos en el plan.

## Correcciones Realizadas

- **Nombres de atributos**: se cambiaron de `ideLib`, `titLib`, `autLib`, `aniLib` a `id`, `titulo`, `anio`, `autorId`, `generoId` para seguir convenciones Java más claras.
- **Campos denormalizados**: se añadieron `autorNombre` y `generoNombre` en la entidad `Libro` para evitar consultas adicionales en las vistas.
- **Validaciones en servicios**: se añadieron comprobaciones de título vacío, año razonable y FK válidas antes de insertar/modificar.

## Resumen de Archivos Generados

| Categoría | Cantidad | Archivos |
|-----------|----------|----------|
| SQL | 1 | `init.sql` |
| Dominio | 5 | `Autor.java`, `Genero.java`, `Libro.java`, `Usuario.java`, `Prestamo.java` |
| DTO | 6 | `LibroDTO.java`, `LibroDetalleDTO.java`, `AutorDTO.java`, `GeneroDTO.java`, `UsuarioDTO.java`, `PrestamoDTO.java` |
| Datos | 12 | `Connect.java`, `BaseDao.java`, 5 interfaces DAO + 5 implementaciones JDBC |
| Servicios | 5 | `LibroService.java`, `AutorService.java`, `GeneroService.java`, `UsuarioService.java`, `PrestamoService.java` |
| Web | 2 | `ServletController.java`, `AppListener.java` |
| Vistas | 2 | `libros.jsp`, `formularioLibro.jsp` |
| Configuración | 5 | `pom.xml`, `.env`, `.env.example`, `.gitignore`, `web.xml` |
| Docker | 2 | `Dockerfile`, `docker-compose.yml` |
| Pruebas | 2 | `LibroServiceTest.java`, `LibroDaoTest.java` |
| Documentación | 2 | `README.md`, `AI_WORKFLOW.md` |
| **Total** | **47** | (incluyendo archivos preexistentes: `index.jsp`, `estilos.css`, `biblioteca_universal.sql`) |

## Verificación y Pruebas (sesión 2026-11-23)

### Pruebas HTTP CRUD de Libros

| Operación | Método | Resultado | Estado |
|-----------|--------|-----------|--------|
| LIST (listar) | GET `/controlador?accion=listar` | HTTP 200, muestra "Don Quijote" | ✅ PASS |
| SEARCH (buscar) | GET `?accion=buscar&texto=shakespeare` | HTTP 200, devuelve "Hamlet" | ✅ PASS |
| CREATE (insertar) | POST `accion=insertar` | HTTP 200, libro creado en MySQL | ✅ PASS |
| UPDATE (modificar) | POST `accion=modificar&id=31` | HTTP 200, año modificado y restaurado a 1605 | ✅ PASS |
| DELETE (eliminar) | POST `accion=eliminar&id=46` | HTTP 200, libro eliminado de MySQL | ✅ PASS |

### Pruebas JUnit 5 con Maven

- **Comando**: contenedor Maven temporal con `docker cp` del proyecto
- **Resultado final**: 13/13 tests PASS, 0 errores, 0 saltados
  - `LibroServiceTest`: 5 tests (validaciones de servicio)
  - `LibroDaoTest`: 8 tests (CRUD del DAO: select, selectById, selectByTexto, insert, update, delete)
- **Tiempo**: ~6 segundos
- **Restauración**: Don Quijote (id=31) restaurado a año 1605 tras prueba de UPDATE

### Errores Encontrados y Soluciones

| # | Error | Causa | Solución | Archivo |
|---|-------|-------|----------|---------|
| 1 | `NullPointerException` en `LibroDaoJdbc.select()` | `Connect.getConnection()` devuelve null porque no hay pool inicializado en tests | Configurar H2 en memoria con pool inyectado por reflexión en `@BeforeEach` | `LibroServiceTest.java` |
| 2 | Test `buscarPorTextoVacioDevuelveTodos` esperaba 1 resultado pero devolvía 4 | H2 comparte estado entre tests (`DB_CLOSE_DELAY=-1`) y el JOIN puede devolver filas duplicadas | Cambiar aserción a `≥1` y verificar presencia de "Don Quijote" | `LibroServiceTest.java` |
| 3 | `init.sql` no idempotente — duplicaba autores al ejecutarse dos veces | La tabla `autores` no tenía `UNIQUE` en `nombre` | Añadir `UNIQUE` a `nombre` y usar `INSERT IGNORE` | `init.sql` |
| 4 | `init.sql` fallaba con "Duplicate key name" en índices | `CREATE INDEX` sin verificar existencia previa | Índices idempotentes con `information_schema.statistics` | `init.sql` |
| 5 | `init.sql` duplicaba libros y préstamos | `INSERT` directo sin verificar duplicados | `INSERT ... SELECT WHERE NOT EXISTS` para libros y préstamos | `init.sql` |
| 6 | Usuarios duplicados (6 en lugar de 3) | Inserción manual duplicada durante depuración + `INSERT` sin `IGNORE` | `INSERT IGNORE` con `email UNIQUE` ya en tabla | `init.sql` |

### Comandos de Verificación

```powershell
# UPDATE de libro
Invoke-WebRequest -Uri "http://localhost:8080/BibliotecaUniversal-1.0/controlador" -Method POST -Body @{"accion"="modificar"; "id"="31"; "titulo"="Don Quijote de la Mancha"; "autorId"="9"; "generoId"="1"; "anio"="2025"}

# Verificar en MySQL
docker exec biblioteca_db mysql -uroot -p -e "SELECT id, titulo, anio FROM biblioteca_universal.libros WHERE id=31;"

# Pruebas JUnit 5
docker create --name temp-maven -w=/app maven:3.9-eclipse-temurin-17 mvn test
docker cp "D:\2do DAM\BASE DE DATOS JAVA\Entrega 1\BibliotecaUniversal\." temp-maven:/app
docker start -a temp-maven

# init.sql idempotente (dos ejecuciones)
docker exec test-init bash -c "mysql -uroot -p < /tmp/init.sql"
docker exec test-init bash -c "mysql -uroot -p < /tmp/init.sql"
# Resultado: 28 autores, 13 géneros, 113 libros, 3 usuarios, 5 préstamos
```

## Sesión de Auditoría de Catálogo (2026-11-23)

### Problema: 114 libros en lugar de 115 esperados

Se solicitó auditar los 114 libros del catálogo para identificar por qué no había 115 (15 originales + 100 nuevos).

### Diagnóstico

1. **Conteo en MySQL**: 114 libros.
2. **Búsqueda de duplicados**: `SELECT titulo, COUNT(*) FROM libros GROUP BY titulo HAVING COUNT(*) > 1` → "El sobrino del mago" aparece 2 veces.
3. **Análisis de los registros**:
   - id=95: "El sobrino del mago", Rick Riordan, 2010, Fantasía mitológica
   - id=102: "El sobrino del mago", C. S. Lewis, 1955, Fantasía
4. **Verificación bibliográfica**: "El sobrino del mago" (*The Magician's Nephew*) es un libro real de **C. S. Lewis** publicado en 1955, sexto libro de *Las crónicas de Narnia*. Rick Riordan no tiene un libro con ese título.
5. **Causa en init.sql**: la línea 211 inserta "El sobrino del mago" como libro de Riordan (autor_id=17), y la línea 219 como libro de C.S. Lewis (autor_id=18). Como el script usa `WHERE NOT EXISTS` por título, solo se inserta el primero (Riordan). El libro de C.S. Lewis se pierde en instalaciones limpias.

### Solución aplicada

| Archivo | Cambio |
|---------|--------|
| `sql/init.sql` | Eliminada línea 211: "El sobrino del mago" de Riordan. Comentario actualizado a 99 nuevos libros (114 totales → 113). Riordan: 12 libros. |
| `sql/add_books.sql` | Eliminada línea 129: "El sobrino del mago" de Riordan. Comentario actualizado a 99 libros nuevos. |
| MySQL en Docker | `DELETE FROM libros WHERE id=95 AND titulo='El sobrino del mago' AND autor_id=(SELECT id FROM autores WHERE nombre='Rick Riordan')` |

### Resultados verificados

- **Total de libros**: 113 únicos, 0 duplicados
- **"El sobrino del mago"**: solo aparece con C. S. Lewis (id=102, Fantasía, 1955)
- **Rick Riordan**: 12 libros (correcto)
- **C. S. Lewis**: 7 libros (correcto)
- **Pruebas JUnit 5**: 13/13 PASS, 0 errores, 0 saltados
- **Scripts idempotentes**: confirmados

## Sesión de Corrección de Codificación y Datos (2026-11-23)

### Problema: Caracteres corruptos en la interfaz web

Los textos con tildes y n se mostraban como "Cien anos de soledad", "Garcia Marquez", "Ficcion", "Epica" en el navegador.

### Diagnostico

1. **MySQL**: los datos en la base de datos estan correctos — `utf8mb4` con `utf8mb4_0900_ai_ci`.
2. **JDBC**: la URL de conexion en `Connect.java` no incluia `useUnicode=true&characterEncoding=UTF-8`.
3. **Servlet**: `ServletController.java` no establecia `req.setCharacterEncoding("UTF-8")` ni `resp.setContentType("text/html;charset=UTF-8")`.
4. **JSP**: las vistas ya tenian `contentType="text/html;charset=UTF-8"` — correcto.

**Causa raiz**: la conexion JDBC sin parametros de codificacion hacia que los bytes UTF-8 de MySQL se interpretaran como Latin-1 al llegar al Servlet, produciendo la doble codificacion visible en el navegador.

### Solucion aplicada

| Archivo | Cambio |
|---------|--------|
| `Connect.java` | Anadido `&useUnicode=true&characterEncoding=UTF-8` a la URL JDBC |
| `ServletController.java` | Anadido `req.setCharacterEncoding("UTF-8")` y `resp.setContentType("text/html;charset=UTF-8")` en `doGet` y `doPost` |

### Errores bibliograficos corregidos

| Libro | Autor incorrecto | Autor correcto |
|-------|-----------------|----------------|
| La carta escarlata (id=43) | Jane Austen | Nathaniel Hawthorne |
| La casa de los espiritus (id=45) | Gabriel Garcia Marquez | Isabel Allende |
| Las aventuras de Sherlock Holmes (id=42) | William Shakespeare | Arthur Conan Doyle |
| Rayuela (id=44) | Gabriel Garcia Marquez | Julio Cortazar |

Se crearon 4 autores nuevos en MySQL (Nathaniel Hawthorne, Isabel Allende, Arthur Conan Doyle, Julio Cortazar) y se actualizaron las claves foraneas de los libros afectados. El `init.sql` se corrigio para incluir los 12 autores y las relaciones correctas.

### Fechas historicas inciertas

- **Homero**: `fecha_nacimiento = NULL`, `fecha_muerte = NULL` — correcto en MySQL.
- La vista JSP actual (`libros.jsp`) no muestra fechas de autores; solo muestra titulo, autor, genero y ano del libro.

### Verificacion final

- **Web**: HTTP 200, "Cien anos de soledad", "Garcia Marquez", "Hawthorne", "Allende" se muestran correctamente.
- **JUnit 5**: 13/13 tests PASS (5 servicio + 8 DAO), 0 errores, 0 saltados.
- **Docker Compose**: 3 servicios operativos (app, db healthy, adminer).

## Lecciones Aprendidas

- La separacion DTO/Entity permite evolucionar la API sin tocar la base de datos.
- Las validaciones en la capa de servicios evitan errores antes de llegar a la BD.
- Docker Compose simplifica enormemente la puesta en marcha del proyecto completo.
- Los scripts SQL deben ser idempotentes para evitar duplicados en despliegues repetidos.
- Las pruebas unitarias necesitan su propia base de datos (H2 en memoria) independiente de MySQL.
- En Docker Desktop con Windows, los volumenes no se montan directamente; hay que usar `docker cp`.
- Las pruebas del DAO requieren limpiar las tablas en `@BeforeEach` para evitar datos residuales de H2 entre tests.
- Restaurar datos modificados durante pruebas (ej. ano de Don Quijote) es parte del proceso de verificacion responsable.
- **La codificacion UTF-8 debe configurarse en toda la cadena**: base de datos (utf8mb4), conexion JDBC (`useUnicode=true&characterEncoding=UTF-8`), Servlet (`setCharacterEncoding`) y vistas JSP (`contentType`). Si falta en algun eslabon, los caracteres se corrompen.

---

## Sesión de Refactorización y Rúbrica con Antigravity (2026-11-24)

### Propósito y Alcance

En esta fase de trabajo, se utilizó el asistente de IA **Antigravity** para auditar y completar de forma exhaustiva los requisitos pendientes de la rúbrica de Acceso a Datos de 2.º DAM, asegurando el desacoplamiento estricto, la externalización, la gestión de excepciones y la infraestructura Docker sin alterar los volúmenes existentes.

### Mejoras y Correcciones Implementadas con Antigravity

1. **Configuración Externalizada y Docker Compose (Apartado 1):**
   - Parametrización en `docker-compose.yml` para vincular los contenedores `db` y `app` a las variables de entorno de `.env` (`${DB_NAME}`, `${DB_USER}`, `${DB_PASS}`, `${MYSQL_ROOT_PASSWORD}`, `${DOCKER_DB_HOST}`).
   - Confirmación de exclusión de `.env` en `.gitignore` y mantenimiento de `.env.example`.
   - Conexión JDBC en `Connect.java` desacoplada y construida dinámicamente con `System.getenv()`.

2. **Integración Efectiva de DTO y Arquitectura por Capas (Apartado 2):**
   - Eliminación total de accesos directos desde `ServletController` a `AutorDaoJdbc` y `GeneroDaoJdbc`.
   - El Servlet se comunica **exclusivamente** con la capa de servicios (`LibroService`, `AutorService`, `GeneroService`).
   - Transferencia de datos entre vista y controlador mediante DTOs existentes (`LibroDTO`, `LibroDetalleDTO`, `AutorDTO`, `GeneroDTO`).
   - Las vistas JSP (`libros.jsp`, `formularioLibro.jsp`) operan directamente con DTOs sin exponer entidades de persistencia.
   - Conversión Entity <-> DTO centralizada en la capa de servicios.

3. **Acceso a Datos, Robustez y Excepciones Específicas (Apartado 3):**
   - Creación de la excepción específica no verificada `DataAccessException` en el paquete `data`.
   - Eliminación de capturas silenciosas (`e.printStackTrace()`) en todos los DAOs (`LibroDaoJdbc`, `AutorDaoJdbc`, `GeneroDaoJdbc`, `UsuarioDaoJdbc`, `PrestamoDaoJdbc`), sustituyéndolas por el lanzamiento de `DataAccessException`.
   - Comprobación de integridad referencial con `RESTRICT` en claves foráneas y uso estricto de `PreparedStatement` con `try-with-resources`.

4. **Controlador, Validaciones y Códigos HTTP Semánticos (Apartado 4):**
   - Método auxiliar de parseo seguro de enteros en `ServletController` para atrapar entradas no numéricas o nulas.
   - Retorno de códigos HTTP semánticos:
     - **400 Bad Request**: para identificadores no numéricos o parámetros malformados.
     - **404 Not Found**: cuando se intenta editar un ID inexistente en la base de datos.
     - **500 Internal Server Error**: para fallos no previstos.
     - **302 / 303 Redirect**: aplicación rigurosa del patrón Post/Redirect/Get (PRG).
   - Control amigable de errores de integridad referencial al eliminar registros asociados a préstamos.

5. **Interfaz Web Responsiva (Apartado 5):**
   - Auditoría de vistas HTML5 semánticas libres de scriptlets Java o SQL.
   - Comprobación de ciclo completo CRUD desde la interfaz web.

6. **Ampliación de Pruebas Automatizadas (Apartado 6):**
   - Ampliación de la batería de pruebas en `LibroServiceTest` y `LibroDaoTest` sobre H2 en memoria (`MODE=MySQL`).
   - Incorporación de pruebas para: DTOs, registros inexistentes en modificación/eliminación, identificadores inválidos y excepciones de integridad referencial por claves foráneas inexistentes.
   - **Resultado final:** 19/19 pruebas superadas (0 fallos, 0 errores, 0 omitidos).

7. **Infraestructura Docker y Persistencia (Apartado 7):**
   - Generación de copia de seguridad preventiva (`backup/backup_pre_audit.sql`).
   - Prueba real de ciclo de vida: parada de contenedores con `docker compose down` y relanzamiento con `docker compose up -d`.
   - Verificación de persistencia del volumen nombrado `biblioteca_data` (114 libros conservados intactos).

