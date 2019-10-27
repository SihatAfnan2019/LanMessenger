package com.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Download implements Runnable{
    
    private ServerSocket server;
    private Socket socket;
    private int port;
    private String saveTo = "";
    private InputStream In;
    private FileOutputStream Out;
    private Client_Controller gui;
    private String sender;
    
    public Download(String saveTo, Client_Controller gui, String sender){
        try
        {
            server = new ServerSocket(0);
            port = server.getLocalPort();
            this.saveTo = saveTo;
            this.gui = gui;
            this.sender = sender;
        } 
        catch (IOException ex)
        {
            System.out.println("Exception [Download : Download(...)]");
        }
    }

    @Override
    public void run()
    {
        try
        {
            socket = server.accept();
            System.out.println("Download : "+socket.getRemoteSocketAddress());
            In = socket.getInputStream();
            System.out.println(saveTo);
            File fp = new File(saveTo);
            if(!fp.exists())
                fp.createNewFile();
            System.out.println("time has come");
            Out = new FileOutputStream(fp);
            System.out.println(saveTo);
            byte[] buffer = new byte[1024];
            int count;
            while((count = In.read(buffer)) >= 0){
                Out.write(buffer, 0, count);
            }
            Out.flush();
            System.out.println("[Application > Me] : Download complete\n");
            gui.getChat_box().appendText("[Application > Me] : File recieved from " + sender + "\n");
            if(Out != null){ Out.close(); }
            if(In != null){ In.close(); }
            if(socket != null){ socket.close(); }
        } 
        catch (Exception ex)
        {
            System.out.println("Exception [Download : run(...)]");
        }
    }

   public int getPort()
   {
       return port;
   }
}