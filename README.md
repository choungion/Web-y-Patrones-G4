# Aplicación Web Centro de Arte Taru

## Descripción
Proyecto de desarrollo de una solución tecnológica para el Centro de Artes Taru, ubicado en Nicoya de Guanacaste. El sistema web busca centralizar la información y automatizar los procesos administrativos clave como la matrícula de estudiantes, el control de asistencia, la gestión de pagos y la comunicación con los usuarios.

## Usuarios de prueba

| Rol        | Usuario       | Contraseña        |
|------------|---------------|--------------------|
| ADMIN      | admin         | Admin123.          |
| ESTUDIANTE | maria.lopez   | Estudiante123.     |

## Integrantes
* Diana Herrera Valerio.
* Robert Monge Garro.
* Pablo Joohyung Lee Fuentes.

## Objetivos del Proyecto
* Optimizar la gestión administrativa del centro
* Reducir errores en el manejo de información
* Mejorar la experiencia de estudiantes, padres de familia y la administración
* Facilitar la inscripción en línea y dar visibilidad a la oferta académica

## Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Lenguaje / Runtime | Java 21 |
| Framework | Spring Boot 3.5.14 |
| Vistas | Thymeleaf + Thymeleaf Extras Spring Security 6 |
| Seguridad | Spring Security (login por formulario, roles, rutas dinámicas) |
| Persistencia | Spring Data JPA + MySQL (mysql-connector-j) |
| Almacenamiento de archivos | Firebase Admin SDK / Firebase Storage |
| Correo | Spring Mail (SMTP Gmail) |
| Frontend | HTML, CSS, JavaScript, Bootstrap 5.3.8, Font Awesome 7.2.0, jQuery 4.0.0, Popper.js |
| Build | Maven |
| Contenedor | Docker (build multi-stage con Maven + JRE Alpine) |

---

## Requisitos previos

Antes de instalar el proyecto se necesita tener disponible:

1. **JDK 21** (o superior) instalado y configurado en el `PATH`.
2. **Maven 3.9+** (o usar el wrapper `mvnw` si el proyecto lo incluye).
3. Una **base de datos MySQL** accesible (local o en la nube, por ejemplo Aiven, RDS, etc.).
4. Un **proyecto de Firebase** con un bucket de Storage habilitado y un archivo de credenciales de servicio (JSON) descargado desde la consola de Firebase (*Configuración del proyecto → Cuentas de servicio → Generar nueva clave privada*).
5. Una **cuenta de correo (Gmail)** con una [contraseña de aplicación](https://myaccount.google.com/apppasswords) generada, para el envío automático de cobros y notificaciones.
6. (Opcional) **Docker** si se desea ejecutar la aplicación en un contenedor.

---

## Instalación

1. Clonar el repositorio:
```bash
   git clone https://github.com/choungion/Web-y-Patrones-G4.git
   cd Web-y-Patrones-G4
```

2. Colocar el archivo de credenciales de Firebase (wallet/JSON de servicio) dentro de:

src/main/resources/firebase/

   Este archivo **no debe subirse a un repositorio público** con datos reales; se recomienda agregarlo a `.gitignore` y distribuirlo por un canal seguro entre el equipo.

3. Configurar las propiedades de la aplicación (ver sección *Configuración* a continuación) en:

src/main/resources/application.properties


4. Crear el esquema y los datos iniciales de la base de datos ejecutando el script:

src/main/resources/creaTablas.sql

   Este script crea la base de datos `taru`, el usuario `usuario_taru`, todas las tablas del sistema y los datos de ejemplo (roles, usuarios de prueba, cursos, rutas de seguridad, etc.).

---

## Configuración

El archivo `src/main/resources/application.properties` centraliza toda la configuración del sistema. Los valores sensibles (contraseñas, credenciales) que aparecen en el repositorio corresponden al entorno de desarrollo del equipo; para un despliegue propio se deben reemplazar por credenciales propias y, preferiblemente, cargarlas como variables de entorno en lugar de dejarlas escritas en el archivo.

### Puerto de la aplicación
```properties
server.port=80
```
Puede cambiarse a `8080` u otro puerto libre según el entorno.

### Base de datos MySQL
```properties
spring.datasource.url=jdbc:mysql://<host>:<puerto>/taru
spring.datasource.username=usuario_taru
spring.datasource.password=<contraseña>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```
El usuario y la contraseña deben coincidir con los creados por `creaTablas.sql` (o con los que se definan al adaptar el script a un entorno propio).

### Almacenamiento en Firebase
```properties
firebase.bucket.name=<nombre-del-bucket>
firebase.storage.path=taru
firebase.json.path=firebase
firebase.json.file=<nombre-del-archivo-de-credenciales>.json
```
- `firebase.json.path` y `firebase.json.file` apuntan al archivo de credenciales dentro de `src/main/resources/firebase/`.
- Alternativamente, `StorageConfig` admite la variable de entorno `FIREBASE_CREDENTIALS_JSON` con el contenido del JSON de credenciales completo, útil para no versionar el archivo en despliegues en la nube (por ejemplo, en Docker/Render).

### Envío de correo
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<correo@gmail.com>
spring.mail.password=<contraseña de aplicación>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
Se utiliza para el envío automático de recordatorios/cobros de mensualidad (`CorreoMensualidadService`) y para las notificaciones del módulo de comunicados.

### Idiomas
El sistema soporta español (por defecto) e inglés mediante `messages.properties`, `messages_es.properties` y `messages_en.properties`. El idioma puede cambiarse agregando el parámetro `?lang=en` o `?lang=es` a cualquier URL.

---

## Ejecución

### Opción 1 — Con Render: 
https://web-y-patrones-g4-hnp4.onrender.com

### Opción 2 — NetBeans
Se abre el proyecto en NetBeans, se hace click en clean install y se ejecuta el proyecto.

---

## Descripción de los módulos

### Sitio público
- **Inicio (`/`, `/index`)**: página de bienvenida del centro.
- **Nosotros (`/nosotros/**`)**: historia, misión y visión del centro (con administración de contenido para el rol ADMIN).
- **Servicios/Cursos (`/cursos/servicios`)**: catálogo público de cursos ofrecidos.
- **Galería (`/galeria`)**: imágenes públicas de las instalaciones y actividades del centro.
- **Contacto (`/contacto/**`)**: teléfono, dirección, correo, redes sociales y horario de atención.
- **Inscripción en línea (`/inscripcion/**`)**: formulario público para matricular estudiantes en un curso, con confirmación de la solicitud.

### Módulo de cursos (`/cursos`)
CRUD de cursos: nombre, descripción, edad recomendada, horario, días, cupo, precio mensual e imagen. Controla la oferta académica que se muestra en el sitio público y que está disponible para inscripción.

### Módulo de estudiantes y encargados (`/estudiante`)
Registro y administración de los estudiantes matriculados, su información personal, encargado responsable y curso asignado.

### Módulo de inscripciones (`/inscripcion`)
Gestiona las solicitudes de inscripción realizadas desde el sitio público, su estado (Pendiente/Confirmada) y su vínculo con el curso y el estudiante.

### Módulo de asistencia (`/asistencia`)
Registro de asistencia por curso y fecha (Presente/Ausente, con observaciones). Incluye un historial consultable tanto por administración como por el propio estudiante (rol ESTUDIANTE) para su historial personal.

### Módulo de ausencias (`/ausencia`)
Registro y consulta del historial de ausencias justificadas de los estudiantes.

### Módulo de mensualidades y cobros (`/mensualidad`, `/pago`, `/cobro`)
- **Mensualidades**: generación automática (mediante `MensualidadScheduler`, tarea programada) y manual de los períodos de pago por inscripción, con estados Pendiente, Pagada o Vencida.
- **Pagos**: registro de pagos de mensualidades (monto, método de pago, comprobante/recibo almacenado en Firebase Storage).
- **Cobros**: envío de notificaciones/recordatorios de cobro por correo a los encargados (`CorreoMensualidadService`), con historial de envíos y su estado.

### Módulo de comunicados (`/comunicado`)
Creación y envío de comunicados generales o dirigidos a estudiantes específicos, con listado de notificaciones enviadas.

### Módulo de galería (administración) (`/galeria`)
CRUD de imágenes de la galería pública, con activación/desactivación de cada elemento.

### Módulo de usuarios, roles y rutas (`/usuario`, `/rol`, `/usuario_rol`)
- **Usuarios**: administración de las cuentas de acceso al sistema (credenciales, datos personales, vínculo opcional con un estudiante).
- **Roles**: definición de los roles del sistema (por ejemplo `ADMIN`, `ESTUDIANTE`).
- **Usuario–Rol**: asignación y remoción de roles a cada usuario.
- La tabla `ruta` define qué URLs requieren autenticación/rol y cuáles son públicas, y es leída dinámicamente por `SecurityConfig` para construir las reglas de acceso en tiempo de arranque.

### Seguridad
Autenticación basada en formulario (`/login`) con `Spring Security` y contraseñas cifradas con `BCrypt`. El control de acceso a cada ruta se define en la base de datos (tabla `ruta`) en lugar de estar codificado en Java, lo que permite modificar permisos sin recompilar la aplicación. Los accesos no autorizados se redirigen a `/acceso_denegado`.

---

## Estado del Proyecto
Hemos completado el 100% del desarrollo de los requerimientos solicitados por el cliente

## Para asegurar la integridad del código y la colaboración eficiente en el proyecto del Centro de Artes Taru, el equipo acuerda seguir las siguientes reglas:
* Rama main: Es la rama principal y contiene la versión estable y funcional del sistema. No se permite realizar push directo a esta rama.  
* Ramas de funcionalidad: Cada nueva tarea derivada de las historias de usuario debe desarrollarse en su propia rama, creada siempre desde main.

### Flujo de cambios:
* Antes de trabajar, cada integrante debe asegurar que su rama local esté actualizada con main.
* Al finalizar una funcionalidad, se debe abrir un Pull Request (PR).
* Gestión de conflictos: En caso de conflictos durante el merge, el responsable de la rama de funcionalidad debe resolverlos manteniendo la coherencia con los requerimientos definidos en el proyecto.
