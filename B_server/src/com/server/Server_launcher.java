package com.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Server_launcher extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Server_GUI.fxml"));
        //primaryStage.setTitle("B_ChAt");
        primaryStage.setScene(new Scene(root, 550, 300));
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("com/server/index.png").toString()));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
