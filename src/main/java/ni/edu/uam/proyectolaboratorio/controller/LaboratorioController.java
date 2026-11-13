package ni.edu.uam.proyectolaboratorio.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.proyectolaboratorio.dao.EquipoDAO;
import ni.edu.uam.proyectolaboratorio.dao.TipoEquipoDAO;
import ni.edu.uam.proyectolaboratorio.model.Equipo;
import ni.edu.uam.proyectolaboratorio.model.TipoEquipo;

import java.util.List;
import java.util.Optional;

public class LaboratorioController {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<TipoEquipo> cmbTipo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private TableView<Equipo> tblEquipos;
    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, Integer> colCantidad;
    @FXML private TableColumn<Equipo, TipoEquipo> colTipo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();
    private final TipoEquipo todos = new TipoEquipo();
    private Long idSeleccionado = null;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEquipo"));

        cmbEstado.setItems(FXCollections.observableArrayList(
                "Disponible", "En mantenimiento", "Fuera de servicio"));
        todos.setNombre("Todos");
        cargarTipos();
        cargarEquipos();

        tblEquipos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        seleccionarEquipo(newValue);
                    }
                });
        actualizarBotones();
    }

    private void cargarTipos() {
        try {
            List<TipoEquipo> tipos = tipoEquipoDAO.listar();
            cmbTipo.setItems(FXCollections.observableArrayList(tipos));
            cmbFiltroTipo.setItems(FXCollections.observableArrayList());
            cmbFiltroTipo.getItems().add(todos);
            cmbFiltroTipo.getItems().addAll(tipos);
            cmbFiltroTipo.setValue(todos);
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron cargar los tipos de equipo.");
        }
    }

    private void cargarEquipos() {
        try {
            List<Equipo> equipos = equipoDAO.listar();
            tblEquipos.setItems(FXCollections.observableArrayList(equipos));
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo cargar la lista de equipos.");
        }
    }

    @FXML
    private void guardarEquipo() {
        Equipo equipo = obtenerFormulario();
        if (equipo == null) {
            return;
        }
        try {
            if (equipoDAO.existePorCodigo(equipo.getCodigo())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Ya existe un equipo con ese código.");
                return;
            }
            equipoDAO.guardar(equipo);
            limpiarCampos();
            refrescarTabla();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Equipo guardado correctamente.");
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo guardar el equipo. Revise si el código ya existe.");
        }
    }

    @FXML
    private void actualizarEquipo() {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un equipo.");
            return;
        }
        Equipo equipo = obtenerFormulario();
        if (equipo == null) {
            return;
        }
        equipo.setId(idSeleccionado);

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setHeaderText("Actualizar equipo");
        confirmacion.setContentText("¿Desea actualizar el equipo seleccionado?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            if (equipoDAO.existePorCodigo(equipo.getCodigo(), idSeleccionado)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Ya existe otro equipo con ese código.");
                return;
            }
            equipoDAO.actualizar(equipo);
            limpiarCampos();
            refrescarTabla();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Equipo actualizado correctamente.");
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo actualizar el equipo. Revise los datos.");
        }
    }

    @FXML
    private void eliminarEquipo() {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un equipo.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setHeaderText("Eliminar equipo");
        confirmacion.setContentText("¿Desea eliminar el equipo seleccionado?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean eliminado = equipoDAO.eliminar(idSeleccionado);
            if (!eliminado) {
                mostrarAlerta(Alert.AlertType.WARNING, "El equipo ya no existe.");
                refrescarTabla();
                return;
            }
            limpiarCampos();
            refrescarTabla();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Equipo eliminado correctamente.");
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el equipo.");
        }
    }

    @FXML
    private void aplicarFiltros() {
        try {
            TipoEquipo tipo = cmbFiltroTipo.getValue();
            if (tipo == todos) {
                tipo = null;
            }
            List<Equipo> equipos = equipoDAO.buscar(txtBuscar.getText(), tipo);
            tblEquipos.setItems(FXCollections.observableArrayList(equipos));
            tblEquipos.getSelectionModel().clearSelection();
            idSeleccionado = null;
            actualizarBotones();
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron aplicar los filtros.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();
        cmbTipo.setValue(null);
        cmbEstado.setValue(null);
        idSeleccionado = null;
        tblEquipos.getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void seleccionarEquipo(Equipo equipo) {
        idSeleccionado = equipo.getId();
        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca());
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));
        cmbEstado.setValue(equipo.getEstado());
        cmbTipo.setValue(equipo.getTipoEquipo());
        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnGuardar.setDisable(!sinSeleccion);
        btnActualizar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Equipo obtenerFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El código y el nombre son obligatorios.");
            return null;
        }
        if (codigo.length() > 30 || nombre.length() > 150 || marca.length() > 100) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código, nombre o marca demasiado largos.");
            return null;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "La cantidad debe ser un número entero.");
            return null;
        }
        if (cantidad < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "La cantidad no puede ser negativa.");
            return null;
        }
        if (cmbTipo.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un tipo de equipo.");
            return null;
        }

        String estado = cmbEstado.getValue();
        if (!"Disponible".equals(estado)
                && !"En mantenimiento".equals(estado)
                && !"Fuera de servicio".equals(estado)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un estado permitido.");
            return null;
        }

        Equipo equipo = new Equipo();
        equipo.setCodigo(codigo);
        equipo.setNombre(nombre);
        equipo.setMarca(marca);
        equipo.setCantidad(cantidad);
        equipo.setEstado(estado);
        equipo.setTipoEquipo(cmbTipo.getValue());
        return equipo;
    }

    private void refrescarTabla() {
        txtBuscar.clear();
        cmbFiltroTipo.setValue(todos);
        cargarEquipos();
    }

    @FXML
    private void demostrarRollback() {
        List<Equipo> equipos = tblEquipos.getItems();
        if (equipos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Debe haber un equipo en la tabla.");
            return;
        }
        Equipo ejemplo = equipos.get(0);
        try {
            equipoDAO.demostrarRollback(ejemplo.getCodigo(), ejemplo.getTipoEquipo());
            mostrarAlerta(Alert.AlertType.ERROR, "La demostración no produjo el error esperado.");
        } catch (RuntimeException ex) {
            refrescarTabla();
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "La escritura falló por código duplicado y se ejecutó rollback. "
                            + "Compruebe que no existe un código PRUEBA en PostgreSQL.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
