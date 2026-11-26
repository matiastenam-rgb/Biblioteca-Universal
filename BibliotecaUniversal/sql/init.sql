-- Biblioteca Universal - Script de inicialización
-- 5 tablas relacionadas: autores, géneros, libros, usuarios, préstamos

CREATE DATABASE IF NOT EXISTS biblioteca_universal
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE biblioteca_universal;

-- Tabla de autores
CREATE TABLE IF NOT EXISTS autores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    nacionalidad VARCHAR(60),
    fecha_nacimiento DATE,
    fecha_muerte DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de géneros
CREATE TABLE IF NOT EXISTS generos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de libros (relacionada con autores y géneros)
CREATE TABLE IF NOT EXISTS libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    anio INT NOT NULL,
    autor_id INT NOT NULL,
    genero_id INT NOT NULL,
    FOREIGN KEY (autor_id) REFERENCES autores(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (genero_id) REFERENCES generos(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de préstamos (relacionada con libros y usuarios)
CREATE TABLE IF NOT EXISTS prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    libro_id INT NOT NULL,
    usuario_id INT NOT NULL,
    fecha_prestamo DATE NOT NULL DEFAULT (CURRENT_DATE),
    fecha_devolucion DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'Prestado',
    FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Índices para mejorar búsquedas (idempotentes)
SET @exists = 0;
SELECT COUNT(*) INTO @exists FROM information_schema.statistics WHERE table_schema = 'biblioteca_universal' AND index_name = 'idx_libros_titulo';
SET @sql = IF(@exists = 0, 'CREATE INDEX idx_libros_titulo ON libros(titulo)', 'SELECT ""');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists = 0;
SELECT COUNT(*) INTO @exists FROM information_schema.statistics WHERE table_schema = 'biblioteca_universal' AND index_name = 'idx_libros_anio';
SET @sql = IF(@exists = 0, 'CREATE INDEX idx_libros_anio ON libros(anio)', 'SELECT ""');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists = 0;
SELECT COUNT(*) INTO @exists FROM information_schema.statistics WHERE table_schema = 'biblioteca_universal' AND index_name = 'idx_prestamos_estado';
SET @sql = IF(@exists = 0, 'CREATE INDEX idx_prestamos_estado ON prestamos(estado)', 'SELECT ""');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists = 0;
SELECT COUNT(*) INTO @exists FROM information_schema.statistics WHERE table_schema = 'biblioteca_universal' AND index_name = 'idx_prestamos_usuario';
SET @sql = IF(@exists = 0, 'CREATE INDEX idx_prestamos_usuario ON prestamos(usuario_id)', 'SELECT ""');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================
-- DATOS DE EJEMPLO (con tildes correctas)
-- ============================================

-- Autores originales (12) + nuevos (16) = 28 autores totales
-- Los sin tildes se actualizan primero si ya existen
INSERT IGNORE INTO autores (nombre, nacionalidad, fecha_nacimiento, fecha_muerte) VALUES
    ('Miguel de Cervantes', 'Española', '1547-09-29', '1616-04-23'),
    ('Gabriel García Márquez', 'Colombiana', '1927-03-06', '2014-04-17'),
    ('Jane Austen', 'Británica', '1775-12-16', '1817-06-18'),
    ('Fiódor Dostoievski', 'Rusa', '1821-11-11', '1881-02-09'),
    ('Homero', 'Griega', NULL, NULL),
    ('William Shakespeare', 'Británica', '1564-04-26', '1616-04-23'),
    ('Mario Vargas Llosa', 'Peruana', '1936-03-28', NULL),
    ('Jorge Luis Borges', 'Argentina', '1899-08-24', '1986-06-14'),
    ('Nathaniel Hawthorne', 'Estadounidense', '1804-07-04', '1864-05-19'),
    ('Isabel Allende', 'Chilena', '1942-08-02', NULL),
    ('Arthur Conan Doyle', 'Británica', '1859-05-22', '1930-07-07'),
    ('Julio Cortázar', 'Argentina', '1914-08-26', '1984-02-12'),
    ('J. K. Rowling', 'Británica', '1965-07-31', NULL),
    ('J. R. R. Tolkien', 'Británica', '1892-01-03', '1973-09-02'),
    ('Brandon Sanderson', 'Estadounidense', '1975-12-19', NULL),
    ('George R. R. Martin', 'Estadounidense', '1948-09-20', NULL),
    ('Rick Riordan', 'Estadounidense', '1964-06-05', NULL),
    ('C. S. Lewis', 'Británica', '1898-11-29', '1963-01-22'),
    ('Frank Herbert', 'Estadounidense', '1920-10-08', '1986-02-11'),
    ('Isaac Asimov', 'Estadounidense', '1920-01-02', '1992-04-06'),
    ('Suzanne Collins', 'Estadounidense', '1962-08-10', NULL),
    ('Patrick Rothfuss', 'Estadounidense', '1973-05-06', NULL),
    ('Christopher Paolini', 'Estadounidense', '1983-11-24', NULL),
    ('Andrzej Sapkowski', 'Polaca', '1948-06-21', NULL),
    ('George Orwell', 'Británica', '1903-06-25', '1950-01-21'),
    ('Aldous Huxley', 'Británica', '1894-07-26', '1963-11-22'),
    ('Ray Bradbury', 'Estadounidense', '1920-08-22', '2012-06-06'),
    ('Andy Weir', 'Estadounidense', '1972-06-16', NULL);

-- Géneros originales (7) + nuevos (6) = 13 géneros totales
INSERT IGNORE INTO generos (nombre, descripcion) VALUES
    ('Novela clásica', 'Obras narrativas de la literatura universal clásica'),
    ('Realismo mágico', 'Narrativa que mezcla lo real con lo fantástico'),
    ('Novela romántica', 'Obras centradas en las relaciones amorosas y emociones'),
    ('Novela psicológica', 'Narrativa que explora la mente y motivaciones humanas'),
    ('Épica', 'Narraciones heroicas y legendarias de la antigüedad'),
    ('Drama', 'Obras teatrales de tono serio y trágico'),
    ('Ficción filosófica', 'Narrativa que explora ideas y preguntas filosóficas'),
    ('Fantasía', 'Narrativa con elementos mágicos o sobrenaturales'),
    ('Fantasía épica', 'Obras de fantasía de gran escala con mundos inventados'),
    ('Fantasía mitológica', 'Narrativa basada en mitologías y dioses'),
    ('Ciencia ficción', 'Narrativa basada en ciencia y tecnología futura'),
    ('Distopía', 'Sociedades imaginarias opresivas o degradadas'),
    ('Sátira política', 'Crítica social y política a través del humor');

-- Libros: 15 originales + 99 nuevos = 114 libros totales
-- Nota: "El sobrino del mago" es de C. S. Lewis, no de Rick Riordan. Riordan tiene 12 libros.
-- Autores: 1=Cervantes, 2=García Márquez, 3=Austen, 4=Dostoievski, 5=Homero,
--           6=Shakespeare, 7=Vargas Llosa, 8=Borges, 9=Hawthorne, 10=Allende,
--           11=Conan Doyle, 12=Cortázar, 13=Rowling, 14=Tolkien, 15=Sanderson,
--           16=G.R.R. Martin, 17=Riordan, 18=C.S. Lewis, 19=Herbert, 20=Asimov,
--           21=Collins, 22=Rothfuss, 23=Paolini, 24=Sapkowski, 25=Orwell,
--           26=Huxley, 27=Bradbury, 28=Weir
-- Géneros: 1=Novela clásica, 2=Realismo mágico, 3=Novela romántica,
--          4=Novela psicológica, 5=Épica, 6=Drama, 7=Ficción filosófica,
--          8=Fantasía, 9=Fantasía épica, 10=Fantasía mitológica,
--          11=Ciencia ficción, 12=Distopía, 13=Sátira política
INSERT INTO libros (titulo, anio, autor_id, genero_id)
SELECT t.titulo, t.anio, t.autor_id, t.genero_id FROM (
    -- 15 libros originales
    SELECT 'Don Quijote de la Mancha' AS titulo, 1605 AS anio, 1 AS autor_id, 1 AS genero_id UNION ALL
    SELECT 'Cien años de soledad', 1967, 2, 2 UNION ALL
    SELECT 'Orgullo y prejuicio', 1813, 3, 3 UNION ALL
    SELECT 'Crimen y castigo', 1866, 4, 4 UNION ALL
    SELECT 'La Odisea', -800, 5, 5 UNION ALL
    SELECT 'Romeo y Julieta', 1597, 6, 6 UNION ALL
    SELECT 'Hamlet', 1603, 6, 6 UNION ALL
    SELECT 'La ciudad y los perros', 1963, 7, 1 UNION ALL
    SELECT 'Conversación en La Catedral', 1969, 7, 1 UNION ALL
    SELECT 'Ficciones', 1944, 8, 7 UNION ALL
    SELECT 'El jardín de senderos que se bifurcan', 1941, 8, 7 UNION ALL
    SELECT 'Las aventuras de Sherlock Holmes', 1892, 11, 1 UNION ALL
    SELECT 'La carta escarlata', 1850, 9, 4 UNION ALL
    SELECT 'Rayuela', 1963, 12, 7 UNION ALL
    SELECT 'La casa de los espíritus', 1982, 10, 2 UNION ALL
    -- Harry Potter (7 libros) - J.K. Rowling (13) - Fantasía (8)
    SELECT 'Harry Potter y la piedra filosofal', 1997, 13, 8 UNION ALL
    SELECT 'Harry Potter y la cámara secreta', 1998, 13, 8 UNION ALL
    SELECT 'Harry Potter y el prisionero de Azkaban', 1999, 13, 8 UNION ALL
    SELECT 'Harry Potter y el cáliz de fuego', 2000, 13, 8 UNION ALL
    SELECT 'Harry Potter y la Orden del Fénix', 2003, 13, 8 UNION ALL
    SELECT 'Harry Potter y el misterio del príncipe', 2005, 13, 8 UNION ALL
    SELECT 'Harry Potter y las reliquias de la Muerte', 2007, 13, 8 UNION ALL
    -- Tolkien (7 libros) - J.R.R. Tolkien (14) - Fantasía épica (9)
    SELECT 'El hobbit', 1937, 14, 9 UNION ALL
    SELECT 'La Comunidad del Anillo', 1954, 14, 9 UNION ALL
    SELECT 'Las dos torres', 1954, 14, 9 UNION ALL
    SELECT 'El retorno del rey', 1955, 14, 9 UNION ALL
    SELECT 'El Silmarillion', 1977, 14, 9 UNION ALL
    SELECT 'Los hijos de Húrin', 2007, 14, 9 UNION ALL
    SELECT 'Beren y Lúthien', 2017, 14, 9 UNION ALL
    SELECT 'La caída de Gondolin', 2018, 14, 9 UNION ALL
    -- Brandon Sanderson (14 libros) - (15) - Fantasía épica (9) / Fantasía (8)
    SELECT 'El imperio final', 2006, 15, 9 UNION ALL
    SELECT 'El pozo de la ascensión', 2007, 15, 9 UNION ALL
    SELECT 'El héroe de las eras', 2008, 15, 9 UNION ALL
    SELECT 'Aleación de ley', 2011, 15, 8 UNION ALL
    SELECT 'Sombras de identidad', 2015, 15, 8 UNION ALL
    SELECT 'Brazales de duelo', 2016, 15, 8 UNION ALL
    SELECT 'El metal perdido', 2022, 15, 8 UNION ALL
    SELECT 'El camino de los reyes', 2010, 15, 9 UNION ALL
    SELECT 'Palabras radiantes', 2014, 15, 9 UNION ALL
    SELECT 'Juramentada', 2017, 15, 9 UNION ALL
    SELECT 'El ritmo de la guerra', 2020, 15, 9 UNION ALL
    SELECT 'Viento y verdad', 2024, 15, 9 UNION ALL
    SELECT 'Danzante del filo', 2016, 15, 8 UNION ALL
    SELECT 'Esquirla del amanecer', 2020, 15, 8 UNION ALL
    -- George R.R. Martin (7 libros) - (16) - Fantasía épica (9)
    SELECT 'Juego de tronos', 1996, 16, 9 UNION ALL
    SELECT 'Choque de reyes', 1998, 16, 9 UNION ALL
    SELECT 'Tormenta de espadas', 2000, 16, 9 UNION ALL
    SELECT 'Festín de cuervos', 2005, 16, 9 UNION ALL
    SELECT 'Danza de dragones', 2011, 16, 9 UNION ALL
    SELECT 'Fuego y sangre', 2018, 16, 9 UNION ALL
    SELECT 'El caballero de los Siete Reinos', 2015, 16, 9 UNION ALL
    -- Rick Riordan (12 libros) - (17) - Fantasía mitológica (10)
    SELECT 'El ladrón del rayo', 2005, 17, 10 UNION ALL
    SELECT 'El mar de los monstruos', 2006, 17, 10 UNION ALL
    SELECT 'La maldición del titán', 2007, 17, 10 UNION ALL
    SELECT 'La batalla del laberinto', 2008, 17, 10 UNION ALL
    SELECT 'El último héroe del Olimpo', 2009, 17, 10 UNION ALL
    SELECT 'El cáliz de los dioses', 2023, 17, 10 UNION ALL
    SELECT 'La ira de la diosa triple', 2024, 17, 10 UNION ALL
    SELECT 'El héroe perdido', 2010, 17, 10 UNION ALL
    SELECT 'El hijo de Neptuno', 2011, 17, 10 UNION ALL
    SELECT 'La marca de Atenea', 2012, 17, 10 UNION ALL
    SELECT 'La casa de Hades', 2013, 17, 10 UNION ALL
    SELECT 'La sangre del Olimpo', 2014, 17, 10 UNION ALL
    -- C.S. Lewis (7 libros) - (18) - Fantasía (8)
    SELECT 'El león, la bruja y el armario', 1950, 18, 8 UNION ALL
    SELECT 'El caballo y el muchacho', 1954, 18, 8 UNION ALL
    SELECT 'El príncipe Caspian', 1951, 18, 8 UNION ALL
    SELECT 'La travesía del Viajero del Alba', 1952, 18, 8 UNION ALL
    SELECT 'La silla de plata', 1953, 18, 8 UNION ALL
    SELECT 'La última batalla', 1956, 18, 8 UNION ALL
    SELECT 'El sobrino del mago', 1955, 18, 8 UNION ALL
    -- Frank Herbert (6 libros) - (19) - Ciencia ficción (11)
    SELECT 'Dune', 1965, 19, 11 UNION ALL
    SELECT 'El mesías de Dune', 1969, 19, 11 UNION ALL
    SELECT 'Hijos de Dune', 1976, 19, 11 UNION ALL
    SELECT 'Dios emperador de Dune', 1981, 19, 11 UNION ALL
    SELECT 'Herejes de Dune', 1984, 19, 11 UNION ALL
    SELECT 'Casa Capitular Dune', 1985, 19, 11 UNION ALL
    -- Isaac Asimov (7 libros) - (20) - Ciencia ficción (11)
    SELECT 'Fundación', 1951, 20, 11 UNION ALL
    SELECT 'Fundación e Imperio', 1952, 20, 11 UNION ALL
    SELECT 'Segunda Fundación', 1953, 20, 11 UNION ALL
    SELECT 'Los límites de la Fundación', 1982, 20, 11 UNION ALL
    SELECT 'Fundación y Tierra', 1986, 20, 11 UNION ALL
    SELECT 'Preludio a la Fundación', 1988, 20, 11 UNION ALL
    SELECT 'Hacia la Fundación', 1993, 20, 11 UNION ALL
    -- Suzanne Collins (5 libros) - (21) - Distopía (12)
    SELECT 'Los juegos del hambre', 2008, 21, 12 UNION ALL
    SELECT 'En llamas', 2009, 21, 12 UNION ALL
    SELECT 'Sinsajo', 2010, 21, 12 UNION ALL
    SELECT 'Balada de pájaros cantores y serpientes', 2020, 21, 12 UNION ALL
    SELECT 'Amanecer en la cosecha', 2025, 21, 12 UNION ALL
    -- Patrick Rothfuss (4 libros) - (22) - Fantasía (8)
    SELECT 'El nombre del viento', 2007, 22, 8 UNION ALL
    SELECT 'El temor de un hombre sabio', 2011, 22, 8 UNION ALL
    SELECT 'La música del silencio', 2014, 22, 8 UNION ALL
    SELECT 'El estrecho sendero entre deseos', 2023, 22, 8 UNION ALL
    -- Christopher Paolini (5 libros) - (23) - Fantasía (8)
    SELECT 'Eragon', 2002, 23, 8 UNION ALL
    SELECT 'Eldest', 2005, 23, 8 UNION ALL
    SELECT 'Brisingr', 2008, 23, 8 UNION ALL
    SELECT 'Legado', 2011, 23, 8 UNION ALL
    SELECT 'Murtagh', 2023, 23, 8 UNION ALL
    -- Andrzej Sapkowski (9 libros) - (24) - Fantasía (8)
    SELECT 'El último deseo', 1993, 24, 8 UNION ALL
    SELECT 'La espada del destino', 1992, 24, 8 UNION ALL
    SELECT 'La sangre de los elfos', 1994, 24, 8 UNION ALL
    SELECT 'Tiempo de odio', 1995, 24, 8 UNION ALL
    SELECT 'Bautismo de fuego', 1996, 24, 8 UNION ALL
    SELECT 'La torre de la golondrina', 1997, 24, 8 UNION ALL
    SELECT 'La dama del lago', 1999, 24, 8 UNION ALL
    SELECT 'Estación de tormentas', 2013, 24, 8 UNION ALL
    SELECT 'Temporada de guerra', 2014, 24, 8 UNION ALL
    -- George Orwell (2 libros) - (25) - Distopía (12) / Sátira política (13)
    SELECT '1984', 1949, 25, 12 UNION ALL
    SELECT 'Rebelión en la granja', 1945, 25, 13 UNION ALL
    -- Aldous Huxley (1 libro) - (26) - Distopía (12)
    SELECT 'Un mundo feliz', 1932, 26, 12 UNION ALL
    -- Ray Bradbury (2 libros) - (27) - Distopía (12) / Ciencia ficción (11)
    SELECT 'Fahrenheit 451', 1953, 27, 12 UNION ALL
    SELECT 'Crónicas marcianas', 1950, 27, 11 UNION ALL
    -- Andy Weir (2 libros) - (28) - Ciencia ficción (11)
    SELECT 'El marciano', 2011, 28, 11 UNION ALL
    SELECT 'Proyecto Hail Mary', 2021, 28, 11
) AS t
WHERE NOT EXISTS (SELECT 1 FROM libros l WHERE l.titulo = t.titulo);

-- Usuarios (idempotente: email es UNIQUE)
INSERT IGNORE INTO usuarios (nombre, email, fecha_registro) VALUES
    ('Ana García', 'ana.garcia@email.com', '2025-01-15'),
    ('Carlos López', 'carlos.lopez@email.com', '2025-02-20'),
    ('María Fernández', 'maria.fernandez@email.com', '2025-03-10');

-- Préstamos (idempotente: se verifica por libro_id + usuario_id + fecha_prestamo)
INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_devolucion, estado)
SELECT t.libro_id, t.usuario_id, t.fecha_prestamo, t.fecha_devolucion, t.estado FROM (
    SELECT 1 AS libro_id, 1 AS usuario_id, '2025-09-01' AS fecha_prestamo, '2025-09-15' AS fecha_devolucion, 'Devuelto' AS estado UNION ALL
    SELECT 2, 2, '2025-09-10', NULL, 'Prestado' UNION ALL
    SELECT 3, 1, '2025-10-01', NULL, 'Prestado' UNION ALL
    SELECT 5, 3, '2025-10-05', '2025-10-20', 'Devuelto' UNION ALL
    SELECT 9, 2, '2025-11-01', NULL, 'Prestado'
) AS t
WHERE NOT EXISTS (
    SELECT 1 FROM prestamos p
    WHERE p.libro_id = t.libro_id AND p.usuario_id = t.usuario_id AND p.fecha_prestamo = t.fecha_prestamo
);

-- La creación del usuario y sus permisos se gestiona
-- mediante las variables de entorno de Docker Compose.