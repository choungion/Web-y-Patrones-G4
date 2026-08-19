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
  estado VARCHAR(20) DEFAULT 'Pendiente',
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

-- Tabla de mensualidades
CREATE TABLE taru.mensualidad (
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
CREATE TABLE taru.pago (
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
(CURDATE(), 'Pendiente', 2, 2);

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





