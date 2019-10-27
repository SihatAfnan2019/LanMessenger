package com.client;

import com.Messege.Messege;
import javafx.application.Platform;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TreeMap;

public class SocketClient implements Runnable
{
    private int port;
    private String server_address;
    private Socket socket;
    private Client_Controller gui;
    private ObjectInputStream In;
    private ObjectOutputStream Out;
    TreeMap<String, String> tm1;
    TreeMap<String, Integer> tm2;
    private Client_Server clientServer;
    private String sport;

    public SocketClient(Client_Controller gui) throws Exception
    {
        this.gui = gui;
        this.server_address = gui.getHostAdress();
        this.port = gui.getHostPort();
        socket = new Socket(InetAddress.getByName(server_address), port);
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        tm1 = new TreeMap<>();
        tm2 = new TreeMap<>();
        clientServer = new Client_Server( 0, gui, this);

        Thread t = new Thread( clientServer);
        t.start();

    }
    public SocketClient(String ip, int port) throws Exception
    {
        this.server_address = ip;
        this.port = port;
        socket = new Socket(InetAddress.getByName(server_address), port);
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
    }


    @Override
    public void run()
    {
        boolean KeepRunning = true;
        while(KeepRunning)
        {
            try
            {
                Messege msg = null;
                try
                {
                    msg = (Messege) In.readObject();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }

                if(msg.getType().equals("login"))
                {
                    if(msg.getContent().equals("TRUE"))
                    {

                        gui.getLog_in().setDisable(true);
                        gui.getSign_up().setDisable(true);
                        gui.getChat_box().appendText("[Server > Me] : Login Successful\n");
                        gui.playSound("a.mp3");
                        gui.Hide();
                        //gui.getSend_messege().setDisable(false);
                        gui.getPasswordtxt().setEditable(false);
                        gui.getUser_nametxt().setEditable(false);
//                        gui.getPrimaryStage().initStyle(StageStyle.UNDECORATED);
                        gui.getLog_out().setDisable(false);
                        gui.getBrowse_destination_folder().setDisable(false);

                    }
                    else
                    {

                        gui.getChat_box().appendText("[Server > Me] : Login Failed\n");
                        gui.playSound("b.mp3");


                    }
                }
                else if(msg.getType().equals("test"))
                {

                    gui.getConnect().setDisable(true);
                    gui.getChat_box().appendText("[Server > Me] : Connection Successful\n");
                    gui.playSound("a.mp3");
                    gui.getLog_in().setDisable(false);
                    gui.getSign_up().setDisable(false);
                    gui.getHost_addresstxt().setEditable(false);
                    gui.getHost_porttxt().setEditable(false);

                }
                else if(msg.getType().equals("newuser"))
                {

                    if(!msg.getContent().equals(gui.getUserName()))
                    {
                        boolean exists = false;
                        for(int i = 0; i < gui.getList().size(); i++)
                        {
                            if(gui.getList().get(i).equals(msg.getContent()))
                            {
                                exists = true;
                                break;
                            }

                        }
                        if(!exists)
                        {

                            gui.addList(msg.getContent());
                            tm1.put( msg.getContent(), msg.getIp());
                            tm2.put( msg.getContent(), Integer.parseInt(msg.getPort().trim()));

                        }
                    }
                }
                else if(msg.getType().equals("signup"))
                {
                    if(msg.getContent().equals("TRUE"))
                    {
                        gui.getLog_in().setDisable(true);
                        gui.getSign_up().setDisable(true);
                        gui.getChat_box().appendText("[Server > Me] : Signup Successful\n");
                        gui.Hide();
                       // gui.getSend_messege().setDisable(false);
                        //gui.getBrowse_file().setDisable(false);
                        gui.getPasswordtxt().setEditable(false);
                        gui.getUser_nametxt().setEditable(false);
                       // gui.getPrimaryStage().initStyle(StageStyle.UNDECORATED);
                        gui.getLog_out().setDisable(false);
                        gui.getBrowse_destination_folder().setDisable(false);
                    }
                    else
                    {
                        gui.getChat_box().appendText("[Server > Me] : Signup Failed\n");
                        gui.playSound("b.mp3");
                    }
                }
                else if(msg.getType().equals("signout"))
                {
                    if(msg.getContent().equals(gui.getUserName()))
                    {
                        gui.getChat_box().appendText("[" + msg.getSender() + " > Me] : Bye\n");
                        gui.getConnect().setDisable(false);
                        gui.getHost_addresstxt().setEditable(true);
                        gui.getHost_porttxt().setEditable(true);
                        //gui.getList().clear();
                        if(tm1.size() != 0)
                            tm1.clear();
                        if(tm2.size() != 0)
                           tm2.clear();
                    }
                    else
                    {
                        for(int i = 0; i < gui.getList().size(); i++)
                            if(gui.getList().get(i).equals(msg.getContent()))
                            {
                                gui.removeList(i);
                                tm1.remove(msg.getContent());
                                tm2.remove(msg.getContent());
                            }

                        gui.getChat_box().appendText("[" + msg.getSender() + "> All] : " + msg.getContent() + " has signed out\n");
                    }
                }
                else
                {
                    gui.getChat_box().appendText("Unknown messege send by " + msg.getSender());
                }

            }
            catch (Exception e)
            {
                KeepRunning = false;
                //gui.getChat_box().appendText(c);
                System.out.println("[Application > Me] : Connection Failure\n");
                gui.getConnect().setDisable(false);
                gui.getHost_addresstxt().setEditable(true);
                gui.getHost_porttxt().setEditable(true);
                gui.getSign_up().setDisable(true);
                gui.getLog_in().setDisable(true);
                System.out.println("Exception SocketClient run()");
                e.printStackTrace();
            }
        }

    }
    public void send(Messege msg)
    {
        try
        {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : " + msg.getContent());
        }
        catch(IOException e)
        {
            System.out.println("Exception SocketClient send()");
        }
    }
    public void close() throws IOException
    {
        socket.close();
        In.close();
        Out.close();
    }
    public void closeThread(Thread t)
    {
        t = null;
    }
    public TreeMap<String, String> getTm1()
    {
        return tm1;
    }
    public TreeMap<String, Integer> getTm2()
    {
        return tm2;
    }
    public void setSport(String sport)
    {
        this.sport = sport;
    }
    public String getSport()
    {
        return sport;
    }
}
