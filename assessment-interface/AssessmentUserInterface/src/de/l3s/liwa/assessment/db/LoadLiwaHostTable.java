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

public class LoadLiwaHostTable {

    ConnectToDb db;
    Configuration config;
    String hostTable;

    public LoadLiwaHostTable(String hostTable) {
        this.hostTable = hostTable;
    }

    public void loadTable(String tableSpace, String tableNamePrefix) {
        config = Configuration.getInstance();
        db = config.getDbConnection();

        String prefix = tableSpace + "." + tableNamePrefix;
        loadHosts(prefix + "host_table", hostTable);        
    }

    public void close() {
        db.close();
    }

    protected void loadHosts( String tableName, String fileName ) {
        LoadCsv loader = new LoadCsv(db);

        db.execute( "drop table " + tableName );

        int offset = 2; // how many columns of data on top of attributes: two (MainUrl; language)
        List<HostAttributeType> attributes = config.getHostAttributeTypes();

        String[] fieldNames = new String[attributes.size() + offset];
        fieldNames[0] = "MainURL";
        fieldNames[1] = "Language";

        String[] fieldTypes = new String[attributes.size() + offset];
        fieldTypes[0] = "VARCHAR2(2000 BYTE)";
        fieldTypes[1] = "VARCHAR2(20 BYTE)";

        for(int i=0; i<attributes.size(); ++i){
            fieldNames[i + offset] = attributes.get(i).getName();
            switch (attributes.get(i).getType() ) {
            case STRING : 
                fieldTypes[i + offset] = "VARCHAR2(2000 BYTE)";
                break;
            case INT : 
                fieldTypes[i + offset] = "INTEGER";
                break;
            case DOUBLE : 
                fieldTypes[i + offset] = "NUMBER";
                break;
            case URL : 
                fieldTypes[i + offset] = "VARCHAR2(2000 BYTE)";
                break;
            default:
                System.err.println("Error HostAttributeType "+attributes.get(i).getType()+" is not known");
            }
        }
        //        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, true);
        loader.createLoadCsvWithIdGen(fileName, fieldNames, fieldTypes, tableName, false, "HostID");
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
        LoadLiwaHostTable load = new LoadLiwaHostTable(path);
        load.loadTable( tableSpace, tableNamePrefix );
    }

}
