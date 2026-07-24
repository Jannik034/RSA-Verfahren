module com.example.rsaverfahren {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rsaverfahren to javafx.fxml;
    exports com.example.rsaverfahren;
}