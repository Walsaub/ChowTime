module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.chowtime;
    opens com.chowtime.controllers to javafx.fxml;
}