package org.chatapp.Alerts;

import javafx.scene.control.Alert;

import static java.lang.System.exit;

public class AlertMSG {
    public static void alertError(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(e.getMessage());
        alert.setHeaderText(null);
        alert.setTitle("ERROR");
        alert.showAndWait();
        exit(1);
    }
    public static void alertWarning(Exception e){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(e.getMessage());
        alert.setHeaderText(null);
        alert.setTitle("ERROR");
        alert.showAndWait();
    }
}
