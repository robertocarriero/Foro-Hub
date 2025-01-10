# Foro-Hub

Este proyecto es un foro desarrollado con Spring Boot 3 y Java, que permite a los usuarios:

- Registrarse e iniciar sesión (con JWT).
- Crear, listar, actualizar y eliminar tópicos y respuestas.
- Manejar roles para ADMIN y USER, con diferentes niveles de permisos.
- Validar la información de entrada y devolver mensajes claros en caso de errores.

## Tabla de Contenidos
1. [Características Principales](#1-características-principales)
2. [Requisitos](#2-requisitos)
3. [Estructura de Carpetas](#3-estructura-de-carpetas)
4. [Diagrama de Arquitectura](#4-diagrama-de-arquitectura)
5. [Configuración de la Base de Datos](#5-configuración-de-la-base-de-datos)
6. [Ejecución del Proyecto](#6-ejecución-del-proyecto)
7. [Endpoints Principales](#7-endpoints-principales)
8. [Validaciones y Manejo de Excepciones](#8-validaciones-y-manejo-de-excepciones)
9. [Rol de ADMIN vs USER](#9-rol-de-admin-vs-user)


------------------------------------------------------------------------------------------------------

## 1. Características Principales
- **Registro de usuarios** con validaciones (contraseña encriptada).
- **Autenticación** mediante JWT (token Bearer).
- **Protección de rutas**: solo usuarios autenticados pueden crear/editar/borrar tópicos y respuestas.
- **Rol de ADMIN** con endpoints especiales para administrar usuarios (listar, cambiar roles, eliminar).
- **Uso de Spring Data JPA con MySQL**.
- **Migraciones con Flyway** (archivos en `db/migration`).
- **Validaciones personalizadas** (clases en el paquete `validation`)

-------------------------------------------------------------------------------------------------------

## 2. Requisitos
- **Java 17 o superior**.
- **Maven 3.x** o **Gradle** (aquí se usa Maven por el archivo `pom.xml`).
- **MySQL instalado** o acceso a un servidor MySQL.
- **Un IDE como IntelliJ IDEA** o VSCode con extensiones de Java.

-------------------------------------------------------------------------------------------------------

## 3. Estructura de Carpetas

```plaintext
Foro_Hub
└── api
    ├── src
    │   └── main
    │       └── java
    │           └── Foro.Hub.api
    │               ├── config
    │               │   ├── JwtAuthFilter.java
    │               │   └── SecurityConfig.java
    │               ├── controller
    │               │   ├── AdminController.java
    │               │   ├── AuthController.java
    │               │   ├── RespuestaController.java
    │               │   ├── TopicoController.java
    │               │   └── UsuarioController.java
    │               ├── domain
    │               │   ├── respuesta
    │               │   │   ├── Respuesta.java
    │               │   │   ├── RespuestaDTO.java
    │               │   │   └── RespuestaRepository.java
    │               │   ├── topico
    │               │   │   ├── StatusTopico.java
    │               │   │   ├── Topico.java
    │               │   │   ├── TopicoDTO.java
    │               │   │   └── TopicoRepository.java
    │               │   └── usuario
    │               │       ├── Usuario.java
    │               │       └── UsuarioRepository.java
    │               ├── exception
    │               │   └── GlobalExceptionHandler.java
    │               ├── service
    │               │       ├── JwtService.java
    │               │       ├── UserDetailsServiceImpl.java
    │               │       └── UsuarioService.java
    │               ├── validation
    │               │   ├── RespuestaValidation.java
    │               │   ├── TopicoValidation.java
    │               │   └── UsuarioValidation.java
    │               └── ApiApplication.java
    ├── resources
    │   ├── db
    │   │   └── migration
    │   │       ├── V1_create_table_usuario.sql
    │   │       ├── V2_create_table_topico.sql
    │   │       └── V3_create_table_respuesta.sql
    │   └── application.properties
    ├── test
    └── pom.xml

```

### Configuración de Estructura
- **config**: Clases de configuración de Spring Security, JWT Filter, etc.
- **controller**: Controladores REST que exponen los endpoints.
- **domain**: Entidades JPA y repositorios.
- **validation**: Clases para validar la entrada de datos (tópicos, usuarios, respuestas).
- **exception**: Manejador global de excepciones (`GlobalExceptionHandler`).
- **infra/service**: Servicios de lógica de negocio (`JwtService`, `UsuarioService`, etc.).
- **resources/db/migration**: Scripts SQL de migración con Flyway.
- **application.properties**: Configuraciones (puerto, datasource, logging, etc.).

-------------------------------------------------------------------------------------------------------

## 4. Diagrama de Arquitectura
Un diagrama sencillo de cómo se relacionan los componentes:

```mermaid
flowchart TB
    A[Cliente/Front-end/Postman] --> |HTTP requests| B[Controller Layer]
    B --> |Valida/Requiere Token| C[SecurityConfig + JwtAuthFilter]
    B --> |Llama servicios| D[Service Layer]
    D --> |Persistencia de datos| E[JPA Repository]
    E --> |Interacción con| F((MySQL DB))

   ```

### Flujo de Interacción del Proyecto

- El cliente (Postman, front-end) llama a los endpoints en los **Controllers**.
- `SecurityConfig` + `JwtAuthFilter` protege rutas y comprueba el token.
- Los **Controllers** delegan la lógica a los **Services** (`JwtService`, `UsuarioService`, etc.).
- Los **Services** usan los **Repositories** para comunicarse con la base de datos MySQL.

-------------------------------------------------------------------------------------------------------

## 5. Configuración de la Base de Datos

En el archivo `application.properties`, se configura:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foro_hub?createDatabaseIfNotExist=true
spring.datasource.username=
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
server.port=8081
jwt.secret=EstaEsUnaClaveSuperSeguraParaJWT123456789
logging.level.org.springframework.security=DEBUG

```

**spring.datasource.url**: Cambia localhost:3306 y foro_hub según tu ambiente.
**flyway**: ejecutará los scripts V1_create_table_usuario.sql, V2_create_table_topico.sql, etc.
**jwt.secret**: tu secreto para firmar tokens JWT
`Asegúrate de tener un usuario en MySQL con permisos para crear y modificar la base.`

-------------------------------------------------------------------------------------------------------

## 6. Ejecución del Proyecto
- 1 **Clonar el repositorio o copiar los archivos del proyecto.**
- 2 **Ajustar en application.properties tu usuario y contraseña de MySQL.**
- 3 **Desde la raíz del proyecto, compilar con Maven:**

Copiar código:
> mvn clean install

- 4 **Iniciar la Aplicación:**

Copiar código:
> mvn spring-boot:run

- 5 **La app correrá en http://localhost:8081.**
- *Si todo está bien, verás en la consola mensajes de arranque de Spring Boot, Flyway creando o validando las tablas, y la app escuchando en el puerto 8081.*

-------------------------------------------------------------------------------------------------------

## 7. Endpoints Principales (Probar en Insomnia o Postman)

7.1 **Usuarios**
**POST /usuarios/registrar**
*Registra un nuevo usuario (por defecto, rol = "USER").*
Body (JSON):
json
Copiar código
{
"nombre": "Pepito Perez",
"email": "pepito@gmail.com",
"contraseña": "12345678"
}
Respuestas:
*200 OK si se registra con éxito, devolviendo el usuario creado.*
*400 Bad Request si hay validaciones incumplidas (p. ej., email ya existe).*

**GET /usuarios**
*Lista todos los usuarios.*

7.2 **Autenticación (Auth)**
**POST /auth/login**
Recibe:
json
Copiar código
{
"username": "pepito@gmail.com",
"password": "12345678"
}
*Devuelve un token JWT en texto plano (String) si la autenticación es exitosa.*
*Usa este token en los Headers: Authorization: Bearer <token> para acceder a endpoints protegidos.*

7.3 **Tópicos**
**POST /topicos**
Crea un tópico. Requiere token JWT.
El “autor” se determina automáticamente por el usuario logueado.
Ejemplo Body:
json
Copiar código
{
"titulo": "Pregunta sobre Spring Boot",
"mensaje": "¿Qué es y para qué sirve?",
"fechaCreacion": "2025-01-07T17:25:00",
"status": "NO_RESPONDIDO"
}**
**GET/topicos**
Lista los tópicos existentes. Requiere token.
PUT /topicos/{id}
Actualiza un tópico (solo si el autor coincide con el usuario logueado).
DELETE /topicos/{id}
Elimina un tópico (solo si el autor coincide).

7.4 **Respuestas**
**POST /respuestas**
*Crea una nueva respuesta a un tópico. Requiere token.*
Body:
json
Copiar código
{
"topicoId": 1,
"mensaje": "Creo que se usa para desarrollar microservicios.",
"fechaCreacion": "2025-01-08T10:00:00"
}
**GET /respuestas**
Lista todas las respuestas. Requiere token (o no, según tu config).
**PUT /respuestas/{id}**
**DELETE /respuestas/{id}**
Solo si el usuario logueado es el autor de la respuesta.

7.5 **Admin**
**GET /admin/usuarios**
Lista todos los usuarios. Solo accesible si rol = "ADMIN".
**PATCH /admin/usuarios/{id}/rol?nuevoRol=ADMIN**
Cambia el rol de un usuario.
*Ej: /admin/usuarios/5/rol?nuevoRol=ADMIN.*
Requiere token de un usuario que ya sea ADMIN.
**DELETE /admin/usuarios/{id}**
Elimina un usuario. Solo si quien hace la petición es ADMIN.

------------------------------------------------------------------------------------------------------

## 8. Validaciones y Manejo de Excepciones

*El proyecto cuenta con un GlobalExceptionHandler (GlobalExceptionHandler.java) que atrapa, por ejemplo,* *IllegalArgumentException y devuelve un 400 Bad Request con el mensaje de error.*

*Además, existen clases como UsuarioValidation, TopicoValidation, y RespuestaValidation que verifican* *campos obligatorios (título, mensaje, email, etc.) y lanzan excepciones personalizadas para mantener los* *datos correctos.*

------------------------------------------------------------------------------------------------------

## 9. Rol de ADMIN vs USER

*Por defecto, todo usuario nuevo se registra con rol = "USER".*
*Para que alguien sea ADMIN, se puede:*
*Cambiar manualmente en la base de datos el rol a "ADMIN"*.*
*Tener un primer usuario “ADMIN” que modifique el rol de otros usuarios vía /admin/usuarios/{id}/rol.*
*En el método loadUserByUsername de UserDetailsServiceImpl, al llamar .roles(usuario.getRol()), Spring* *Security convierte "ADMIN" en "ROLE_ADMIN", y "USER" en "ROLE_USER".*

-------------------------------------------------------------------------------------------------------

## 10. Configuración del archivo `application.properties`

### Antes de ejecutar el proyecto, asegúrate de configurar el archivo `application.properties` en el directorio `src/main/resources`.

Puedes usar el archivo `application.properties.example` como base. Asegúrate de reemplazar los placeholders con los valores correctos para tu entorno:

- `spring.datasource.url`: URL de tu base de datos.
- `spring.datasource.username`: Usuario de la base de datos.
- `spring.datasource.password`: Contraseña de la base de datos.
- `jwt.secret`: Clave secreta para firmar los tokens JWT.

---

## Autor

Este proyecto fue desarrollado por ***Roberto Carriero*** como Trabajo Práctico final en desarrollo backend con **Java** y **Spring Boot**, en el programa **ONE** *Oracle Next education* y **ALURA LATAM**

Mis objetivos con este proyecto incluyen:
- Aprender y aplicar buenas prácticas en el desarrollo de APIs REST.
- Mejorar mis conocimientos en validación, autenticación y autorización.
- Dominar el uso de JWT y roles de usuario en un entorno seguro.
- Explorar las herramientas de persistencia con Spring Data JPA y MySQL.

---

## Contacto


- **Correo Electrónico:**  *roberto_carriero1@yahoo.com.ar*
- **GitHub:** *https://github.com/robertocarriero*
- **Linkedin:** *https://www.linkedin.com/in/roberto-carriero/*
---

## Licencia

Este proyecto se distribuye bajo la licencia **MIT**. Puedes usar, modificar y distribuir el código siempre que se mantenga el reconocimiento al autor.

---

¡Gracias por revisar este proyecto!







