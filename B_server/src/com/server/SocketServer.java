package com.server;

import com.Messege.Messege;
import javafx.application.Platform;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable
{
    private ServerThread clients[];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0, port ;
    private Server_Controller gui;
    private Database db;
    public SocketServer(Server_Controller gui)
    {
        this.gui = gui;
        clients = new ServerThread[50];

        db = new Database(this.gui.getFilepath());

        try
        {
            server = new ServerSocket( 0, 50,InetAddress.getLocalHost());
            start();
            port = server.getLocalPort();
            gui.getTextArea().appendText("Server started. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort() + "\n");
            gui.getTextArea().appendText("\nWaiting for a client ...");


        }
        catch (IOException ioe)
        {
            gui.getTextArea().appendText("Can not bind to port : " + port + "\nRetrying");
        }
    }
    @Override
    public void run()
    {
         while(thread != null)
         {
             try
             {
                 addThread(server.accept());
                 gui.getTextArea().appendText("\nWaiting for a client ...");

             }
             catch (Exception e)
             {
                 System.out.println("Problem in run() in SocketServer class");
             }
         }
    }
    public void start()
    {
       if(thread == null)
       {

           thread = new Thread(this);
           thread.start();
       }
    }

    @SuppressWarnings("deprecation")
    public void stop()
    {

        if (thread != null)
        {
            thread.stop();
            thread = null;
        }
    }
    private int findClient(int ID){
        for (int i = 0; i < clientCount; i++){
            if (clients[i].getID() == ID){
                return i;
            }
        }
        return -1;
    }



    public synchronized void handle(int ID, Messege msg)
    {
        if (msg.getType().equals("logout"))
        {
            Announce("signout", "SERVER", msg.getSender());
            remove(ID);
        }
        else{
            if(msg.getType().equals("login")){
                if(findUserThread(msg.getSender()) == null)
                {
                    if(db.checkLogin(msg.getSender(), msg.getContent()))
                    {
                        clients[findClient(ID)].setUsername(msg.getSender());
                        clients[findClient(ID)].setIp(msg.getIp());
                        clients[findClient(ID)].setPort(msg.getPort());
                        clients[findClient(ID)].send(new Messege("login", "SERVER", "TRUE", msg.getSender()));
                        Announce("newuser", "SERVER", msg.getSender(), msg.getIp(), msg.getPort());
                        SendUserList(msg.getSender());
                    }
                    else
                    {
                        clients[findClient(ID)].send(new Messege("login", "SERVER", "FALSE", msg.getSender()));
                    }
                }
                else
                {
                    clients[findClient(ID)].send(new Messege("login", "SERVER", "FALSE" ,msg.getSender()));
                }
            }
            else if(msg.getType().equals("test"))
            {
                clients[findClient(ID)].send(new Messege("test", "SERVER", "OK",  msg.getSender()));
            }
            else if(msg.getType().equals("signup"))
            {
                if(findUserThread(msg.getSender()) == null)
                {
                    if(!db.userExists(msg.getSender()))
                    {
                        db.addUser(msg.getSender(), msg.getContent());
                        clients[findClient(ID)].setUsername(msg.getSender());
                        clients[findClient(ID)].setIp(msg.getIp());
                        clients[findClient(ID)].setPort(msg.getPort());
                        clients[findClient(ID)].send(new Messege("signup", "SERVER", "TRUE", msg.getSender()));
                        clients[findClient(ID)].send(new Messege("login", "SERVER", "TRUE", msg.getSender()));
                        Announce("newuser", "SERVER", msg.getSender(), msg.getIp(), msg.getPort());
                        SendUserList(msg.getSender());
                    }
                    else
                    {
                        clients[findClient(ID)].send(new Messege("signup", "SERVER", "FALSE", msg.getSender()));
                    }
                }
                else
                {
                    clients[findClient(ID)].send(new Messege("signup", "SERVER", "FALSE", msg.getSender()));
                }
            }
        }
    }
    public void Announce(String type, String sender, String content, String ip, String port)
    {
        Messege msg = new Messege(type, sender, content, "All", ip, port);
        for(int i = 0; i < clientCount; i++)
        {
            clients[i].send(msg);
        }
    }
    public void Announce(String type, String sender, String content)
    {
        Messege msg = new Messege(type, sender, content, "All");
        for(int i = 0; i < clientCount; i++)
        {
            clients[i].send(msg);
        }
    }
    public void SendUserList(String toWhom)
    {
        for(int i = 0; i < clientCount; i++)
        {
            findUserThread(toWhom).send(new Messege("newuser", "SERVER", clients[i].getUsername(),  toWhom, clients[i].getIp(), clients[i].getPort()));
        }
    }

    public ServerThread findUserThread(String usr)
    {
        for(int i = 0; i < clientCount; i++)
        {
            if(clients[i].getUsername().equals(usr))
            {
                return clients[i];
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public synchronized void remove(int ID)
    {
        int pos = findClient(ID);
        if (pos >= 0)
        {
            ServerThread toTerminate = clients[pos];
            gui.getTextArea().appendText("\nRemoving client thread " + ID + " at " + pos);
            if (pos < clientCount-1)
            {
                for (int i = pos+1; i < clientCount; i++)
                {
                    clients[i-1] = clients[i];
                }
            }
            clientCount--;
            try
            {
                toTerminate.close();
            }
            catch(IOException ioe)
            {
                gui.getTextArea().appendText("\nError closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }
    private void addThread(Socket socket)
    {
        if(clientCount < clients.length)
        {
            gui.getTextArea().appendText("\nClient accepted: " + socket);
            clients[clientCount] = new ServerThread(this, socket);
            try
            {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            }
            catch (IOException ioe)
            {
                gui.getTextArea().appendText("\nError opening socket " + ioe);
            }
        }
        else
        {
            gui.getTextArea().appendText("\nClient refused : maximum " + clients.length + " reached");
        }
    }

    public Server_Controller getGui()
    {
        return gui;
    }
}
