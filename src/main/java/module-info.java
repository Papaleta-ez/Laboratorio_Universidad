module ni.edu.uam.laboratorio_universidad {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.laboratorio_universidad to javafx.fxml;
    exports ni.edu.uam.laboratorio_universidad;
}