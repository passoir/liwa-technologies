package de.l3s.liwa.assessment;

import java.io.*;
import java.util.*;

public class CsvReader {
    private String fileName;
    private char separator;
    private BufferedReader input;
    private String line;
    private CSV csv;
    private boolean hasNext;

    public CsvReader(String _fileName, char _separator) throws IOException {
        fileName = _fileName;
        separator = _separator;
        csv = new CSV(separator);
        input = new BufferedReader( new FileReader(fileName) );
        readNextLineFromFile();
    }

    private void readNextLineFromFile() throws IOException {
        line = input.readLine();
        if(null==line){
            hasNext = false;
        } else {
            // skip lines starting with "#"
            if (line.startsWith("#")) {
                readNextLineFromFile();
            } else {
                hasNext = true;
            }
        }
    }

    public boolean hasNext(){
        return hasNext;
    }

    public ArrayList<String> next() throws IOException{
        ArrayList<String> result = new ArrayList<String>();
        Iterator e = csv.parse(line).iterator();
        int i = 0;
        while (e.hasNext()) {
            result.add( (String) e.next() );
        }
        readNextLineFromFile();
        return result;
    }

    public static void main(String[] args){
        CsvReader reader = null;
        try {
            reader = new CsvReader(args[0], args[1].charAt(0) );
        } catch (Exception e){
            e.printStackTrace();
        }

        while( reader.hasNext() ){
            List<String> line = null;
            try {
                line = reader.next();
            } catch (Exception e){
                e.printStackTrace();
            }
            for(String s : line){
                System.out.print("|" + s);
            }
            System.out.println("");
        }

    }

}