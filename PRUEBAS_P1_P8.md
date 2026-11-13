# Matriz de pruebas P1–P8

Estas pruebas se realizan con PostgreSQL iniciado y la aplicación abierta. Las casillas quedan pendientes hasta probarlas en IntelliJ; la compilación Maven por sí sola no demuestra el funcionamiento de la interfaz ni la conexión real.

| Prueba | Acción | Resultado esperado | Estado |
|---|---|---|---|
| P1 | Abrir la aplicación | Se muestran los 8 equipos iniciales y su tipo | Pendiente |
| P2 | Guardar equipo con código nuevo y datos válidos | Aparece en la tabla | Pendiente |
| P3 | Intentar guardar con código ya existente | Alerta; no aparece otra fila | Pendiente |
| P4 | Dejar nombre vacío o poner cantidad negativa/texto | Alerta; no se guarda | Pendiente |
| P5 | Seleccionar equipo, cambiar marca y confirmar Actualizar | La tabla muestra la nueva marca | Pendiente |
| P6 | Seleccionar equipo y confirmar Eliminar | Desaparece de la tabla | Pendiente |
| P7 | Buscar parte de un nombre y escoger un tipo | Solo aparecen coincidencias de ambos filtros | Pendiente |
| P8 | Pulsar Probar rollback y consultar códigos `PRUEBA-%` en pgAdmin | Hay alerta de rollback y la consulta devuelve 0 filas | Pendiente |

Antes de P5 y P6, prueba también **Cancelar**: el registro debe permanecer sin cambios. Para P3 y P8 puedes usar el código `C1` de los datos iniciales.
