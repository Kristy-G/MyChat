package org.example.chat_client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.chat_client.network.ChatMessageServiceImpl;
import org.example.chat.common.MessageType;
import org.example.chat.common.ChatMessage;
import org.example.chat_client.network.ChatMessageService;
import org.example.chat_client.network.MessageProcessor;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ClientInfoStatus;
import java.util.ResourceBundle;

public class MainChatController implements Initializable, MessageProcessor {
    public TextArea chatArea;
    public ListView onlineUsers;
    public TextField inputField;
    public Button btnSendMessage;
    public TextField loginField;
    public PasswordField passwordField;
    public Button btnSendAuth;
    private ChatMessageService messageService;
    private String currentName;

    public void mockAction(ActionEvent actionEvent) {
        System.out.println("MOCK!");
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void showHelp(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI("https://docs.google.com/document/d/1wr0YEtIc5yZtKFu-KITqYnBtp8KC28v2FEYUANL0YAM/edit?usp=sharing"));
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String text = inputField.getText();
        if (text.isEmpty()) return;
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.PUBLIC);
        msg.setFrom(currentName);
        msg.setBody(text);
        messageService.send(msg.marshall());
        inputField.clear();
    }

    private void appendTextFromTF(ChatMessage msg) throws IOException {
//        String msg = inputField.getText();
//        if (msg.isEmpty()) return;
        String text = String.format("[%s] %s\n", msg.getFrom(), msg.getBody());
        chatArea.appendText(text);
//        inputField.clear();
        addToFile(text);
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong!");
        alert.setHeaderText(e.getMessage());
        VBox dialog = new VBox();
        Label label = new Label("Trace:");
        TextArea textArea = new TextArea();
        //TODO
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) {
            builder.append(el).append(System.lineSeparator());
        }
        textArea.setText(builder.toString());
        dialog.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialog);
        alert.showAndWait();
    }

    public void showAbout(ActionEvent event) {
        Label secondLabel = new Label("Copyright:\nAlex Grigorev\ngb.ru\n2021");

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene aboutScene = new Scene(secondaryLayout, 230, 100);

        Stage aboutWindow = new Stage();
        aboutWindow.setTitle("About");
        aboutWindow.setScene(aboutScene);

        Stage aboutStage = new Stage();
        aboutWindow.setX(aboutStage.getX() + 200);
        aboutWindow.setY(aboutStage.getY() + 100);

        aboutWindow.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.messageService = new ChatMessageServiceImpl("localhost", 12256, this);
        messageService.connect();
    }

    @Override
    public void processMessage(String msg) {
        Platform.runLater(() -> {
                    ChatMessage message = ChatMessage.unmarshall(msg);
                    System.out.println("Received message");

                    switch (message.getMessageType()) {
                        case PUBLIC -> {
                            try {
                                appendTextFromTF(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        case CLIENT_LIST -> refreshOnlineUsers(message);
                        case AUTH_CONFIRM -> {
                            this.currentName = message.getBody();
                            App.stage1.setTitle(currentName);
                        }
                    }
                }
        );
    }


    private void refreshOnlineUsers(ChatMessage message) {
        this.onlineUsers.setItems(FXCollections.observableArrayList(message.getOnlineUsers()));
    }

    public void sendAuth(ActionEvent actionEvent) throws IOException {
        String log = loginField.getText();
        String pass = passwordField.getText();
        if (log.isEmpty() || pass.isEmpty()) return;
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.SEND_AUTH);
        msg.setLogin(log);
        msg.setPassword(pass);
        messageService.send(msg.marshall());
    }

    public void addToFile(String message) throws IOException {
        String str = "chat_client/src/main/java/files/" + this.currentName + ".txt";
        File file = new File(str);
        if (!file.exists()) file.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(str, true))) {
            bw.write(String.valueOf(message));
        }

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(str))) {
            int x;
            while ((x = bis.read()) >= 0) {
                System.out.print((char)x);
            }
        }
    }

}
