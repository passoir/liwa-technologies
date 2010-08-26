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

public class CreateAssessmentTable {

    Configuration config;
    ConnectToDb db;

    public CreateAssessmentTable() {

    }

    public void createTable(String tableSpace, String tableNamePrefix) {
        config = Configuration.getInstance();
        db = config.getDbConnection();

        String prefix = tableSpace + "." + tableNamePrefix;

        String tableName = prefix + "assessment_history_table";

        db.execute( "drop table " + tableName );

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
  
        db.createTable(tableName, fieldNames, fieldTypes, new ArrayList<Integer>());
    
    }
    public void close(){
        db.close();
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
        CreateAssessmentTable cat = new CreateAssessmentTable();
        cat.createTable( tableSpace, tableNamePrefix );
    }

}

