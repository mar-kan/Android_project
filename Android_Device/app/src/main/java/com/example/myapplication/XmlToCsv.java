package com.example.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class XmlToCsv {
    static public void XmlToCSVConvert(String xmlFilename,String csvFilename) throws Exception{
        File inputFile = new File(xmlFilename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList timestepList = doc.getElementsByTagName("timestep");

        FileWriter writer = new FileWriter(new File(csvFilename));

        for (int i = 0; i < timestepList.getLength(); i++) {
            Node timestepNode = timestepList.item(i);
            if (timestepNode.getNodeType() == Node.ELEMENT_NODE) {
                Element timestepElement = (Element) timestepNode;
                NodeList vehicleList = timestepElement.getElementsByTagName("vehicle");
                for (int j = 0; j < vehicleList.getLength(); j++) {
                    Node vehicleNode = vehicleList.item(j);
                    if (vehicleNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element vehicleElement = (Element) vehicleNode;
                        String newLine = (timestepElement.getAttribute("time")+","+
                                vehicleElement.getAttribute("id")+","+
                                vehicleElement.getAttribute("x")+","+
                                vehicleElement.getAttribute("y")+","+
                                vehicleElement.getAttribute("angle")+","+
                                vehicleElement.getAttribute("type")+","+
                                vehicleElement.getAttribute("speed")+","+
                                vehicleElement.getAttribute("pos")+","+
                                vehicleElement.getAttribute("lane")+","+
                                vehicleElement.getAttribute("slope")+"\n");
                        writer.append(newLine);
                    }
                }
            }
        }
        writer.flush();
        writer.close();
    }
}