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

public class LoadUsersListTable {

    ConnectToDb db;
    Configuration config;
    String fileName;

    public LoadUsersListTable(String file) {
        fileName = file;
    }

    public void loadTable(String tableSpace, String tableNamePrefix) {
        config = Configuration.getInstance();
        db = config.getDbConnection();

        String prefix = tableSpace + "." + tableNamePrefix;
        loadData(prefix + "users_list_table", fileName);        
    }

    public void close() {
        db.close();
    }

    protected void loadData( String tableName, String fileName ) {
        LoadCsv loader = new LoadCsv(db);

        db.execute("drop table "+tableName);

        String[] fieldNames = new String[]{
            "UserID",
            "HostURL",
            "Assessed"
        };
        String[] fieldTypes = new String[]{
            "INTEGER",
            "VARCHAR2(2000 BYTE)",
            "INTEGER"
        };  

        loader.createLoadCsvWithIdGen(fileName, fieldNames, fieldTypes, tableName, false, "fifo_counter");
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
        LoadUsersListTable load = new LoadUsersListTable(path);
        load.loadTable( tableSpace, tableNamePrefix );
    }

}
