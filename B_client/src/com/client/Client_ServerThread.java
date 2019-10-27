package com.client;

import com.Messege.Messege;

import java.io.*;
import java.net.Socket;

public class Client_ServerThread extends Thread
{
    private final Socket clientsocket;
    private ObjectOutputStream streamOut = null;
    private ObjectInputStream streamIn = null;
    private Client_Controller gui;
    private Client_Server clientServer;

    public Client_ServerThread(Socket clientsocket, Client_Controller gui, Client_Server clientServer)
    {
        this.clientsocket = clientsocket;
        this.gui = gui;
        this.clientServer = clientServer;

    }

    @Override
    public void run() {
        try
        {
            handleClientSocket();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException
    {
        streamOut = new ObjectOutputStream(clientsocket.getOutputStream());
        streamOut.flush();
        streamIn = new ObjectInputStream(clientsocket.getInputStream());
        try
        {
            Messege msg = (Messege) streamIn.readObject();
            clientServer.handle(msg);

        }
        catch(Exception ioe)
        {


        }
        ///clientsocket.close();
    }

}
