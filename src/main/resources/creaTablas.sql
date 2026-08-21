drop database if exists taru;
drop user if exists usuario_taru;

-- Creacion del esquema
CREATE database taru
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creacion de usuario de aplicacion con contrasena segura
create user 'usuario_taru'@'%' identified by 'Usuar1o_Clave.';
grant select, insert, update, delete on taru.* to 'usuario_taru'@'%';
flush privileges;

use taru;

-- Tabla de encargados
create table encargado (
  id_encargado INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  telefono VARCHAR(20),
  correo VARCHAR(100),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_encargado))
  ENGINE = InnoDB;

-- Tabla de cursos
create table curso (
  id_curso INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  edad_recomendada VARCHAR(50),
  horario VARCHAR(100),
  dias VARCHAR(100),
  cupo INT unsigned CHECK (cupo >= 0),
  precio_mensual decimal(12,2) CHECK (precio_mensual >= 0),
  ruta_imagen varchar(1024),
  activo boolean DEFAULT true,
  PRIMARY KEY (id_curso),
  unique (nombre))
  ENGINE = InnoDB;

-- Tabla de estudiantes matriculados
create table estudiante (
  id_estudiante INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL,
  fecha_nacimiento DATE,
  telefono VARCHAR(20),
  correo VARCHAR(100),
  direccion VARCHAR(255),
  observaciones TEXT,
  ruta_foto varchar(1024),
  activo boolean DEFAULT true,
  id_encargado INT,
  id_curso INT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_estudiante),
  index ndx_apellido (apellido),
  foreign key fk_estudiante_encargado (id_encargado) references encargado(id_encargado),
  foreign key fk_estudiante_curso (id_curso) references curso(id_curso))
  ENGINE = InnoDB;

-- Tabla de inscripciones en linea
create table inscripcion (
  id_inscripcion INT NOT NULL AUTO_INCREMENT,
  fecha_inscripcion DATE NOT NULL,
  estado VARCHAR(20) DEFAULT 'Confirmada',
  id_estudiante INT NOT NULL,
  id_curso INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_inscripcion),
  foreign key fk_inscripcion_estudiante (id_estudiante) references estudiante(id_estudiante),
  foreign key fk_inscripcion_curso (id_curso) references curso(id_curso))
  ENGINE = InnoDB;

-- Tabla de asistencia
CREATE TABLE asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    observaciones VARCHAR(255),
    id_estudiante INT NOT NULL,
    id_curso INT NOT NULL,
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    FOREIGN KEY (id_curso) REFERENCES curso(id_curso)
)   ENGINE = InnoDB;

CREATE TABLE ausencia (
    id_ausencia INT NOT NULL AUTO_INCREMENT,
    fecha_ausencia DATE NOT NULL,
    motivo VARCHAR(255),
    fecha_registro DATETIME NOT NULL,
    id_estudiante INT NOT NULL,
    id_curso INT NOT NULL,

    PRIMARY KEY (id_ausencia),

    FOREIGN KEY (id_estudiante)
        REFERENCES estudiante(id_estudiante),

    FOREIGN KEY (id_curso)
        REFERENCES curso(id_curso)
) ENGINE = InnoDB;

-- Tabla de mensualidades
CREATE TABLE mensualidad (
    id_mensualidad INT AUTO_INCREMENT PRIMARY KEY,
    id_inscripcion INT NOT NULL,
    periodo CHAR(7) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    estado ENUM('Pendiente','Pagada','Vencida')
        DEFAULT 'Pendiente',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mensualidad_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES inscripcion(id_inscripcion),

    CONSTRAINT uk_mensualidad_periodo
        UNIQUE (id_inscripcion, periodo)
) ENGINE=InnoDB;

-- Tabla de pago de mensualidades
CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_mensualidad INT NOT NULL,
    fecha_pago DATE NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(30),
    observaciones VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ruta_recibo varchar(1024),

    CONSTRAINT fk_pago_mensualidad
        FOREIGN KEY (id_mensualidad)
        REFERENCES mensualidad(id_mensualidad)
) ENGINE=InnoDB;

CREATE TABLE cobro (
    id_cobro INT AUTO_INCREMENT PRIMARY KEY,
    id_mensualidad INT NOT NULL,
    destinatario VARCHAR(100) NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL,

    CONSTRAINT fk_cobro_mensualidad
        FOREIGN KEY (id_mensualidad)
        REFERENCES mensualidad(id_mensualidad)
) ENGINE=InnoDB;

insert into encargado (nombre, telefono, correo) values
('Laura Ramirez', '8123-4567', 'laura.ramirez@correo.com'),
('Andrea Cruz', '8234-5678', 'andrea.cruz@correo.com');

insert into curso (nombre, descripcion, edad_recomendada, horario, dias, cupo, precio_mensual, activo) values
('Danza Contemporanea', 'Explora el movimiento libre, la creatividad y la expresion corporal a traves de la danza contemporanea.', '12 a 18 anios', '4:00 p.m. - 6:00 p.m.', 'Martes y Jueves', 5, 45000, true),
('Gimnasia Artistica', 'Mejora la fuerza, flexibilidad y coordinacion con entrenamientos dinamicos y seguros.', '6 a 15 anios', '5:00 p.m. - 6:30 p.m.', 'Lunes y Miercoles', 8, 40000, true),
('Ballet Infantil', 'Desarrolla tecnica, postura, coordinacion y expresion artistica a traves del ballet clasico.', '4 a 8 anios', '4:00 p.m. - 5:30 p.m.', 'Martes y Jueves', 8, 35000, true),
('Macro Gimnasia', 'Rutinas grupales orientadas a la resistencia y coordinacion.', '8 a 14 anios', '5:00 p.m. - 6:00 p.m.', 'Sabados', 7, 40000, true);

insert into estudiante (nombre, apellido, fecha_nacimiento, telefono, correo, direccion, id_encargado, id_curso, activo) values
('Maria', 'Lopez', '2010-04-12', '6123-4567', 'maria@correo.com', 'Barrio San Andres, San Jose', 1, 3, true),
('Sofia', 'Ramirez', '2014-02-20', '6234-5678', 'sofia@correo.com', 'Nicoya, Guanacaste', 1, 2, true),
('Daniel', 'Vargas', '2012-08-05', '6345-6789', 'daniel@correo.com', 'Nicoya, Guanacaste', 2, 4, true);

insert into inscripcion (fecha_inscripcion, estado, id_estudiante, id_curso) values
(CURDATE(), 'Confirmada', 1, 3),
(CURDATE(), 'Confirmada', 2, 2);

INSERT INTO asistencia
(fecha, estado, observaciones, id_estudiante, id_curso)
VALUES
('2026-08-04', 'Presente', 'Participó en todas las actividades.', 1, 3),
('2026-08-06', 'Presente', NULL, 1, 3),
('2026-08-11', 'Ausente', 'Ausencia justificada por el encargado.', 1, 3),
('2026-08-13', 'Presente', NULL, 1, 3),

('2026-08-03', 'Presente', NULL, 2, 2),
('2026-08-05', 'Ausente', 'No se presentó a la clase.', 2, 2),
('2026-08-10', 'Presente', 'Participó activamente.', 2, 2),

('2026-08-08', 'Presente', NULL, 3, 4),
('2026-08-15', 'Ausente', 'Ausencia sin observaciones.', 3, 4);

INSERT INTO mensualidad
(id_inscripcion, periodo, fecha_vencimiento, monto, estado)
VALUES
(1, '2026-06', '2026-06-10', 35000, 'Pagada'),
(1, '2026-07', '2026-07-10', 35000, 'Pagada'),
(1, '2026-08', '2026-08-10', 35000, 'Pagada'),
(1, '2026-09', '2026-09-10', 35000, 'Pendiente');

-- Inscripción 2 = Sofía Ramírez / Gimnasia Artística / ₡40.000
INSERT INTO mensualidad
(id_inscripcion, periodo, fecha_vencimiento, monto, estado)
VALUES
(2, '2026-06', '2026-06-10', 40000, 'Vencida'),
(2, '2026-07', '2026-07-10', 40000, 'Vencida'),
(2, '2026-08', '2026-08-10', 40000, 'Pendiente');


INSERT INTO pago
(id_mensualidad, fecha_pago, monto, metodo_pago, observaciones, ruta_recibo)
VALUES
(1, '2026-06-05', 35000, 'SINPE Móvil',
 'Pago realizado por el encargado.',
 'recibos/2026/06/maria_junio.jpg');

-- Pago de julio de María
INSERT INTO pago
(id_mensualidad, fecha_pago, monto, metodo_pago, observaciones, ruta_recibo)
VALUES
(2, '2026-07-07', 35000, 'Transferencia',
 'Pago completo de la mensualidad.',
 'recibos/2026/07/maria_julio.jpg');

-- Pago de agosto de María
INSERT INTO pago
(id_mensualidad, fecha_pago, monto, metodo_pago, observaciones, ruta_recibo)
VALUES
(3, '2026-08-08', 35000, 'Efectivo',
 'Pago realizado en el centro de danza.',
 'recibos/2026/08/maria_agosto.jpg');





CREATE TABLE asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    observaciones VARCHAR(255),
    id_estudiante INT NOT NULL,
    id_curso INT NOT NULL,
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    FOREIGN KEY (id_curso) REFERENCES curso(id_curso)
)   ENGINE = InnoDB;

-- Tabla de usuarios
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(512) NOT NULL,
  nombre VARCHAR(50) NOT NULL,
  apellidos VARCHAR(50) NOT NULL,
  correo VARCHAR(100) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN DEFAULT TRUE,
  id_estudiante INT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  index ndx_username (username),
  FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante))
  ENGINE = InnoDB;

-- Tabla de roles
CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol VARCHAR(20) UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol))
  ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  FOREIGN KEY fk_usuarioRol_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_usuarioRol_rol (id_rol) REFERENCES rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de rutas:
CREATE TABLE ruta (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT NULL,
    requiere_rol BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (id_rol IS NOT NULL OR requiere_rol = FALSE),
    PRIMARY KEY (id_ruta),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol))
    ENGINE = InnoDB;

CREATE TABLE galeria (
  id_galeria INT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255),
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_galeria))
  ENGINE = InnoDB;

CREATE TABLE nosotros (
  id_nosotros INT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(100) NOT NULL,
  parrafo1 TEXT,
  parrafo2 TEXT,
  mision TEXT,
  vision TEXT,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_nosotros))
  ENGINE = InnoDB;

CREATE TABLE contacto (
  id_contacto INT NOT NULL AUTO_INCREMENT,
  telefono VARCHAR(25) NOT NULL,
  direccion VARCHAR(255) NOT NULL,
  correo VARCHAR(100) NOT NULL,
  whatsapp VARCHAR(25),
  facebook VARCHAR(255),
  instagram VARCHAR(255),
  horario VARCHAR(100),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_contacto))
  ENGINE = InnoDB;

-- Roles del sistema
INSERT INTO rol (rol) VALUES ('ADMIN'), ('ESTUDIANTE');

-- Usuario administrador de ejemplo contraseña: Admin123., usuario: admin
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, ruta_imagen, activo) VALUES
('admin', '$2b$10$cI1eOc12wpqBcyCD5P/LmOmsu/i/IArmODbNgHm1bFz84B3bsoqO.', 'Jennifer', 'Ramos Cruz', 'admin@tarucentrodearte.com', '8364-6179', 'https://ui-avatars.com/api/?name=Admin+Taru&background=1f4d3a&color=fff', true);

-- Usuario estudiante de ejemplo contraseña: Estudiante123., usuario: maria.lopez
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, ruta_imagen, activo, id_estudiante) VALUES
('maria.lopez', '$2b$10$rO7gWGX9NpKK/xIvR26MUuhLNLDSfgw9vkNhgxTwPJcaVk7DDYRZC', 'Maria', 'Lopez', 'maria@correo.com', '6123-4567', 'https://ui-avatars.com/api/?name=Maria+Lopez&background=C58B12&color=fff', true, 1);

-- Asignación de roles a los usuarios de ejemplo
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1, 1), -- admin ADMIN
(2, 2); -- maria.lopez ESTUDIANTE

-- Rutas que requieren el rol ADMIN
INSERT INTO ruta (ruta, id_rol) VALUES
('/cursos/listado', 1),
('/cursos/guardar', 1),
('/cursos/modifica/**', 1),
('/cursos/eliminar', 1),
('/ausencia/listado', 1),
('/estudiante/**', 1),
('/encargado/**', 1),
('/asistencia/listado', 1),
('/asistencia/nuevo', 1),
('/asistencia/guardar', 1),
('/asistencia/modificar/**', 1),
('/asistencia/eliminar', 1),
('/inscripcion/listado', 1),
('/galeria/listado', 1),
('/galeria/guardar', 1),
('/galeria/modifica/**', 1),
('/galeria/eliminar', 1),
('/nosotros/listado', 1),
('/nosotros/guardar', 1),
('/nosotros/modifica/**', 1),
('/nosotros/eliminar', 1),
('/contacto/listado', 1),
('/contacto/guardar', 1),
('/contacto/modificar/**', 1),
('/contacto/eliminar', 1),
('/usuario/**', 1),
('/usuario_rol/**', 1),
('/rol/**', 1);

-- Rutas que requieren el rol ESTUDIANTE
INSERT INTO ruta (ruta, id_rol) VALUES
('/asistencia/historial/**', 2);

-- Rutas públicas
INSERT INTO ruta (ruta, requiere_rol) VALUES
('/', false),
('/index', false),
('/login', false),
('/logout', false),
('/acceso_denegado', false),
('/cursos/servicios', false),
('/inscripcion/**', false),
('/galeria', false),
('/js/**', false),
('/css/**', false),
('/webjars/**', false),
('/fav/**', false);

INSERT INTO galeria (titulo, descripcion, ruta_imagen, activo) VALUES
('Salón de Danza Contemporánea', 'Nuestro salón principal equipado con piso especializado para danza.', 'https://images.unsplash.com/photo-1518611012118-696072aa579a?w=800', true),
('Clase de Gimnasia Artística', 'Estudiantes practicando rutinas de gimnasia con equipo profesional.', 'https://images.unsplash.com/photo-1518310383802-640c2de311b6?w=800', true),
('Ballet Infantil', 'Nuestras estudiantes más pequeñas durante una clase de ballet.', 'https://images.unsplash.com/photo-1544928147-79a2dbc1f389?w=800', true),
('Presentación Anual', 'Estudiantes durante la muestra artística de fin de año.', 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=800', true);

INSERT INTO nosotros (titulo, parrafo1, parrafo2, mision, vision, ruta_imagen, activo) VALUES
('Nosotros',
 'Hola, soy Jennifer Ramos, fundadora de TARU Centro de Artes. Soy bachiller en Danza por la Universidad Nacional de Costa Rica y cuento con experiencia como integrante de la Compañía Nacional de Danza. Mi pasión por el movimiento, la expresión artística y la enseñanza me motivó a crear un espacio donde personas de todas las edades puedan desarrollar sus habilidades, fortalecer su confianza y disfrutar del arte.',
 'TARU Centro de Artes nació en 2025 en Nicoya con el propósito de ampliar la oferta cultural de la región, ofreciendo disciplinas innovadoras y accesibles para la comunidad. Brindamos clases de danza contemporánea, gimnasia y macrogimnasia en un ambiente inclusivo que promueve el desarrollo físico, artístico y personal de cada estudiante.',
 'Brindar un espacio artístico y formativo donde niños, jóvenes y adultos desarrollen sus habilidades mediante la danza contemporánea, la gimnasia y la macrogimnasia, promoviendo la creatividad, el bienestar físico, la disciplina y la confianza en un ambiente inclusivo y de respeto.',
 'Ser un centro de artes reconocido en Guanacaste por inspirar el desarrollo integral de las personas a través del movimiento y la expresión artística, fortaleciendo la cultura y convirtiéndose en un referente de innovación, calidad y compromiso con la comunidad.',
 '/js/images/nosotros.png',
 true);

INSERT INTO contacto (telefono, direccion, correo, whatsapp, facebook, instagram, horario, activo) VALUES
('+506 8364 6179', 'Polideportivo de Nicoya, Nicoya, Costa Rica, 50201', 'tarucentrodearte@gmail.com', '+506 8364 6179',
 'https://www.facebook.com/share/1DSsFHpb2C/', 'https://www.instagram.com/tarucentrodearte?igsh=cjI2aDN3eXhtYmZm',
 'Lunes a sábado, 2:00 p.m. - 7:00 p.m.', true);
