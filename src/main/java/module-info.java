module com.example.rsaverfahren {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.rsaverfahren to javafx.fxml;
    exports com.example.rsaverfahren;
}