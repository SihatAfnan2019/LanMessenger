package com.client;

import com.Messege.Messege;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client_Server implements Runnable
{
    private int port ;
    private Client_Controller gui;
    private String username;
    private SocketClient tempClient, socketClient;
    private Thread tempClientThread;
   public Client_Server(int port, Client_Controller gui, SocketClient socketClient)
   {
       this.port = port;
       this.gui = gui;
       this.username = gui.getUserName();
       System.out.println(gui.getUserName());
       this.socketClient = socketClient;
   }
   public void begin()
   {
       try
       {
           ServerSocket serversocket = new ServerSocket(port);
           //socketClient.setSport(Integer.toString(serversocket.getLocalPort()));
           socketClient.setSport(serversocket.getLocalPort()+"");
           while(true)
           {
               System.out.println("About to accept a client connection...");
               Socket clientsocket = serversocket.accept();
               System.out.println("Accepted connection from" + clientsocket);
               Client_ServerThread worker = new Client_ServerThread(clientsocket, gui, this);
               worker.start();
           }
       }
       catch(IOException e)
       {
           e.printStackTrace();
       }
   }

    @Override
    public void run()
    {
        begin();
    }
    public synchronized void handle(Messege msg) throws IOException {
        if (msg.getType().equals("messege"))
        {
            gui.playSound("plucky.mp3");
            System.out.println(msg.getRecipient());
            System.out.println(gui.getUserName());
            if((msg.getRecipient()).equals(gui.getUserName()))
               gui.getChat_box().appendText("[" + msg.getSender() + " > Me] : " + msg.getContent() + "\n" );
            else
            {
                gui.getChat_box().appendText("[" + msg.getSender() + " > " + msg.getRecipient() + "] : " + msg.getContent() + "\n" );
            }
        }
        else if(msg.getType().equals("upload_req"))
        {

           if(gui.getDirectoryFilePath() != null )
           {
               String saveTo;
               if(gui.getDirectoryFilePath().length() == 3)
               {
                   System.out.println("hurrah");
                   Boolean fl = new File(gui.getDirectoryFilePath() + "B_ChATDownloads").mkdir();
                   saveTo = gui.getDirectoryFilePath() + "B_ChATDownloads\\" + msg.getContent();
               }

               else
               {
                   //Boolean fl = new File(gui.getDirectoryFilePath() + "B_ChATDownloads").mkdir();
                   saveTo = gui.getDirectoryFilePath() + "\\" + msg.getContent();

               }
               System.out.println(saveTo);
              Download dwn = new Download(saveTo, gui, msg.getSender());
              Thread t = new Thread(dwn);
              t.start();
               try
               {
                   tempClient = new SocketClient(socketClient.getTm1().get(msg.getSender()), socketClient.getTm2().get(msg.getSender()));
                   tempClientThread = new Thread(tempClient);
                   tempClientThread.start();
                   tempClient.send( new Messege( "upload_res", gui.getUserName(), (""+dwn.getPort()), msg.getSender()));
               }
               catch (Exception e)
               {
                   gui.getChat_box().appendText("[Application > Me] : ClientServer " + msg.getSender() + " not found\n");
               }
               tempClient.close();
               tempClient.closeThread(tempClientThread);
           }
           else
           {
               try
               {
                   tempClient = new SocketClient(socketClient.getTm1().get(msg.getSender()), socketClient.getTm2().get(msg.getSender()));
                   tempClientThread = new Thread(tempClient);
                   tempClientThread.start();
                   tempClient.send( new Messege( "upload_res", gui.getUserName(), "NO", msg.getSender()));
               }
               catch (Exception e)
               {
                   gui.getChat_box().appendText("[Application > Me] : ClientServer " + msg.getSender() + " not found\n");
               }
           }
        }
       else if(msg.getType().equals("upload_res"))
       {
          if(!msg.getContent().equals("NO"))
          {
              int port  = Integer.parseInt(msg.getContent().trim());
              String addr = socketClient.getTm1().get(msg.getSender());
              System.out.println("hello");
              System.out.println(msg.getSender() + " " + msg.getSender());
              Upload upl = new Upload( addr, port, new File(gui.getFilepath()), gui, msg.getSender());
              Thread t = new Thread(upl);
              t.start();
          }
          else
          {
               System.out.println("[Server > Me] : " + msg.getSender() + " rejected file request\n");
          }
       }

    }

}
