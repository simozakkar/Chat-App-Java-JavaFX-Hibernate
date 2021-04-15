package org.chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

import static org.chatapp.Alerts.AlertMSG.alertError;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    public static void setRoot(String nameFile) throws IOException {
        scene.setRoot(loadFXML("fxml/"+nameFile));
    }

    @Override
    public void start(Stage stage) {
        stage.setResizable(false);
        // Read the content from file
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader("autho.txt"));
            String line = bufferedReader.readLine();
            if(line != null) {
                Socket socket = new Socket("localhost", 1236);
                PrintStream printStream = new PrintStream(socket.getOutputStream());
                printStream.println(line);
                BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String resp = flux.readLine();
                if (resp.equals("200")) {
                    scene = new Scene(loadFXML("fxml/main"), 756, 522);
                    stage.setScene(scene);
                    stage.show();
                    socket.close();
                    return;
                }
            }
        }catch (FileNotFoundException e){
            // nothing
        } catch (Exception e){
            alertError(e);
        }
        try {
            scene = new Scene(loadFXML("fxml/setNumberPhone"), 756, 522);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}