-- ============================================================
-- NEXOLAB - Schema de Base de Datos
-- Sistema de Gestión de Análisis Clínicos Veterinarios
-- ============================================================

CREATE DATABASE IF NOT EXISTS nexolab CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nexolab;

-- ------------------------------------------------------------
-- Tabla: clinicas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clinicas (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    direccion  VARCHAR(255),
    telefono   VARCHAR(30),
    email      VARCHAR(100),
    activa     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ------------------------------------------------------------
-- Tabla: laboratorios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS laboratorios (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(150) NOT NULL,
    direccion VARCHAR(255),
    telefono  VARCHAR(30),
    email     VARCHAR(100),
    activo    BOOLEAN NOT NULL DEFAULT TRUE
);

-- ------------------------------------------------------------
-- Tabla: usuarios
-- Rol: ADMIN | VETERINARIO | LABORATORIO
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(60)  NOT NULL UNIQUE,
    password_hash   VARCHAR(64)  NOT NULL,  -- SHA-256 hex
    nombre_completo VARCHAR(150) NOT NULL,
    rol             ENUM('ADMIN','VETERINARIO','LABORATORIO') NOT NULL,
    id_clinica      INT NULL,
    id_laboratorio  INT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_clinica     FOREIGN KEY (id_clinica)     REFERENCES clinicas(id),
    CONSTRAINT fk_usuario_laboratorio FOREIGN KEY (id_laboratorio) REFERENCES laboratorios(id)
);

-- ------------------------------------------------------------
-- Tabla: duenos
-- Propietarios de las mascotas, asociados a una clínica
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS duenos (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    telefono   VARCHAR(30),
    email      VARCHAR(100),
    id_clinica INT NOT NULL,
    CONSTRAINT fk_dueno_clinica FOREIGN KEY (id_clinica) REFERENCES clinicas(id)
);

-- ------------------------------------------------------------
-- Tabla: pacientes
-- Mascotas; cada una pertenece a un dueño y a una clínica
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pacientes (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    especie          VARCHAR(60)  NOT NULL,
    raza             VARCHAR(100),
    fecha_nacimiento DATE,
    id_dueno         INT NOT NULL,
    id_clinica       INT NOT NULL,
    CONSTRAINT fk_paciente_dueno   FOREIGN KEY (id_dueno)   REFERENCES duenos(id),
    CONSTRAINT fk_paciente_clinica FOREIGN KEY (id_clinica) REFERENCES clinicas(id)
);

-- ------------------------------------------------------------
-- Tabla: tipos_analisis
-- Catálogo de estudios ofrecidos por cada laboratorio
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tipos_analisis (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    id_laboratorio  INT NOT NULL,
    CONSTRAINT fk_tipo_laboratorio FOREIGN KEY (id_laboratorio) REFERENCES laboratorios(id)
);

-- ------------------------------------------------------------
-- Tabla: ordenes_analisis
-- Estado: PENDIENTE | RECIBIDA | EN_PROCESO | FINALIZADA | CANCELADA
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ordenes_analisis (
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    fecha_creacion         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_cambio    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    estado                 ENUM('PENDIENTE','RECIBIDA','EN_PROCESO','FINALIZADA','CANCELADA')
                           NOT NULL DEFAULT 'PENDIENTE',
    observaciones          TEXT,
    id_paciente      INT NOT NULL,
    id_clinica       INT NOT NULL,
    id_laboratorio   INT NOT NULL,
    id_veterinario   INT NOT NULL,
    CONSTRAINT fk_orden_paciente    FOREIGN KEY (id_paciente)    REFERENCES pacientes(id),
    CONSTRAINT fk_orden_clinica     FOREIGN KEY (id_clinica)     REFERENCES clinicas(id),
    CONSTRAINT fk_orden_laboratorio FOREIGN KEY (id_laboratorio) REFERENCES laboratorios(id),
    CONSTRAINT fk_orden_veterinario FOREIGN KEY (id_veterinario) REFERENCES usuarios(id)
);

-- ------------------------------------------------------------
-- Tabla: orden_items
-- Detalle de qué tipos de análisis incluye cada orden
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orden_items (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    id_orden         INT NOT NULL,
    id_tipo_analisis INT NOT NULL,
    CONSTRAINT fk_item_orden  FOREIGN KEY (id_orden)         REFERENCES ordenes_analisis(id),
    CONSTRAINT fk_item_tipo   FOREIGN KEY (id_tipo_analisis) REFERENCES tipos_analisis(id)
);

-- ------------------------------------------------------------
-- Tabla: resultados
-- Resultado final cargado por el laboratorio
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS resultados (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    id_orden      INT          NOT NULL UNIQUE,
    fecha_carga   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    ruta_archivo  VARCHAR(500),   -- path al PDF en el filesystem
    id_tecnico    INT          NOT NULL,
    CONSTRAINT fk_resultado_orden   FOREIGN KEY (id_orden)   REFERENCES ordenes_analisis(id),
    CONSTRAINT fk_resultado_tecnico FOREIGN KEY (id_tecnico) REFERENCES usuarios(id)
);

-- ============================================================
-- DATOS SEMILLA (para pruebas del MVP)
-- ============================================================

-- Clínicas
INSERT INTO clinicas (nombre, direccion, telefono, email) VALUES
('Clínica Veterinaria Huellitas', 'Av. Corrientes 1234, CABA', '011-4321-0000', 'huellitas@vet.com'),
('Centro Médico Animal PetCare',  'Calle 9 de Julio 500, Rosario', '0341-555-1111', 'petcare@vet.com');

-- Laboratorios
INSERT INTO laboratorios (nombre, direccion, telefono, email) VALUES
('BioVet Laboratorio Clínico', 'Av. Santa Fe 789, CABA', '011-4500-2222', 'biovet@lab.com');

-- Usuarios (password: 'admin123' -> SHA-256)
-- SHA-256 de 'admin123'  = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- SHA-256 de 'vet123'    = b777a42e70dcc5d3c742baeebe66d14b7c24e0f17e1f52b5ccdbd6c70cb3ce25
-- SHA-256 de 'lab123'    = 66de68a02aaf4d69c54cfdb43ea7ebe7b1e20becc23e01d8e56c2db741b8e3c4

INSERT INTO usuarios (username, password_hash, nombre_completo, rol, id_clinica, id_laboratorio) VALUES
('admin',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador NexoLab', 'ADMIN', NULL, NULL),
('dra.garcia', 'b777a42e70dcc5d3c742baeebe66d14b7c24e0f17e1f52b5ccdbd6c70cb3ce25', 'Dra. Laura García', 'VETERINARIO', 1, NULL),
('dr.perez',   'b777a42e70dcc5d3c742baeebe66d14b7c24e0f17e1f52b5ccdbd6c70cb3ce25', 'Dr. Marcos Pérez',  'VETERINARIO', 2, NULL),
('tec.biovet', '66de68a02aaf4d69c54cfdb43ea7ebe7b1e20becc23e01d8e56c2db741b8e3c4', 'Lic. Ana Torres',   'LABORATORIO', NULL, 1);

-- Dueños y pacientes de ejemplo
INSERT INTO duenos (nombre, apellido, telefono, email, id_clinica) VALUES
('Carlos', 'Rodríguez', '011-1234-5678', 'carlos.r@mail.com', 1),
('Sofía',  'Méndez',    '011-8765-4321', 'sofia.m@mail.com',  1);

INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, id_dueno, id_clinica) VALUES
('Milo',   'Canino',  'Labrador Retriever', '2020-03-15', 1, 1),
('Whiskas','Felino',  'Siamés',             '2019-07-22', 2, 1);

-- Catálogo de análisis del laboratorio
INSERT INTO tipos_analisis (nombre, descripcion, id_laboratorio) VALUES
('Hemograma Completo',        'Recuento de células sanguíneas, hemoglobina y hematocrito.', 1),
('Perfil Bioquímico Básico',  'Glucosa, urea, creatinina, ALT, AST.', 1),
('Urianalisis Completo',      'Examen físico, químico y microscópico de orina.', 1),
('Coproparasitológico',       'Detección de parásitos intestinales en materia fecal.', 1),
('Perfil Tiroideo (T3/T4)',   'Evaluación de la función tiroidea.', 1);
