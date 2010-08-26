package de.l3s.liwa.assessment.db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import de.l3s.liwa.assessment.CsvReader;
import de.l3s.liwa.assessment.Configuration;
import de.l3s.liwa.assessment.HostAttributeType;
import de.l3s.liwa.assessment.LabelSet;

public class LoadLiwaUserTable {

    ConnectToDb db;
    Configuration config;
    String userTable;

    public LoadLiwaUserTable(String userTable) {
        this.userTable = userTable;
    }

    public void loadTable(String tableSpace, String tableNamePrefix) {
        config = Configuration.getInstance();
        db = config.getDbConnection();

        String prefix = tableSpace + "." + tableNamePrefix;
        loadUsers(prefix + "user_table", userTable);        
    }

    public void close() {
        db.close();
    }

    protected void loadUsers( String tableName, String fileName ) {
        LoadCsv loader = new LoadCsv(db);

        db.execute("drop table "+tableName);

        String[] fieldNames = new String[]{
            "UserID",
            "UserName",
            "Password",
            "Language"
        };
        String[] fieldTypes = new String[]{
            "INTEGER",
            "VARCHAR2(500 BYTE)",
            "VARCHAR2(500 BYTE)",
            "VARCHAR2(100 BYTE)"
        };  

        //        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, true);
        loader.createLoadCsv(fileName, fieldNames, fieldTypes, tableName, false);
    }

    public static void main (String[] args) {
        //        java -cp `ls ./AssessmentUserInterface/WebContent//../lib/*.jar | awk 'NR==1{printf $1} NR>=2{printf":"$i}'`:tmp_build/liwa/WEB-INF/classes/ de.l3s.liwa.assessment.db.LoadLiwaTables liwa test_ tmp_data
        /*
        String tableSapce= "liwa";
        String tableNamePrefix = "test_"; 
        String path = "tmp_data2";
        */
        String tableSpace= args[0];
        String tableNamePrefix = args[1]; 
        String path = args[2];
        LoadLiwaUserTable load = new LoadLiwaUserTable(path);
        load.loadTable( tableSpace, tableNamePrefix );
    }

}
