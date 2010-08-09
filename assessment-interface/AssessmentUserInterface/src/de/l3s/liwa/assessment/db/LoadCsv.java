package de.l3s.liwa.assessment.db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import de.l3s.liwa.assessment.CsvReader;

public class LoadCsv {

    protected ConnectToDb db;
    protected boolean outerDb;
    protected Boolean isTest;

    /*
      public LoadCsv() {
      db = new ConnectToDb(new DbConfig());
      outerDb = false;
      }
    */
    public LoadCsv(ConnectToDb _db) {
        db = _db;
        outerDb = true;
        isTest = false;
    }

    protected void createIdSequence(String seqName) {
        db.execute("DROP SEQUENCE " + seqName);
        db.execute("CREATE SEQUENCE " + seqName + " start with 1 nomaxvalue ");
    }

    public void setIsTest(boolean _isTest) {
        isTest = _isTest;
    }

    public void createTable (String[] fieldNames, String[] fieldTypes, String tableName) {
        db.deleteTable(tableName);
        db.createTable(tableName, fieldNames, fieldTypes);
    }
    
    public void createLoadCsvWithIdGen(String csvFileName, String[] fieldNames, String[] fieldTypes,
                                       String tableName, boolean ignoreHeader, String idName) {
        createIdSequence(idName + "_seq");
        db.createTableWithIdGen(tableName, fieldNames, fieldTypes, idName);
        loadCsvData(csvFileName, fieldNames, fieldTypes, tableName, ignoreHeader, idName);
    }

    public void delCreateLoadCsv(String csvFileName, String[] fieldNames, String[] fieldTypes,
                                 String tableName, boolean ignoreHeader) {
        db.deleteTable(tableName);
        createLoadCsv(csvFileName, fieldNames, fieldTypes, tableName, ignoreHeader);
    }

    public void delCreateLoadCsv(String csvFileName, String[] fieldNames, String[] fieldTypes,
                                 String tableName, boolean ignoreHeader, int pk) {
        db.deleteTable(tableName);
        createLoadCsv(csvFileName, fieldNames, fieldTypes, tableName, ignoreHeader, pk);
    }

    public void createLoadCsv(String csvFileName, String[] fieldNames, String[] fieldTypes,
                              String tableName, boolean ignoreHeader) {
        db.createTable(tableName, fieldNames, fieldTypes);
        loadCsvData(csvFileName, fieldNames, fieldTypes, tableName, ignoreHeader, null);
    }

    public void createLoadCsv(String csvFileName, String[] fieldNames, String[] fieldTypes,
                              String tableName, boolean ignoreHeader, int pk) {
        ArrayList<Integer> v = new ArrayList<Integer>();
        v.add(pk);
        db.createTable(tableName, fieldNames, fieldTypes, v);
        loadCsvData(csvFileName, fieldNames, fieldTypes, tableName, ignoreHeader, null);
    }

    public void loadCsvData(String csvFileName, String[] fieldNames, String[] fieldTypes, 
                            String tableName, boolean ignoreHeader, String idName) {

        int fieldNum;
        if (fieldTypes.length < fieldNames.length) {
            fieldNum = fieldTypes.length;
        } else {
            fieldNum = fieldNames.length;
        }

        try {
            CsvReader reader = new CsvReader(csvFileName, ';');
            ArrayList<String> nextLine;
            String insertString = "INSERT INTO " + tableName + " VALUES (";
            if (idName != null) {
                insertString += " " + idName + "_seq.nextval , ";
            }

            for (int i = 0; i < fieldNum - 1; ++i) {
                insertString += "?, ";
            }
            insertString += "?)";

            PreparedStatement insert = db.getConnection().prepareStatement(insertString);
            int counter = 0;
            //SimpleDateFormat dFormat = new SimpleDateFormat("yyyy.MM.dd");

            SimpleDateFormat dFormat = new SimpleDateFormat("dd-MMM-yy");
            if (ignoreHeader) {
                nextLine = reader.next();
            }
            while ( reader.hasNext() ) {
                nextLine = reader.next();
                if (nextLine.size() <= 1) { // skip empty lines
                    continue;
                }
                //		    String tmp="";
                for (int i = 0; i < fieldNum; ++i) {
                    //			tmp+=nextLine.get(i) + ", ";

                    try {

                        if (fieldTypes[i].contains("VARCHAR2(")) {
                            insert.setString(i + 1, nextLine.get(i));
                        }

                        if (fieldTypes[i].equals("INTEGER")) {
                            if (!nextLine.get(i).equals("")) {
                                int tmp_int = 0;
                                try {
                                    tmp_int = Integer.valueOf(nextLine.get(i).trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("in table " + tableName +
                                                       " field " + (i + 1) + " is not integer:|" + nextLine.get(i)+"|");
                                }
                                insert.setInt(i + 1, tmp_int);
                            } else {
                                insert.setNull(i + 1, java.sql.Types.INTEGER);
                            }
                        }
                        
                        if (fieldTypes[i].equals("NUMBER")) {
                            if (!nextLine.get(i).equals("")) {
                                Float f = (float) 0;
                                try {
                                    f = Float.valueOf(nextLine.get(i));
                                } catch (NumberFormatException e) {
                                    System.out.println("in table " + tableName +
                                                       " field " + (i + 1) + " is not float:" + nextLine.get(i));
                                }
                                insert.setFloat(i + 1, f);
                            } else {
                                insert.setNull(i + 1, java.sql.Types.FLOAT);
                            }
                        }
                        
                        
                        if (fieldTypes[i].equals("DATE")) {
                            if (!nextLine.get(i).equals("")) {
                                java.sql.Date dt = null;
                                dt = java.sql.Date.valueOf("1234-05-06");
                                try {
                                    String dateString = nextLine.get(i);
                                    String tmp_str;
                                    dateString = dateString.substring(0, 10) + ".";

                                    if (-1 == dateString.indexOf('.')) {
                                        tmp_str = dateString.substring(0, 4) + "-" +
                                            dateString.substring(4, 6) + "-" + dateString.substring(6, 8);
                                    } else {

                                        if ('.' == dateString.toCharArray()[dateString.length() - 1]) {
                                            dateString = dateString.substring(0, dateString.length() - 1);
                                        }
                                        tmp_str = dateString.replace('.', '-');
                                    }

                                    dt = java.sql.Date.valueOf(tmp_str);
                                } catch (Exception e) {
                                    System.out.println("in table " + tableName +
                                                       " field " + (i + 1) + " is not date:" + nextLine.get(i));
                                }
                                insert.setDate(i + 1, dt);
                            } else {
                                insert.setNull(i + 1, java.sql.Types.DATE);
                            }
                        }
                        


                        // for dates represented as seconds (long)
                        /*if (fieldTypes[i].equals("DATE")) {
                          if (!nextLine.get(i).equals("")) {                                                                        
                          long tmp_long = 0;
                          try {
                          tmp_long = Long.valueOf(nextLine.get(i));
                          } catch (NumberFormatException e) {
                          System.out.println("in table " + tableName +
                          " field " + (i + 1) + " is not long:" + nextLine.get(i));
                          }
                          java.sql.Time dt = new java.sql.Time(tmp_long*1000);
                          insert.setTime(i + 1, dt);
                          } else {
                          insert.setNull(i + 1, java.sql.Types.DATE);
                          } 
                          } */                   

                    } catch (SQLException se) {
                        System.err.println("ConnectToDb: We got an exception while setString");
                        se.printStackTrace();
                        System.exit(1);
                    }
                }

                ++counter;

                if ((isTest) && (counter > 500)) {
                    break;
                }

                if (counter % 1000 == 0) {
                    System.out.println("" + counter);
                }
                try {
                    insert.executeUpdate();
                } catch (SQLException se) {
                    System.err.println("ConnectToDb: We got an exception while executeUpdate()");
                    se.printStackTrace();
                    System.exit(1);
                }
            }

        } catch (Exception se) {
            System.err.println("PreProc: We got an exception while prepareStatement()");
            se.printStackTrace();
        }

    }


    public static void main(String[] args) {
        ConnectToDb db;
        db = new ConnectToDb("nessi3:1521/mine","liwa","eic7cao","localhost:1521/mine",false);

        LoadCsv loader = new LoadCsv(db);

        //  public void loadCsvData(String csvFileName, String[] fieldNames, String[] fieldTypes, String tableName, boolean ignoreHeader);
        String[] fieldNames = new String[] {
            "MainURL",
            "MaxPrPageURL",
            "MaxPrPageOutLinks",
            "MaxPrPageInLinks",
            "MaxPageRank",
            "Misc"};
        String[] fieldTypes = new String[] {
            "VARCHAR2(4000 BYTE)",
            "VARCHAR2(4000 BYTE)",
            "INTEGER",
            "INTEGER",
            "NUMBER",
            "VARCHAR2(4000 BYTE)"
        };
        String tableName = "liwa.test_host_table";
        //        loader.loadCsvData("tmp_data/host_table.csv", fieldNames, fieldTypes, tableName, true);
        loader.createLoadCsv("tmp_data/host_table.csv", fieldNames, fieldTypes, tableName, true);

        db.close();
    }

}
