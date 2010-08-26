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

public class LoadLiwaTables {

    Configuration config;
    ConnectToDb db;

    public LoadLiwaTables() {

    }

    String userTable;
    String hostTable;
    String hostLinkTable;
    String hostPagesTable;
    String aHistoryTable; // assessment history table: contains assigned label&scores

    public void setTables( String userTable, String hostTable,  
                           String hostLinkTable, String hostPagesTable,
                           String assessmentHistoryTable) {
        this.userTable = userTable;
        this.hostTable =  hostTable;
        this.hostLinkTable =  hostLinkTable;
        this.hostPagesTable =  hostPagesTable;
        this.aHistoryTable = assessmentHistoryTable;
    }

    public void setTables( String hostLinkTable, String hostPagesTable) {
        this.hostLinkTable =  hostLinkTable;
        this.hostPagesTable =  hostPagesTable;
    }


    public void loadTables(String tableSpace, String tableNamePrefix) {
        config = Configuration.getInstance();
        //db = new ConnectToDb("nessi3:1521/mine","liwa","eic7cao","localhost:1521/mine",false);
        db = config.getDbConnection();

        String prefix = tableSpace + "." + tableNamePrefix;

        //        loadHistory(prefix + "assessment_history_table", aHistoryTable);

        //        loadHosts(prefix + "host_table", hostTable);

        //appendHostLinks(prefix + "host_link_table", hostLinkTable);

        //appendHostPages(prefix + "host_pages_table", hostPagesTable);

        //        loadUsers(prefix + "user_table", userTable);        
    }

    public void close(){
        db.close();
    }

    protected void loadHistory(String tableName, String fileName) {
        LoadCsv loader = new LoadCsv(db);

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
  
        //        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, true);
        loader.createLoadCsv(fileName, fieldNames, fieldTypes, tableName, false);
    }

    protected void loadHosts( String tableName, String fileName ) {
        LoadCsv loader = new LoadCsv(db);

        db.execute( "drop table " + tableName );

        int offset = 1; // how many columns of data on top of attributes: one (MainUrl)
        List<HostAttributeType> attributes = config.getHostAttributeTypes();

        String[] fieldNames = new String[attributes.size() + offset];
        fieldNames[0] = "MainURL";

        String[] fieldTypes = new String[attributes.size() + offset];
        fieldTypes[0] = "VARCHAR2(2000 BYTE)";

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

    protected void loadHostLinks( String tableName, String fileName ){
        LoadCsv loader = new LoadCsv(db);

        db.execute("drop table "+tableName);

        String[] fieldNames = new String[]{
            "FromHostURL",
            "ToHostURL",
            "NumOfLinks"
        };
        String[] fieldTypes = new String[]{
            "VARCHAR2(2000 BYTE)",
            "VARCHAR2(2000 BYTE)",
            "INTEGER"
        };  

        //        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, true);
        loader.createLoadCsv(fileName, fieldNames, fieldTypes, tableName, false);
    }

    protected void appendHostLinks(String tableName, String fileName) {
        LoadCsv loader = new LoadCsv(db);
        String[] fieldNames = new String[]{
            "FromHostURL",
            "ToHostURL",
            "NumOfLinks"
        };
        String[] fieldTypes = new String[]{
            "VARCHAR2(2000 BYTE)",
            "VARCHAR2(2000 BYTE)",
            "INTEGER"
        };  
        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, false, null);

    }

    protected void loadHostPages( String tableName, String fileName ){
        LoadCsv loader = new LoadCsv(db);

        db.execute("drop table "+tableName);

        String[] fieldNames = new String[]{
            "HostURL",
            "PageURL"
        };
        String[] fieldTypes = new String[]{
            "VARCHAR2(2000 BYTE)",
            "VARCHAR2(2000 BYTE)"
        };  

        //        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, true);
        loader.createLoadCsv(fileName, fieldNames, fieldTypes, tableName, false);
    }

    protected void appendHostPages( String tableName, String fileName ){
        LoadCsv loader = new LoadCsv(db);

        String[] fieldNames = new String[]{
            "HostURL",
            "PageURL"
        };
        String[] fieldTypes = new String[]{
            "VARCHAR2(2000 BYTE)",
            "VARCHAR2(2000 BYTE)"
        };  

        loader.loadCsvData(fileName, fieldNames, fieldTypes, tableName, false, null);
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
        LoadLiwaTables loadLiwaTables = new LoadLiwaTables();
        loadLiwaTables.setTables( //path+"/user_table.csv",  
                                  //path+"/host_table.csv", 
                                  path+"/endlinks.csv", 
                                  path+"/end_host_pages.csv"//, 
                                  //path+"/a_history_table.csv"
                                  );
        loadLiwaTables.loadTables( tableSpace, tableNamePrefix );
    }

}
