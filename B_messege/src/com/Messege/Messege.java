package com.Messege;

import java.io.Serializable;

public class Messege implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String type, sender, content, recipient, ip, port, fileName;

    public Messege( String type, String sender,String content, String recipient, String fileName)
    {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
        this.fileName = fileName;
    }

    public Messege( String type, String sender, String content, String recipient)
    {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
    }

    public Messege( String type, String sender , String content, String recipient,  String ip, String port)
    {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
        this.ip = ip;
        this.port = port;
    }

    public String getType()
    {
        return type;
    }
    public String getSender()
    {
        return sender;
    }
    public String getIp(){ return ip;}
    public String getContent()
    {
        return content;
    }
    public String getRecipient()
    {
        return recipient;
    }
    public String getPort(){ return port;}
    public String getFileName() { return fileName; }
    @Override
    public String toString()
    {
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"'}";
    }
}
