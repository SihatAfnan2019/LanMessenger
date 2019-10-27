package com.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Upload implements Runnable{

    private String addr;
    private int port;
    private Socket socket;
    private FileInputStream In;
    private OutputStream Out;
    private File file;
    private Client_Controller gui;
    private String name;
    
    public Upload(String addr, int port, File filepath, Client_Controller gui, String name){
        super();
        try
        {
            file = filepath;
            this.addr = addr;
            this.port = port;
            System.out.println(addr + " " + this.port);
            socket = new Socket( InetAddress.getByName(addr), this.port);
            System.out.println("bravo");
            Out = socket.getOutputStream();
            In = new FileInputStream(filepath);
            this.gui = gui;
            this.name = name;
        } 
        catch (Exception ex)
        {
            System.out.println("Exception [Upload : Upload(...)]");
        }
    }
    
    @Override
    public void run() {
        try {       
            byte[] buffer = new byte[1024];
            int count;
            
            while((count = In.read(buffer)) >= 0)
            {
                Out.write(buffer, 0, count);
            }
            Out.flush();

            System.out.println("[Applcation > Me] : File upload complete\n");
            gui.getChat_box().appendText("[Applcation > Me] : File sucessfully sent to " + name + "\n");
            if(In != null){ In.close(); }
            if(Out != null){ Out.close(); }
            if(socket != null){ socket.close(); }
        }
        catch (Exception ex) {
            System.out.println("Exception [Upload : run()]");
            ex.printStackTrace();
        }
    }

}