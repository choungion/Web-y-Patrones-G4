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
