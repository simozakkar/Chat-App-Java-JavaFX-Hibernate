package org.chatapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.chatapp.entity.Friend;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static java.lang.System.exit;
import static org.chatapp.Alerts.AlertMSG.alertWarning;

public class MsgBoxController implements Initializable {
    public TextField msgField;
    public ImageView photo;
    public Button document;
    public Button record;
    public ImageView photoFriend;
    public Label nameFriend;
    public Label status;
    public Button call;
    public ListView<Pane> boxmsg;
    public ImageView sendImg;

    private Friend friend;
    private String permission;


    public Friend getFriend() {
        return friend;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        msgField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.equals("")) sendImg.setImage(new Image(String.valueOf(getClass().getResource("images/micro.png"))));
            else sendImg.setImage(new Image(String.valueOf(getClass().getResource("images/arrow.png"))));

            if (newValue.length() > 46) msgField.setText(oldValue);

        });

    }

    public void setMsgLeft(String msgValue, String msgDate){
        Rectangle rectangle = new Rectangle();
        //Setting the properties of the rectangle
        rectangle.setLayoutX(7.0f);
        rectangle.setLayoutY(4.0f);
        rectangle.setWidth(386.0f);
        rectangle.setHeight(42.0f);
        rectangle.setFill(Paint.valueOf("EDF5E1"));
        rectangle.setStroke(Paint.valueOf("cdcdcd"));
        rectangle.setStrokeType(StrokeType.INSIDE);

        //Setting the height and width of the arc
        rectangle.setArcWidth(20.0);
        rectangle.setArcHeight(20.0);

        Label labelMsg = new Label(msgValue);
        labelMsg.setText(msgValue);
        labelMsg.setLayoutX(14);
        labelMsg.setLayoutY(4);

        Label labelDate = new Label();
        labelDate.setText(msgDate.replace('T',' ').substring(0, msgDate.lastIndexOf(":")));
        labelDate.setLayoutX(242);
        labelDate.setLayoutY(29);
        Pane pane = new Pane(rectangle, labelMsg, labelDate);
        boxmsg.getItems().add(pane);
    }

    private void setMsgRight(String msgValue, String msgDate){
        Rectangle rectangle = new Rectangle();
        //Setting the properties of the rectangle
        rectangle.setLayoutX(103.0f);
        rectangle.setLayoutY(3.0f);
        rectangle.setWidth(386.0f);
        rectangle.setHeight(42.0f);
        rectangle.setFill(Paint.valueOf("5CDB95"));
        rectangle.setStroke(Paint.valueOf("cdcdcd"));
        rectangle.setStrokeType(StrokeType.INSIDE);

        //Setting the height and width of the arc
        rectangle.setArcWidth(20.0);
        rectangle.setArcHeight(20.0);

        Label labelMsg = new Label(msgValue);
        labelMsg.setText(msgValue);
        labelMsg.setLayoutX(109);
        labelMsg.setLayoutY(6);



        Label labelDate = new Label();
        labelDate.setText(msgDate.replace('T',' ').substring(0, msgDate.lastIndexOf(":")));
        labelDate.setLayoutX(360);
        labelDate.setLayoutY(29);

        Pane pane = new Pane(rectangle, labelMsg, labelDate);
        boxmsg.getItems().add(pane);
    }

    @FXML
    public void sendMsgOrVoice(){
        // send msg
        if (!msgField.getText().equals(""))
            new Thread(() -> {
                try {
                    if(permission != null) {
                        Socket socket = new Socket("localhost", 1240);
                        PrintStream printStream = new PrintStream(socket.getOutputStream());
                        printStream.println(permission);
                        BufferedReader flux = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String resp = flux.readLine();
                        if (resp.equals("200")) {
                            String content = msgField.getText();
                            String destination = friend.getPhoneNum();
                            String dateTime = LocalDateTime.now().toString();

                            printStream.println(destination);
                            printStream.println(content);

                            msgField.setText("");
                            // Display the msg.
                            Platform.runLater(()->{
                                setMsgRight(content, dateTime);
                                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destination+".txt", true))) {
                                    // Write the content in file
                                    bufferedWriter.append("right;").append(content).append(";").append(dateTime);
                                    bufferedWriter.newLine();
                                    bufferedWriter.close();
                                    socket.close();
                                }catch (Exception e){

                                }
                            });

                        }else {
                            System.out.println("No user in the DB with the number or the access key in the File");
                            exit(-1);
                        }
                    }else {
                        System.out.println("Empty File");
                        exit(-1);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        // Record voice and send it
        else {
            System.out.println("Send voice");
        }
    }

    public void setupMsgBox(Friend friend, String permission){
        this.friend = friend;
        this.permission = permission;
        nameFriend.setText(friend.getNameContact());
        setStatu(friend);

        Platform.runLater(()->{
            // display the old msgs from num.txt file
            try {
                // Read the content from file
                BufferedReader bufferedReader = new BufferedReader(new FileReader(friend.getPhoneNum()+".txt"));
                String line = bufferedReader.readLine();
                while(line != null) {
                    String[] columns = line.split(";");
                    if (columns.length == 3){
                        if (columns[0].equals("left")) setMsgLeft(columns[1], columns[2]);
                        else if (columns[0].equals("right")) setMsgRight(columns[1], columns[2]);
                    }
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
            }catch (FileNotFoundException e) {
            }catch(Exception e){
                alertWarning(e);
            }
        });

    }

    public void setStatu(Friend friend) {
        String date = friend.getLastTimeActif();
        if (friend.isActif()){
            status.setText(date);
        }else{
            status.setText(date.replace('T',' ').substring(0, date.lastIndexOf(":")));
        }
    }
}
