package com.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class Database
{
    private String filepath;

    public Database(String filepath)
    {
        this.filepath = filepath;
    }
    public boolean userExists(String username)
    {
        try
        {
            File fXmlFIle = new File(filepath);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFIle);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");

            for(int temp = 0; temp < nList.getLength(); temp++)
            {
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    if(getTagValue("username", eElement).equals(username))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        catch(Exception ex)
        {
            System.out.println("Database Exception : userExists()");
            return false;
        }
    }

    public boolean checkLogin(String username, String password){

        if(!userExists(username)){ return false; }

        try{
            File fXmlFile = new File(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(getTagValue("username", eElement).equals(username) && getTagValue("password", eElement).equals(password)){
                        return true;
                    }
                }
            }
            System.out.println("Hippie");
            return false;
        }
        catch(Exception ex){
            System.out.println("Database exception : userExists()");
            return false;
        }
    }

    public void addUser(String username, String password){

        try {
            System.out.println("000000");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            Node data = doc.getFirstChild();

            Element newuser = doc.createElement("user");
            Element newusername = doc.createElement("username");
            newusername.setTextContent(username);
            Element newpassword = doc.createElement("password");
            newpassword.setTextContent(password);

            newuser.appendChild(newusername);
            newuser.appendChild(newpassword);
            data.appendChild(newuser);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);

        }
        catch(Exception ex){
            System.out.println("Exceptionmodify xml");
        }
    }

    public static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }
}
