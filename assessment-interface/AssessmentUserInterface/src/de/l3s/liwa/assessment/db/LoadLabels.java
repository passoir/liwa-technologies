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

public class LoadLabels {

    ConnectToDb db;
    Configuration config;
    String labelTable;

    public LoadLabels(String labelTable) {
        this.labelTable = labelTable;
    }

    public void loadTable(String tableSpace, String tableNamePrefix) {
        config = Configuration.getInstance();
        db = config.getDbConnection();

        String prefix = tableSpace + "." + tableNamePrefix;
        loadNewLabels(prefix + "assessment_history_table", labelTable);        
    }

    public void close() {
        db.close();
    }

    protected void loadNewLabels( String tableName, String fileName ) {
        LoadCsv loader = new LoadCsv(db);

        ArrayList<String> fieldNameList = new ArrayList<String>();
        fieldNameList.add("UserID");
        fieldNameList.add("HostURL");
        fieldNameList.add("Date");
        fieldNameList.add("Comment");
        fieldNameList.add("IsLast");

        ArrayList<String> fieldTypeList = new ArrayList<String>();
        fieldTypeList.add("INTEGER");
        fieldTypeList.add("VARCHAR2(2000 BYTE)");
        fieldTypeList.add("DATE");
        fieldTypeList.add("VARCHAR2(2000 BYTE)");
        fieldTypeList.add("INTEGER");
        
        // get other field names from config
        List<LabelSet> sets = config.getLabelSets();
        for (LabelSet ls : sets) {
            fieldNameList.add(ls.getName());
            // fill types in parallel
            fieldTypeList.add("VARCHAR2(50 BYTE)");
        }

        String[] fieldNames = (String[])fieldNameList.toArray(new String[]{});
        String[] fieldTypes = (String[])fieldTypeList.toArray(new String[]{});

        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, true, null);
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
        LoadLabels load = new LoadLabels(path);
        load.loadTable( tableSpace, tableNamePrefix );
    }

}
