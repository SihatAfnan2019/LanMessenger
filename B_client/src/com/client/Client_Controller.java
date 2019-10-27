package com.client;

import com.Messege.Messege;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client_Controller implements Initializable
{
    @FXML
    private Label host_address;
    @FXML
    private Label host_port;
    @FXML
    private Label user_name;
    @FXML
    private Label password;
    @FXML
    private Label destination_folder;
    @FXML
    private Label messege;
    @FXML
    private Label file;
    @FXML
    private Label onlyName;
    @FXML
    private Button connect;
    @FXML
    private Button log_in;
    @FXML
    private Button sign_up;
    @FXML
    private Button browse_destination_folder;
    @FXML
    private Button ok;
    @FXML
    private Button send_messege;
    @FXML
    private Button browse_file;
    @FXML
    private Button send_file;
    @FXML
    private Button log_out;
    @FXML
    private Button sendInGroup;
    @FXML
    private TextField host_addresstxt;
    @FXML
    private TextField host_porttxt;
    @FXML
    private TextField user_nametxt;
    @FXML
    private TextField passwordtxt;
    @FXML
    private TextField destination_foldertxt;
    @FXML
    private TextField messegetxt;
    @FXML
    private TextField filetxt;
    @FXML
    private TextArea chat_box;
    @FXML
    private ListView active_user;
    @FXML
    private ImageView userHead;

    private SocketClient client, tempClient;
    private int port;
    private String server_Address, userName, passWord;
    private Thread tempClientThread, clientThread;
    private File file1;
    private File selectedFile;
    private String filepath;
    private Stage primaryStage;
    private String saveTo;
    private  File selectedDirectory;
    private String directoryFilePath;
    ObservableList<String> list = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        active_user.setItems(list);
        active_user.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        chat_box.setEditable(false);
        log_out.setDisable(true);
        log_in.setDisable(true);
        sign_up.setDisable(true);
        browse_destination_folder.setDisable(true);
        ok.setDisable(true);
        send_messege.setDisable(true);
        send_file.setDisable(true);
        browse_file.setDisable(true);
        sendInGroup.setDisable(true);
    }

    public void connectButtonActionPerformed(ActionEvent event) throws Exception
    {
        server_Address = host_addresstxt.getText();
        try
        {
            port = Integer.parseInt(host_porttxt.getText());
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        if(!server_Address.isEmpty() && !host_port.getText().isEmpty())
        {
            try
            {
                client = new SocketClient(this);
                clientThread = new Thread(client);
                clientThread.start();
                client.send(new Messege("test", "testUser", "testcontent" , "SERVER"));
                playSound("a.mp3");

                browse_destination_folder.setDisable(true);
                log_in.setDisable(false);
                sign_up.setDisable(false);

            }
            catch(Exception e)
            {
                chat_box.appendText("[Application > Me] : Server not found\n");
            }
        }
    }

    public Button getBrowse_destination_folder() {
        return browse_destination_folder;
    }

    public Button getLog_out() {
        return log_out;
    }

    public void loginButtonActionPerformed(ActionEvent event) throws Exception
    {
        userName = user_nametxt.getText();
        passWord = passwordtxt.getText();
        if(!userName.isEmpty() && !passWord.isEmpty())
        {
            client.send( new Messege("login", userName, passWord, "SERVER" , InetAddress.getLocalHost().getHostAddress(), client.getSport()));
        }
    }

    public void signUpButtonActionPerformed(ActionEvent event) throws Exception
    {
        userName = user_nametxt.getText();
        passWord = passwordtxt.getText();
        if(!userName.isEmpty() && !passWord.isEmpty())
        {
            client.send( new Messege("signup", userName, passWord, "SERVER", InetAddress.getLocalHost().getHostAddress(), client.getSport()));


        }
    }
    public void browseDestinationButtonActionPerformed(ActionEvent event) throws Exception
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        selectedDirectory = directoryChooser.showDialog(primaryStage);
        if(selectedDirectory != null)
        {
            directoryFilePath = selectedDirectory.getAbsolutePath();
            if(this.isWin32())
            {
                directoryFilePath = directoryFilePath.replace("\\", "/");
                ok.setDisable(false);
            }
            destination_foldertxt.setText(directoryFilePath);
        }
    }

    public void okButtonActionPerformed(ActionEvent event) throws Exception
    {
        if(destination_foldertxt.getText().isEmpty())
        {
            chat_box.appendText("[Application > Me] : No directory selected\n");
        }
        else
        {
            saveTo = directoryFilePath;
            System.out.println(saveTo);
            browse_destination_folder.setDisable(true);
            ok.setDisable(true);
            send_messege.setDisable(false);
            sendInGroup.setDisable(false);
            send_file.setDisable(false);
            browse_file.setDisable(false);
            //chat_box.setEditable(true);
            destination_foldertxt.setEditable(false);
        }
    }

    public void sendMessegeButtonActionPerformed(ActionEvent event) throws Exception
    {
        String msg = messegetxt.getText();
        ObservableList<String> selectedItems =  active_user.getSelectionModel().getSelectedItems();
        if(selectedItems.size() == 0)
        {
            chat_box.appendText("[Application > Me] : Please select more someone to send a messege\n");
        }
        else {
            for (String s : selectedItems) {
                String target = s;
                System.out.println(client.getTm1().get(target));
                if (!msg.isEmpty() && !target.isEmpty()) {
                    messegetxt.setText("");
                    try {
                        tempClient = new SocketClient(client.getTm1().get(target), client.getTm2().get(target));
                        tempClientThread = new Thread(tempClient);
                        tempClientThread.start();
                        tempClient.send(new Messege("messege", userName, msg, target));
                        chat_box.appendText("[Me > " + target + "] : " + msg + "\n");
                    } catch (Exception e) {
                        chat_box.appendText("[Application > Me] : ClientServer of " + target + " not found\n");
                    }
                    tempClient.close();
                    tempClient.closeThread(tempClientThread);
                }
            }
        }

    }
    public void SendInGroupActionPerformed(ActionEvent event)throws Exception
    {
        String tempstr = "";
        String msg = messegetxt.getText();
        ObservableList<String> selectedItems =  active_user.getSelectionModel().getSelectedItems();
        if(selectedItems.size() == 0 || selectedItems.size() == 1)
        {
            chat_box.appendText("[Application > Me] : Please select more than one to send a messege\n");
        }
        else
            {
            for (int i = 0; i < selectedItems.size(); i++) {
                if (i != 0) {
                    tempstr += ", ";
                }
                tempstr += selectedItems.get(i);
            }
            System.out.println(tempstr);
            for (String s : selectedItems) {
                String target = s;
                System.out.println(client.getTm1().get(target));
                if (!msg.isEmpty() && !target.isEmpty()) {
                    messegetxt.setText("");
                    try {
                        tempClient = new SocketClient(client.getTm1().get(target), client.getTm2().get(target));
                        tempClientThread = new Thread(tempClient);
                        tempClientThread.start();
                        tempClient.send(new Messege("messege", userName, msg, tempstr));
                        chat_box.appendText("[Me > " + target + "] : " + msg + "\n");
                    } catch (Exception e) {
                        chat_box.appendText("[Application > Me] : ClientServer of " + target + " not found\n");
                    }
                    tempClient.close();
                    tempClient.closeThread(tempClientThread);
                }
            }
        }

    }

    public boolean isWin32()
    {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public void browseFileButtonActionPerformed(ActionEvent event) throws Exception
    {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null)
        {
            filepath = selectedFile.getAbsolutePath();
            if(this.isWin32())
            {
                filepath = filepath.replace("\\", "/");
            }
            filetxt.setText(filepath);
        }
    }

    public void sendFileButtonActionPerformed(ActionEvent event) throws Exception
    {
        if(selectedFile != null)
        {
            long size = selectedFile.length();
            if(size < 120*1024*1024)
            {
                ObservableList<String> selectedItems =  active_user.getSelectionModel().getSelectedItems();
                if(selectedItems.size() == 0)
                {
                    chat_box.appendText("[Application > Me] : Please select someone to send the file\n");
                }
                else if(selectedItems.size() == 1)
                {
                    for(String s : selectedItems)
                    {
                        String target = s;
                        System.out.println(client.getTm1().get(target));
                        try
                        {
                            tempClient = new SocketClient(client.getTm1().get(target), client.getTm2().get(target));
                            tempClientThread = new Thread(tempClient);
                            tempClientThread.start();
                            tempClient.send(new Messege("upload_req", userName , selectedFile.getName(), target));
                        }
                        catch (Exception e)
                        {
                            chat_box.appendText("[Application > Me] : ClientServer " + target + " not found\n");
                        }
                        tempClient.close();
                        tempClient.closeThread(tempClientThread);
                    }
                }
                else
                {
                    chat_box.appendText("[Application > Me] : Please select one person to send the file\n");
                }
            }
            else
            {
                chat_box.appendText("[Application > Me] : File size is large\n");
            }
        }
    }

    public void logOutButtonActionPerformed(ActionEvent event) throws Exception
    {
        client.send( new Messege( "logout", userName, "", "SERVER"));
        Platform.exit();
        System.exit(0);

    }
    public void Hide()
    {

        host_addresstxt.setVisible(false);
        host_porttxt.setVisible(false);
        host_address.setVisible(false);
        host_port.setVisible(false);
        user_name.setVisible(false);
        password.setVisible(false);
        log_in.setVisible(false);
        sign_up.setVisible(false);
        connect.setVisible(false);
        passwordtxt.setVisible(false);
        user_nametxt.setVisible(false);
        onlyName.setVisible(true);
        String name = user_nametxt.getText();
        Platform.runLater(() ->
        {
           onlyName.setText(name);
        });
    }
    void removeList(int i)
    {
        Platform.runLater(() ->
        {
           final String a = list.remove(i);
        });
    }
    void addList(String a)
    {
        Platform.runLater(() ->
        {
            list.add(a);
        });
    }

    public void playSound(String sound)
    {
        AudioClip note = new AudioClip(this.getClass().getResource(sound).toString());
        note.play();
    }

    public String getHostAdress()
    {
        return host_addresstxt.getText();
    }
    public int getHostPort()
    {
        return port;
    }
    public String getUserName()
    {

        return user_nametxt.getText();

    }
    public Button getConnect()
    {
        return connect;
    }
    public Button getLog_in()
    {
        return log_in;
    }
    public Button getSign_up() { return sign_up; }
    public Button getBrowse_history()
    {
        return browse_destination_folder;
    }
    public Button getSend_messege()
    {
        return send_messege;
    }
    public Button getBrowse_file()
    {
        return browse_file;
    }
    public Button getSend_file()
    {
        return send_file;
    }
    public TextArea getChat_box()
    {
        return chat_box;
    }
    public TextField getHost_addresstxt()
    {
        return host_addresstxt;
    }
    public TextField getHost_porttxt()
    {
        return host_porttxt;
    }
    public TextField getUser_nametxt()
    {
        return user_nametxt;
    }
    public TextField getPasswordtxt()
    {
        return passwordtxt;
    }
    public ListView getActive_user()
    {
        return active_user;
    }
    public ObservableList<String> getList()
    {
        return list;
    }
    public void setStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }
    public String getDirectoryFilePath()
    {
        return directoryFilePath;
    }
    public String getFilepath()
    {
        return filepath;
    }
    public Stage getPrimaryStage()
    {return primaryStage;}
}
