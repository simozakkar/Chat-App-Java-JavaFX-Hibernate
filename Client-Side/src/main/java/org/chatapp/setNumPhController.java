package org.chatapp;


import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.chatapp.Alerts.AlertMSG.alertWarning;
import static org.chatapp.App.setRoot;

public class setNumPhController implements Initializable {
    public Button sendCode;
    public TextField flagsCode;
    public ImageView imgView;
    public TextField code1;

    public TextField code2;

    public TextField code3;

    public TextField code4;
    public Button check;
    public TextField numberPhone;

    public String numero;
    public ProgressIndicator spinnerProgerss;
    public Pane paneCheckCode;
    public ProgressIndicator spinnerProgerssCheckButton;
    public Label wrong;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        flagsCode.textProperty().addListener((observableValue, oldValue, newValue) -> {

            // the flag code should be +([\\d]*3)
            if (newValue.length() < 1 || newValue.length() > 4) {
                flagsCode.setText(oldValue);
            } else {
                if (!newValue.substring(1).equals("")) {
                    try {
                        Integer.parseInt(newValue.substring(1));
                        // display the flag
                        switch (newValue) {
                            case "+212":
                                imgView.setImage(new Image(getClass().getResourceAsStream("/org/chatapp/images/flags/maroc.png")));
                                break;
                            case "+39": // Italy
                                imgView.setImage(new Image(getClass().getResourceAsStream("/org/chatapp/images/flags/italy.png")));
                                break;
                            case "+34": // Espagne
                                imgView.setImage(new Image(getClass().getResourceAsStream("/org/chatapp/images/flags/spain.png")));
                                break;
                            case "+33": // France
                                imgView.setImage(new Image(getClass().getResourceAsStream("/org/chatapp/images/flags/france.png")));
                                break;
                            case "+49": // Allemagne
                                imgView.setImage(new Image(getClass().getResourceAsStream("/org/chatapp/images/flags/germany.png")));
                                break;
                            default:
                                imgView.setImage(null);
                                break;
                        }
                    } catch (Exception e) {
                        flagsCode.setText(oldValue);
                    }
                    if (!numberPhone.getText().equals(""))
                        sendCode.setVisible(true);
                } else sendCode.setVisible(false);
            }
        });

        //d==>[0-9]
        // For a correct phone number.
        numberPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                try {
                    Integer.parseInt(newValue);
                    if (flagsCode.getText().length() > 1) {
                        sendCode.setVisible(true);
                    }
                } catch (Exception e) {
                    numberPhone.setText(oldValue);
                }
            }else sendCode.setVisible(false);
        });

        // Take the phone number and send SMS with validation code to the client.
        sendCode.setOnMouseClicked(mouseEvent -> {
                sendCode.setVisible(false);
                paneCheckCode.setVisible(false);
                spinnerProgerss.setVisible(true);
                new Thread(() -> {
                    try {
                        Socket socket = new Socket("localhost", 1234);
                        PrintStream printStream = new PrintStream(socket.getOutputStream());
                        String flag = flagsCode.getText();
                        String numPhone = numberPhone.getText();
                        numero = flag + numPhone;
                        printStream.println(numero);
                        BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String resp = flux.readLine();
                        if (resp.equals("200")){
                            spinnerProgerss.setVisible(false);
                            paneCheckCode.setVisible(true);
                        }
                        socket.close();
                    } catch (IOException e) {
                        spinnerProgerss.setVisible(false);
                        alertWarning(e);

                    }
                }).start();
        });

        // Add listenner to let the client enter a one number in {code1, code2, code3, code4}
        addListenerTextFieldCode(code1);
        addListenerTextFieldCode(code2);
        addListenerTextFieldCode(code3);
        addListenerTextFieldCode(code4);

        // Send the validation code to the server and wait the response.
        check.setOnMouseClicked(mouseEvent -> {
            wrong.setVisible(false);
            check.setVisible(false);
            spinnerProgerssCheckButton.setVisible(true);
            try {
                Socket socket = new Socket("localhost", 1235);
                PrintStream printStream = new PrintStream(socket.getOutputStream());
                String codeAndNum = code1.getText() + code2.getText() + code3.getText() + code4.getText() + numero;
                printStream.println(codeAndNum);
                BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // If the validation code is correct the server will send "200"(OK)
                if (flux.readLine().equals("200")){
                    // Write the phone number and the access key in autho file
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("autho.txt"))) {
                        String fileContent = flux.readLine();
                        bufferedWriter.write(fileContent);
                    } catch (IOException e) {
                        // Exception handling
                        alertWarning(e);
                    }
                    setRoot("main");
                }else{
                    // Validation Code is wrong
                    wrong.setVisible(true);
                    check.setVisible(true);
                    spinnerProgerssCheckButton.setVisible(false);
                }
                socket.close();
            } catch (IOException e) {
                alertWarning(e);
            }
        });
    }

    private void addListenerTextFieldCode(TextField codeField){
        codeField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                codeField.setText(oldValue);
            }
            if (!newValue.equals("")) {
                try {
                    Integer.parseInt(newValue);
                } catch (Exception e) {
                    codeField.setText(oldValue);
                }
            }
            if (!code1.getText().equals("") && !code2.getText().equals("") && !code3.getText().equals("") && !code4.getText().equals(""))
                check.setVisible(true);
            else check.setVisible(false);
        });
    }

}