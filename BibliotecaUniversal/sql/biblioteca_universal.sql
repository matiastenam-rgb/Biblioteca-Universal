-- Base de datos para Biblioteca Universal
CREATE DATABASE IF NOT EXISTS biblioteca_universal
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE biblioteca_universal;

-- Tabla de libros
CREATE TABLE IF NOT EXISTS libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    anio INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de ejemplo
INSERT INTO libros (titulo, autor, anio) VALUES
    ('Don Quijote de la Mancha', 'Miguel de Cervantes', 1605),
    ('Cien años de soledad', 'Gabriel García Márquez', 1967),
    ('La Odisea', 'Homero', -800),
    ('Orgullo y prejuicio', 'Jane Austen', 1813),
    ('Crimen y castigo', 'Fiódor Dostoievski', 1866);

-- Usuario para la aplicación (creado por Docker Compose mediante variables de entorno)
