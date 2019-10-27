package com.server;


import com.Messege.Messege;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread
{
    private SocketServer server = null;
    private Socket socket = null;
    private int ID = -1;
    private String username = "";
    private String ip = "";
    private ObjectOutputStream streamOut = null;
    private ObjectInputStream streamIn = null;
    private Server_Controller gui;
    private String port;
    public ServerThread(SocketServer server, Socket socket)
    {
        super();
        this.server = server;
        this.socket = socket;
        ID = this.socket.getPort();
        gui = server.getGui();
    }
    public void send(Messege msg)
    {
        try
        {
            streamOut.writeObject(msg);
            streamOut.flush();
        }
        catch(IOException ex)
        {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }
    public int getID()
    {
        return ID;
    }
    @SuppressWarnings("deprecation")
    public void run()
    {
        gui.getTextArea().appendText("\nServer Thread " + ID + " running.");
        while(true)
        {
            try
            {
                Messege msg = (Messege) streamIn.readObject();
                server.handle( ID, msg);
            }
            catch(Exception ioe)
            {
                System.out.println(ID + " Error reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }
    public void open() throws IOException
    {
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        streamOut.flush();
        streamIn = new ObjectInputStream(socket.getInputStream());
    }

    public void close() throws IOException
    {
        if(socket != null) socket.close();
        if(streamIn != null) streamIn.close();
        if(streamOut != null) streamOut.close();
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    public String getIp()
    {
        return ip;
    }
    public void setPort(String port)
    {
        this.port = port;
    }
    public String getPort()
    {
        return port;
    }
}
