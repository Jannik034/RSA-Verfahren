package com.example.rsaverfahren;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Controller {
    @FXML
    private TabPane tabPane;

    @FXML
    protected void initialize() {
        tabPane.getTabs().setAll(
                new Tab("Verschlüsseln", new Label("Verschlüsseln")),
                new Tab("Entschlüsseln", new Label("Entschlüsseln")),
                new Tab("Brute Force Attacke", new Label("Brute Force Attacke"))
        );
    }
}