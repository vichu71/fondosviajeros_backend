-- Tabla: fondo
CREATE TABLE fondo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: usuario
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'USER') NOT NULL
);

-- Tabla intermedia: usuario_fondo
CREATE TABLE usuario_fondo (
    usuario_id BIGINT NOT NULL,
    fondo_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, fondo_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fondo_id) REFERENCES fondo(id) ON DELETE CASCADE
);

-- Tabla: movimiento
CREATE TABLE movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concepto VARCHAR(255) NOT NULL,
    cantidad DOUBLE NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    fondo_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fondo_id) REFERENCES fondo(id) ON DELETE CASCADE
);
