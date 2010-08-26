package de.l3s.liwa.assessment;

import java.util.List;
import junit.framework.*;
import junit.extensions.*;

public class TestCsvReader extends TestCase {

    public TestCsvReader(String name) {
        super(name);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {
    }

    public static Test suite() {
        return new TestSuite(TestCsvReader.class);
    }

    protected void readFile(String fileName, char separator){
        CsvReader reader = null;
        try {
            reader = new CsvReader(fileName, separator );
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

    public void testReadDefaultSep() {
        readFile("AssessmentUserInterface/test/de/l3s/liwa/assessment/test.csv", ',' );
    }

    public void testRead() {
        readFile("AssessmentUserInterface/test/de/l3s/liwa/assessment/test.csv", 'a' );
    }

}
