# Control de equipos de laboratorio

Proyecto JavaFX con Hibernate/JPA y PostgreSQL. La organización sigue el ejemplo de Libro: una clase de inicio, una aplicación JavaFX, entidades, DAO, `JPAUtil`, controlador y FXML.

## Requisitos

- JDK 21 (en IntelliJ: Project SDK y Maven Runner con JDK 21).
- PostgreSQL en `localhost:5432`, usuario `postgres`.
- Base `bd_universidad`.

## Base de datos

Si todavía no existe la base, ejecutar `sql/01_crear_base.sql` conectado a `postgres`. Después, en pgAdmin abrir Query Tool **dentro de `bd_universidad`** y ejecutar `sql/02_esquema_y_datos.sql` una sola vez. Ese script crea el esquema `laboratorio`, dos tablas, tres tipos y ocho equipos. Si ya lo ejecutaste, no lo repitas porque los datos tienen códigos únicos.

En `psql`:

```text
psql -U postgres -d postgres -f sql/01_crear_base.sql
psql -U postgres -d bd_universidad -f sql/02_esquema_y_datos.sql
```

## Ejecutar en IntelliJ

Abre esta carpeta como proyecto Maven y pulsa Reload All Maven Projects. Ejecuta `ni.edu.uam.proyectolaboratorio.Launcher` o la tarea Maven `javafx:run`. Al arrancar se solicita la contraseña de PostgreSQL en un `PasswordField`. También puedes definir la variable de entorno `DB_PASSWORD` en la configuración de ejecución. No se guarda la contraseña en el proyecto.

Si Maven desde la terminal dice que falta `JAVA_HOME`, configúralo con la ruta de tu JDK 21. En este equipo está instalado en `C:\Users\luisv\.jdks\temurin-21.0.12.1`.

## Uso

Selecciona un equipo en la tabla para cargarlo al formulario. Guardar crea uno nuevo; Actualizar y Eliminar piden confirmación. Limpiar deja el formulario listo para otro equipo. Buscar permite combinar parte del nombre con un tipo; `Todos` quita el filtro por tipo. El botón **Probar rollback** intenta insertar un equipo temporal y luego otro con código repetido en la misma transacción: PostgreSQL rechaza el segundo y el primero tampoco queda guardado.

Para comprobar el rollback en pgAdmin:

```sql
SELECT codigo FROM laboratorio.equipo WHERE codigo LIKE 'PRUEBA-%';
```

Debe devolver cero filas después de pulsar el botón. Consulta la matriz en `PRUEBAS_P1_P8.md` y las explicaciones para la sustentación en `GUIA_SUSTENTACION.md`.
