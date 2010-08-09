package de.l3s.liwa.assessment.db;

import java.util.List;
import junit.framework.*;
import junit.extensions.*;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TestLoadCsv extends TestCase {

    public TestLoadCsv(String name) {
        super(name);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {
    }

    public static Test suite() {
        return new TestSuite(TestLoadCsv.class);
    }

    public void testParse() {
        try {
            ConnectToDb db;
            db = new ConnectToDb("nessi3:1521/mine","liwa","eic7cao","localhost:1521/mine",false);

            db.execute("drop table liwa.test_host_table");

            LoadCsv loader = new LoadCsv(db);

            String[] fieldNames = new String[]{
                "MainURL"};
            String[] fieldTypes = new String[]{
                "VARCHAR2(4000 BYTE)"
            };
            String tableName = "liwa.test_host_table";
            //        loader.loadCsvData("tmp_data/host_table.csv", fieldNames, fieldTypes, tableName, true);
            loader.createLoadCsvWithIdGen("tmp_data/host_table.csv", fieldNames, fieldTypes, tableName, false, "hostID");

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
