-- Script para corregir tildes y añadir datos nuevos
USE biblioteca_universal;

-- Corregir autores con tildes corruptas
UPDATE autores SET nombre='Gabriel García Márquez' WHERE id=10;
UPDATE autores SET nombre='Fiódor Dostoievski' WHERE id=12;
UPDATE autores SET nombre='Julio Cortázar' WHERE id=20;

-- Corregir géneros con tildes corruptas
UPDATE generos SET nombre='Novela clásica', descripcion='Obras narrativas de la literatura universal clásica' WHERE nombre='Novela clasica' OR nombre='Novela clÃ¡sica';
UPDATE generos SET nombre='Realismo mágico', descripcion='Narrativa que mezcla lo real con lo fantástico' WHERE nombre='Realismo magico' OR nombre='Realismo mÃ¡gico';
UPDATE generos SET nombre='Novela romántica', descripcion='Obras centradas en las relaciones amorosas y emociones' WHERE nombre='Novela romantica' OR nombre='Novela romÃ¡ntica';
UPDATE generos SET nombre='Novela psicológica', descripcion='Narrativa que explora la mente y motivaciones humanas' WHERE nombre='Novela psicologica' OR nombre='Novela psicolÃ³gica';
UPDATE generos SET nombre='Épica', descripcion='Narraciones heroicas y legendarias de la antigüedad' WHERE nombre='Epica' OR nombre='Ã‰pica';
UPDATE generos SET nombre='Drama', descripcion='Obras teatrales de tono serio y trágico' WHERE nombre='Drama';
UPDATE generos SET nombre='Ficción filosófica', descripcion='Narrativa que explora ideas y preguntas filosóficas' WHERE nombre='Ficcion filosofica' OR nombre='FicciÃ³n filosÃ³fica';

-- Corregir libros existentes con tildes
UPDATE libros SET titulo='Cien años de soledad' WHERE titulo='Cien anos de soledad' OR titulo='Cien aÃ±os de soledad';
UPDATE libros SET titulo='Conversación en La Catedral' WHERE titulo='Conversacion en La Catedral' OR titulo='ConversiÃ³n en La Catedral';
UPDATE libros SET titulo='El jardín de senderos que se bifurcan' WHERE titulo='El jardin de senderos que se bifurcan' OR titulo='El jardÃ­n de senderos que se bifurcan';
UPDATE libros SET titulo='La casa de los espíritus' WHERE titulo='La casa de los espiritus' OR titulo='La casa de los espÃ­ritus';

-- Actualizar usuarios con tildes
UPDATE usuarios SET nombre='Ana García' WHERE email='ana.garcia@email.com';
UPDATE usuarios SET nombre='Carlos López' WHERE email='carlos.lopez@email.com';
UPDATE usuarios SET nombre='María Fernández' WHERE email='maria.fernandez@email.com';

-- Obtener IDs de autores y géneros
SET @cervantes = (SELECT id FROM autores WHERE nombre='Miguel de Cervantes');
SET @garcia_marquez = (SELECT id FROM autores WHERE nombre='Gabriel García Márquez');
SET @austen = (SELECT id FROM autores WHERE nombre='Jane Austen');
SET @dostoievski = (SELECT id FROM autores WHERE nombre='Fiódor Dostoievski');
SET @homero = (SELECT id FROM autores WHERE nombre='Homero');
SET @shakespeare = (SELECT id FROM autores WHERE nombre='William Shakespeare');
SET @vargas_llosa = (SELECT id FROM autores WHERE nombre='Mario Vargas Llosa');
SET @borges = (SELECT id FROM autores WHERE nombre='Jorge Luis Borges');
SET @hawthorne = (SELECT id FROM autores WHERE nombre='Nathaniel Hawthorne');
SET @allende = (SELECT id FROM autores WHERE nombre='Isabel Allende');
SET @conan_doyle = (SELECT id FROM autores WHERE nombre='Arthur Conan Doyle');
SET @cortazar = (SELECT id FROM autores WHERE nombre='Julio Cortázar');
SET @rowling = (SELECT id FROM autores WHERE nombre='J. K. Rowling');
SET @tolkien = (SELECT id FROM autores WHERE nombre='J. R. R. Tolkien');
SET @sanderson = (SELECT id FROM autores WHERE nombre='Brandon Sanderson');
SET @grrm = (SELECT id FROM autores WHERE nombre='George R. R. Martin');
SET @riordan = (SELECT id FROM autores WHERE nombre='Rick Riordan');
SET @cs_lewis = (SELECT id FROM autores WHERE nombre='C. S. Lewis');
SET @herbert = (SELECT id FROM autores WHERE nombre='Frank Herbert');
SET @asimov = (SELECT id FROM autores WHERE nombre='Isaac Asimov');
SET @collins = (SELECT id FROM autores WHERE nombre='Suzanne Collins');
SET @rothfuss = (SELECT id FROM autores WHERE nombre='Patrick Rothfuss');
SET @paolini = (SELECT id FROM autores WHERE nombre='Christopher Paolini');
SET @sapkowski = (SELECT id FROM autores WHERE nombre='Andrzej Sapkowski');
SET @orwell = (SELECT id FROM autores WHERE nombre='George Orwell');
SET @huxley = (SELECT id FROM autores WHERE nombre='Aldous Huxley');
SET @bradbury = (SELECT id FROM autores WHERE nombre='Ray Bradbury');
SET @weir = (SELECT id FROM autores WHERE nombre='Andy Weir');

SET @novela_clasica = (SELECT id FROM generos WHERE nombre='Novela clásica');
SET @realismo_magico = (SELECT id FROM generos WHERE nombre='Realismo mágico');
SET @novela_romantica = (SELECT id FROM generos WHERE nombre='Novela romántica');
SET @novela_psicologica = (SELECT id FROM generos WHERE nombre='Novela psicológica');
SET @epica = (SELECT id FROM generos WHERE nombre='Épica');
SET @drama = (SELECT id FROM generos WHERE nombre='Drama');
SET @ficcion_filosofica = (SELECT id FROM generos WHERE nombre='Ficción filosófica');
SET @fantasia = (SELECT id FROM generos WHERE nombre='Fantasía');
SET @fantasia_epica = (SELECT id FROM generos WHERE nombre='Fantasía épica');
SET @fantasia_mitologica = (SELECT id FROM generos WHERE nombre='Fantasía mitológica');
SET @ciencia_ficcion = (SELECT id FROM generos WHERE nombre='Ciencia ficción');
SET @distopia = (SELECT id FROM generos WHERE nombre='Distopía');
SET @satira_politica = (SELECT id FROM generos WHERE nombre='Sátira política');

-- Insertar 99 libros nuevos (usando variables para IDs dinámicos)
-- Nota: "El sobrino del mago" es de C. S. Lewis, no de Rick Riordan.
INSERT INTO libros (titulo, anio, autor_id, genero_id)
SELECT t.titulo, t.anio, t.autor_id, t.genero_id FROM (
    -- Harry Potter (7) - Rowling - Fantasía
    SELECT 'Harry Potter y la piedra filosofal' AS titulo, 1997 AS anio, @rowling AS autor_id, @fantasia AS genero_id UNION ALL
    SELECT 'Harry Potter y la cámara secreta', 1998, @rowling, @fantasia UNION ALL
    SELECT 'Harry Potter y el prisionero de Azkaban', 1999, @rowling, @fantasia UNION ALL
    SELECT 'Harry Potter y el cáliz de fuego', 2000, @rowling, @fantasia UNION ALL
    SELECT 'Harry Potter y la Orden del Fénix', 2003, @rowling, @fantasia UNION ALL
    SELECT 'Harry Potter y el misterio del príncipe', 2005, @rowling, @fantasia UNION ALL
    SELECT 'Harry Potter y las reliquias de la Muerte', 2007, @rowling, @fantasia UNION ALL
    -- Tolkien (7) - Fantasía épica
    SELECT 'El hobbit', 1937, @tolkien, @fantasia_epica UNION ALL
    SELECT 'La Comunidad del Anillo', 1954, @tolkien, @fantasia_epica UNION ALL
    SELECT 'Las dos torres', 1954, @tolkien, @fantasia_epica UNION ALL
    SELECT 'El retorno del rey', 1955, @tolkien, @fantasia_epica UNION ALL
    SELECT 'El Silmarillion', 1977, @tolkien, @fantasia_epica UNION ALL
    SELECT 'Los hijos de Húrin', 2007, @tolkien, @fantasia_epica UNION ALL
    SELECT 'Beren y Lúthien', 2017, @tolkien, @fantasia_epica UNION ALL
    SELECT 'La caída de Gondolin', 2018, @tolkien, @fantasia_epica UNION ALL
    -- Sanderson (14) - Fantasía épica / Fantasía
    SELECT 'El imperio final', 2006, @sanderson, @fantasia_epica UNION ALL
    SELECT 'El pozo de la ascensión', 2007, @sanderson, @fantasia_epica UNION ALL
    SELECT 'El héroe de las eras', 2008, @sanderson, @fantasia_epica UNION ALL
    SELECT 'Aleación de ley', 2011, @sanderson, @fantasia UNION ALL
    SELECT 'Sombras de identidad', 2015, @sanderson, @fantasia UNION ALL
    SELECT 'Brazales de duelo', 2016, @sanderson, @fantasia UNION ALL
    SELECT 'El metal perdido', 2022, @sanderson, @fantasia UNION ALL
    SELECT 'El camino de los reyes', 2010, @sanderson, @fantasia_epica UNION ALL
    SELECT 'Palabras radiantes', 2014, @sanderson, @fantasia_epica UNION ALL
    SELECT 'Juramentada', 2017, @sanderson, @fantasia_epica UNION ALL
    SELECT 'El ritmo de la guerra', 2020, @sanderson, @fantasia_epica UNION ALL
    SELECT 'Viento y verdad', 2024, @sanderson, @fantasia_epica UNION ALL
    SELECT 'Danzante del filo', 2016, @sanderson, @fantasia UNION ALL
    SELECT 'Esquirla del amanecer', 2020, @sanderson, @fantasia UNION ALL
    -- G.R.R. Martin (7) - Fantasía épica
    SELECT 'Juego de tronos', 1996, @grrm, @fantasia_epica UNION ALL
    SELECT 'Choque de reyes', 1998, @grrm, @fantasia_epica UNION ALL
    SELECT 'Tormenta de espadas', 2000, @grrm, @fantasia_epica UNION ALL
    SELECT 'Festín de cuervos', 2005, @grrm, @fantasia_epica UNION ALL
    SELECT 'Danza de dragones', 2011, @grrm, @fantasia_epica UNION ALL
    SELECT 'Fuego y sangre', 2018, @grrm, @fantasia_epica UNION ALL
    SELECT 'El caballero de los Siete Reinos', 2015, @grrm, @fantasia_epica UNION ALL
    -- Riordan (12) - Fantasía mitológica
    SELECT 'El ladrón del rayo', 2005, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'El mar de los monstruos', 2006, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'La maldición del titán', 2007, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'La batalla del laberinto', 2008, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'El último héroe del Olimpo', 2009, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'El cáliz de los dioses', 2023, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'La ira de la diosa triple', 2024, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'El héroe perdido', 2010, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'El hijo de Neptuno', 2011, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'La marca de Atenea', 2012, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'La casa de Hades', 2013, @riordan, @fantasia_mitologica UNION ALL
    SELECT 'La sangre del Olimpo', 2014, @riordan, @fantasia_mitologica UNION ALL
    -- C.S. Lewis (7) - Fantasía
    SELECT 'El león, la bruja y el armario', 1950, @cs_lewis, @fantasia UNION ALL
    SELECT 'El caballo y el muchacho', 1954, @cs_lewis, @fantasia UNION ALL
    SELECT 'El príncipe Caspian', 1951, @cs_lewis, @fantasia UNION ALL
    SELECT 'La travesía del Viajero del Alba', 1952, @cs_lewis, @fantasia UNION ALL
    SELECT 'La silla de plata', 1953, @cs_lewis, @fantasia UNION ALL
    SELECT 'La última batalla', 1956, @cs_lewis, @fantasia UNION ALL
    SELECT 'El sobrino del mago', 1955, @cs_lewis, @fantasia UNION ALL
    -- Herbert (6) - Ciencia ficción
    SELECT 'Dune', 1965, @herbert, @ciencia_ficcion UNION ALL
    SELECT 'El mesías de Dune', 1969, @herbert, @ciencia_ficcion UNION ALL
    SELECT 'Hijos de Dune', 1976, @herbert, @ciencia_ficcion UNION ALL
    SELECT 'Dios emperador de Dune', 1981, @herbert, @ciencia_ficcion UNION ALL
    SELECT 'Herejes de Dune', 1984, @herbert, @ciencia_ficcion UNION ALL
    SELECT 'Casa Capitular Dune', 1985, @herbert, @ciencia_ficcion UNION ALL
    -- Asimov (7) - Ciencia ficción
    SELECT 'Fundación', 1951, @asimov, @ciencia_ficcion UNION ALL
    SELECT 'Fundación e Imperio', 1952, @asimov, @ciencia_ficcion UNION ALL
    SELECT 'Segunda Fundación', 1953, @asimov, @ciencia_ficcion UNION ALL
    SELECT 'Los límites de la Fundación', 1982, @asimov, @ciencia_ficcion UNION ALL
    SELECT 'Fundación y Tierra', 1986, @asimov, @ciencia_ficcion UNION ALL
    SELECT 'Preludio a la Fundación', 1988, @asimov, @ciencia_ficcion UNION ALL
    SELECT 'Hacia la Fundación', 1993, @asimov, @ciencia_ficcion UNION ALL
    -- Collins (5) - Distopía
    SELECT 'Los juegos del hambre', 2008, @collins, @distopia UNION ALL
    SELECT 'En llamas', 2009, @collins, @distopia UNION ALL
    SELECT 'Sinsajo', 2010, @collins, @distopia UNION ALL
    SELECT 'Balada de pájaros cantores y serpientes', 2020, @collins, @distopia UNION ALL
    SELECT 'Amanecer en la cosecha', 2025, @collins, @distopia UNION ALL
    -- Rothfuss (4) - Fantasía
    SELECT 'El nombre del viento', 2007, @rothfuss, @fantasia UNION ALL
    SELECT 'El temor de un hombre sabio', 2011, @rothfuss, @fantasia UNION ALL
    SELECT 'La música del silencio', 2014, @rothfuss, @fantasia UNION ALL
    SELECT 'El estrecho sendero entre deseos', 2023, @rothfuss, @fantasia UNION ALL
    -- Paolini (5) - Fantasía
    SELECT 'Eragon', 2002, @paolini, @fantasia UNION ALL
    SELECT 'Eldest', 2005, @paolini, @fantasia UNION ALL
    SELECT 'Brisingr', 2008, @paolini, @fantasia UNION ALL
    SELECT 'Legado', 2011, @paolini, @fantasia UNION ALL
    SELECT 'Murtagh', 2023, @paolini, @fantasia UNION ALL
    -- Sapkowski (9) - Fantasía
    SELECT 'El último deseo', 1993, @sapkowski, @fantasia UNION ALL
    SELECT 'La espada del destino', 1992, @sapkowski, @fantasia UNION ALL
    SELECT 'La sangre de los elfos', 1994, @sapkowski, @fantasia UNION ALL
    SELECT 'Tiempo de odio', 1995, @sapkowski, @fantasia UNION ALL
    SELECT 'Bautismo de fuego', 1996, @sapkowski, @fantasia UNION ALL
    SELECT 'La torre de la golondrina', 1997, @sapkowski, @fantasia UNION ALL
    SELECT 'La dama del lago', 1999, @sapkowski, @fantasia UNION ALL
    SELECT 'Estación de tormentas', 2013, @sapkowski, @fantasia UNION ALL
    SELECT 'Temporada de guerra', 2014, @sapkowski, @fantasia UNION ALL
    -- Orwell (2) - Distopía / Sátira política
    SELECT '1984', 1949, @orwell, @distopia UNION ALL
    SELECT 'Rebelión en la granja', 1945, @orwell, @satira_politica UNION ALL
    -- Huxley (1) - Distopía
    SELECT 'Un mundo feliz', 1932, @huxley, @distopia UNION ALL
    -- Bradbury (2) - Distopía / Ciencia ficción
    SELECT 'Fahrenheit 451', 1953, @bradbury, @distopia UNION ALL
    SELECT 'Crónicas marcianas', 1950, @bradbury, @ciencia_ficcion UNION ALL
    -- Weir (2) - Ciencia ficción
    SELECT 'El marciano', 2011, @weir, @ciencia_ficcion UNION ALL
    SELECT 'Proyecto Hail Mary', 2021, @weir, @ciencia_ficcion
) AS t
WHERE NOT EXISTS (SELECT 1 FROM libros l WHERE l.titulo = t.titulo);

-- Verificar resultados
SELECT 'Autores totales' AS metrica, COUNT(*) AS valor FROM autores
UNION ALL
SELECT 'Géneros totales', COUNT(*) FROM generos
UNION ALL
SELECT 'Libros totales', COUNT(*) FROM libros;
