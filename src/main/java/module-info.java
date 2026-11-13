module ni.edu.uam.proyectolaboratorio {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;


    exports ni.edu.uam.proyectolaboratorio;
    opens ni.edu.uam.proyectolaboratorio.controller to javafx.fxml;
    exports ni.edu.uam.proyectolaboratorio.model;
    opens ni.edu.uam.proyectolaboratorio.model;
}

