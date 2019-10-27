package com.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Client_launcher extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Client_GUI.fxml"));
        Parent root = (Parent)loader.load();
        Client_Controller controller = (Client_Controller)loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("B_ChAt");
        Scene sc = new Scene(root, 705, 633);
        sc.getStylesheets().add(getClass().getResource("Button.css").toExternalForm());
        primaryStage.setScene(sc);
        primaryStage.setScene(sc);
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("com/client/index.png").toString()));
        primaryStage.show();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
