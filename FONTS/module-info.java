module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;
    requires junit;
    requires org.mockito;
    requires net.bytebuddy;

    opens domini.classes to javafx.fxml;
    exports domini.classes;
    exports persistencia.classes;
    opens persistencia.classes to javafx.fxml;
    exports presentacio.classes;
    opens presentacio.classes to javafx.fxml;
    /*opens domini.tests to javafx.fxml;
    exports domini.tests;*/
    /*opens persistencia.tests to javafx.fxml;
    exports persistencia.tests;*/
    opens persistencia.excepcions to javafx.fxml;
    exports persistencia.excepcions;
    opens domini.excepcions to javafx.fxml;
    exports domini.excepcions;
    opens domini.utils to javafx.fxml;
    exports domini.utils;
}