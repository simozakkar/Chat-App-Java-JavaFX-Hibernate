package org.chatapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.chatapp.ClientConnections.ReceiveMSGConnection;
import org.chatapp.entity.Friend;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.System.exit;
import static org.chatapp.Alerts.AlertMSG.alertError;
import static org.chatapp.Alerts.AlertMSG.alertWarning;

public class MainController implements Initializable {

    private String permission = null;

    public AnchorPane profile;
    public AnchorPane msg;
    public TextField searchRequest;
    public Button search;
    public ImageView avatar;
    public Label phoneNumber;
    public Label name;
    public Button logout;
    public Button settings;
    public Button addContact;
    public ListView<Friend> listFriends;
    public HBox cell;

    public static MsgBoxController msgBoxController = null;

    public  ObservableList<Friend> originItems = null;

    private boolean isSearch = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Thread for verify the client have the permission to access to the account, and display the list of friends.
        Platform.runLater(() -> {
            try {
                // Read the content from file
                BufferedReader bufferedReader = new BufferedReader(new FileReader("autho.txt"));
                this.permission = bufferedReader.readLine();
                bufferedReader.close();

                if(this.permission != null) {
                    Socket socket = new Socket("localhost", 1237);
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    printStream.println(this.permission);
                    BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String resp = flux.readLine();
                    if (resp.equals("200")) {
                        String phoneNum = this.permission.split(";")[0];
                        phoneNumber.setText(phoneNum);
                        name.setText(flux.readLine());
                        socket.close();
                        // lunch thread for recevie msgs from the server.
                        new Thread(new ReceiveMSGConnection(this.permission)).start();
                        // set the list of friends
                        setupListViewFriends();

                    }else {
                        System.out.println("No user in the DB with the number or the access key in the File");
                        exit(-1);
                    }
                }else {
                    System.out.println("Empty File");
                    exit(-1);
                }
            }catch (FileNotFoundException e) {
                e.printStackTrace();
                exit(-1);
            }catch (IOException e) {
                    e.printStackTrace();
            }
        });
        searchRequest.textProperty().addListener((observable, oldValue, newValue)->{
            if(!newValue.equals("")){
                isSearch = true;
                ObservableList<Friend> searchItems = FXCollections.observableArrayList ();
                for (Friend friend:originItems){
                    if (friend.getNameContact().contains(newValue)){
                        searchItems.add(friend);
                    }
                }
                listFriends.setItems(searchItems);
            }else {
                isSearch = false;
                listFriends.setItems(originItems);
            }
        });
        logout.setOnMouseClicked((MouseEvent event)->{
            File file = new File("autho.txt");
            file.delete();
            closeButtonAction();
        });
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addContact(){
        try {
            Parent parent = new FXMLLoader(getClass().getResource("fxml/addContact.fxml")).load();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            setItems();
        }  catch (IOException ex) {
            alertWarning(ex);
        }
    }

    private void setupListViewFriends(){
        listFriends.setCellFactory(new Callback<>() {

            @Override
            public ListCell<Friend> call(ListView<Friend> list) {
                ListCell<Friend> cell = new ListCell<>() {
                    @Override
                    public void updateItem(Friend item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            return;
                        }

                        ImageView imView = new ImageView();
                        imView.setFitHeight(48.0);
                        imView.setFitWidth(30.0);
                        imView.setPickOnBounds(true);
                        imView.setPreserveRatio(true);
                        imView.setImage(new Image(String.valueOf(getClass().getResource("images/avatarMale.png"))));
                        VBox vbLeft = new VBox(imView);
                        vbLeft.setPrefHeight(44.0);
                        vbLeft.setPrefWidth(40.0);
                        HBox hbTop = new HBox(new Label(item.getNameContact()));
                        HBox hbBottom = new HBox(new Label(item.getPhoneNum()));
                        VBox vbCenter = new VBox(hbTop, hbBottom);
                        vbCenter.setPrefHeight(28.0);
                        vbCenter.setPrefWidth(120.0);
                        VBox vbRight ;
                        if (item.isActif()){
                            ImageView imActif = new ImageView();
                            imActif.setFitHeight(17.0);
                            imActif.setFitWidth(17.0);
                            imActif.setPickOnBounds(true);
                            imActif.setPreserveRatio(true);
                            imActif.setImage(new Image(String.valueOf(getClass().getResource("images/circle.png"))));
                            vbRight = new VBox(imActif);
                        }else vbRight = new VBox();
                        HBox hb = new HBox(vbLeft, vbCenter, vbRight);
                        setGraphic(hb);
                    }
                };

                cell.pressedProperty().addListener((observableValue, aBoolean, t1) -> {
                    if (t1 && cell.getItem()!=null) {
                        try {
                            msg.getChildren().clear();
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/messageBox.fxml"));
                            Parent parent = fxmlLoader.load();
                            msgBoxController = fxmlLoader.getController();
                            msgBoxController.setupMsgBox(cell.getItem(), permission);
                            msg.getChildren().add(parent);
                            //
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return cell;
            }
        });

        // A Thread who can refresh the list of friends in every 10 seconds.
        new Thread(() -> {
            while(true){
                try {
                    sayHiToTheServer();
                    setItems();
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setItems(){
        Platform.runLater(()->{
            ObservableList<Friend> items = FXCollections.observableArrayList ();
            try {
                // Read the content from file
                BufferedReader bufferedReader = new BufferedReader(new FileReader("contacts.txt"));
                String line = bufferedReader.readLine();
                while(line != null && line.contains(";+")) {
                    // To receive info about client cantacts
                    Socket socket = new Socket("localhost", 1238);
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    printStream.println(line.substring(line.indexOf("+")));
                    BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String resp = flux.readLine();
                    if (!resp.equals("404")) {
                        boolean actif;
                        String lastTimeActif = "online" ;
                        if (flux.readLine().equals("true")){
                            actif = true;
                        }else{
                            actif = false;
                            lastTimeActif = flux.readLine();
                            lastTimeActif = "see "+lastTimeActif;
                        }
                        String phNumber = line.substring(line.indexOf("+"));
                        Friend friendContact = new Friend(phNumber, resp, line.substring(0,line.indexOf(";")), "something", actif, lastTimeActif);
                        items.add(friendContact);
                        if(msgBoxController != null && msgBoxController.getFriend().getPhoneNum().equals(phNumber)){
                            msgBoxController.setStatu(friendContact);
                        }
                    }
                    socket.close();
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                originItems = items;
                if (!isSearch) listFriends.setItems(items);
            }catch (FileNotFoundException e) {
            }catch(Exception e){
                alertWarning(e);
            }
        });
    }

    private void sayHiToTheServer() {
        try{
            Socket socket = new Socket("localhost", 1241);
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.println(this.permission);
            BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String resp = flux.readLine();
            if (!resp.equals("200")) {
                alertError(new Exception("Server Error !!"));
                exit(1);
            }
            socket.close();
        }catch (Exception e){
            alertWarning(e);
        }

    }
}