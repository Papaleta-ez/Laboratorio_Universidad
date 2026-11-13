# Guía breve de sustentación

## Estructura

- `Launcher.main`: inicia JavaFX llamando `Application.launch`.
- `LaboratorioApplication.start`: pide la contraseña si falta `DB_PASSWORD`, inicia JPA, carga el FXML y muestra la ventana. `stop` cierra la fábrica de conexiones.
- `Equipo` y `TipoEquipo`: representan las tablas. `@Entity` indica que son entidades; `@Table` señala tabla y esquema; `@Id` y `@GeneratedValue` identifican la clave creada por PostgreSQL; `@Column` configura cada columna.
- `Equipo.tipoEquipo`: `@ManyToOne` y `@JoinColumn` guardan la relación mediante `id_tipo`. Un equipo tiene un tipo. `TipoEquipo.equipos` usa `@OneToMany(mappedBy="tipoEquipo")`: un tipo puede tener muchos equipos. No hay borrado en cascada.
- `TipoEquipo.toString`: devuelve el nombre para que el ComboBox y la tabla lo muestren.
- `JPAUtil.iniciar`: crea una sola `EntityManagerFactory` usando `laboratorioPU` y la contraseña recibida. `getEntityManager` abre un gestor para una operación. `cerrar` libera la fábrica al cerrar la ventana.
- `EquipoDAO`: `guardar` usa `persist`; `actualizar` usa `merge`; `eliminar` busca y usa `remove`. Cada escritura abre transacción con `begin`, confirma con `commit`, y si ocurre una excepción hace `rollback` cuando la transacción sigue activa. `finally` cierra el EntityManager.
- `EquipoDAO.listar`: usa `JOIN FETCH` para cargar el tipo antes de cerrar EntityManager. `buscar` aplica nombre y tipo con parámetros JPQL. `existePorCodigo` cuenta coincidencias para advertir códigos repetidos.
- `EquipoDAO.demostrarRollback`: inserta un equipo temporal, fuerza un código duplicado y revierte la transacción completa.
- `TipoEquipoDAO.listar`: obtiene tipos para los ComboBox y cierra EntityManager.
- `LaboratorioController.initialize`: relaciona columnas con propiedades mediante `PropertyValueFactory`, llena estados y tipos, carga tabla y escucha la selección de filas.
- `obtenerFormulario`: lee y valida código, nombre, cantidad, tipo y estado; devuelve `null` tras mostrar alerta cuando algo es inválido.
- `actualizarBotones`: habilita Guardar cuando no hay selección y Actualizar/Eliminar cuando sí hay una.
- `guardarEquipo`, `actualizarEquipo`, `eliminarEquipo`: llaman al DAO, muestran errores o confirmación, y refrescan la tabla tras el éxito.
- `aplicarFiltros` y `limpiarCampos`: buscan según nombre/tipo y limpian el formulario.

## Tres preguntas tipo examen

1. ¿Por qué `Equipo` tiene un objeto `TipoEquipo` y no solo un número `id_tipo`?
2. ¿Qué diferencia hay entre `persist`, `merge` y `remove`? ¿Cuándo ocurre `commit`?
3. Si el segundo `INSERT` del ejemplo de rollback falla, ¿por qué tampoco queda el primero en la tabla?
