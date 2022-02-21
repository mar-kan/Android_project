package com.example.myapplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CoordinatesFromCSV {
    public CoordinatesFromCSV() {
    }

    public ArrayList<String> coordinates = new ArrayList<>();
    String subStr;

    public ArrayList<String> getCoordinates(String xmlFilename,String csvFilename) throws Exception {

        XmlToCsv.XmlToCSVConvert(xmlFilename, csvFilename);
        Scanner sc = new Scanner(new File(csvFilename));

        while (sc.hasNext()) {
            subStr = sc.next();
            String[] splits = subStr.split(",");
            coordinates.add(splits[2]+","+splits[3]);
        }
        return coordinates;
    }
}