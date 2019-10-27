package com.server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Server_Controller implements Initializable
{
    @FXML
    private Button browse;
    @FXML
    private Button start_server;
    @FXML
    private Label db_file;
    @FXML
    private TextField textField;
    @FXML
    private TextArea textArea;

    private File selectedFile;

    private SocketServer server;

    private String filepath;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        textField.setEditable(false);
        browse.setDisable(false);
        start_server.setDisable(true);
        textArea.setEditable(false);
    }
    public boolean isWin32()
    {
        return System.getProperty("os.name").startsWith("Windows");
    }
    public void browseButtonAction() throws IOException
    {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML Document (.xml)", "*.xml");
        fc.getExtensionFilters().add(extFilter);
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null)
        {
            filepath = selectedFile.getAbsolutePath();
            if(this.isWin32())
            {
                filepath = filepath.replace("\\", "/");
            }
            textField.setText(selectedFile.getAbsolutePath());
            browse.setDisable(true);
            start_server.setDisable(false);
        }
    }
    public void start_serverButtonAction() throws IOException
    {
        server = new SocketServer(this);
        start_server.setDisable(true);
    }
    public TextArea getTextArea()
    {
        return textArea;
    }
    public String getFilepath()
    {
        return filepath;
    }

}
