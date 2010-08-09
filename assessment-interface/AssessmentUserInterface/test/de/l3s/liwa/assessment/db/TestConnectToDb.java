package de.l3s.liwa.assessment.db;

import java.util.List;
import junit.framework.*;
import junit.extensions.*;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TestConnectToDb extends TestCase {

    public TestConnectToDb(String name) {
        super(name);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {
    }

    public static Test suite() {
        return new TestSuite(TestConnectToDb.class);
    }

    public void testParse() {
        ConnectToDb db;
        db = new ConnectToDb("nessi3:1521/mine","liwa","eic7cao","localhost:1521/mine",false);
        db.execute("select 2,3,4 from dual");

        ResultSet rs = db.exec("select 2,3,4 from dual union select 22,23,24 from dual").getResult();
        try {
            while (rs.next()){
                System.out.println( rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3)  );
            }        
        } catch (SQLException se){
            se.printStackTrace();
        }
        db.close();
    }

}
