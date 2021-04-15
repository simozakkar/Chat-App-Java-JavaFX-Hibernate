package org.chatapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AddContact implements Initializable {
    public Button addButton;
    public TextField name;
    public TextField phoneNum;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnMouseClicked((MouseEvent event)->{
            if (name.getText() == null || name.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please make sure that you entered the name correctly");
                alert.showAndWait();
            }else if (phoneNum.getText() == null || phoneNum.getText().length() < 2) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please make sure that you entered the phone number correctly");
                alert.showAndWait();
            }else {
                // Write the content in file
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("contacts.txt", true))) {
                    bufferedWriter.append(name.getText()+";"+phoneNum.getText());
                    bufferedWriter.newLine();
                    close();
                } catch (IOException e) {
                    // Exception handling
                }
            }
        });
        name.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.equals(""))
                if (!newValue.matches("[A-Za-z][A-Za-z0-9_]*"))
                    name.setText(oldValue);
        });
        phoneNum.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 1) {
                phoneNum.setText(oldValue);
            }else if (!newValue.substring(1).matches("\\d*")) {
                    phoneNum.setText(oldValue);
            }
        });
    }

    @FXML
    void close(){
        // get a handle to the stage
        Stage stage = (Stage) addButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}
