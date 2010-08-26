package de.l3s.liwa.assessment.db;

import oracle.jdbc.pool.OracleDataSource;

import java.nio.charset.Charset;
import java.io.*;
import java.util.ArrayList;
import java.sql.*;

/**
 * Handles connections to the database.
 *
 * After usage please call close().
 */
public class ConnectToDb {

    public class ResultAndStatement {
        private ResultSet result;
        private Statement statement;

        public ResultAndStatement(ResultSet rs, Statement st) {
            result = rs;
            statement = st;
        }

        public ResultSet getResult() {
            return result;
        } 
        
        public void close() {
            try {
                result.close();
                statement.close();
            } catch (SQLException se) {
                System.err.println("Exception during closing ResultAndStatement");
                se.printStackTrace();
            }
        }
        
    }

    protected Connection c;

    private String dbName, dbUser, dbPass, dbNameLocal;

    private final boolean preserveConnection;

    public ConnectToDb(String dbName, String dbUser, String dbPass, String dbNameLocal, boolean preserveConnection) {
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbNameLocal = dbNameLocal;
        this.preserveConnection = preserveConnection;

        /*
          try {
          Class.forName("org.postgresql.Driver");
          } catch (ClassNotFoundException cnfe) {
          System.err.println("ConnectToDb: Couldn't find driver class:");
          cnfe.printStackTrace();
          }

          try {
          c = DriverManager.getConnection(dbName, dbUser, dbPass);
          } catch (SQLException se) {
          System.err.println("ConnectToDb: Couldn't connect: dbName=" + dbName + ", dbUser=" + dbUser + ", dbPass=" + dbPass);
          System.err.println("Ez a csatlakozas nem sikerult, megprobalom megegyszer.");
          //System.err.println("Print out a stack trace and exit.");
          //se.printStackTrace();
          //System.exit(1);
          try {
          c = DriverManager.getConnection(dbNameLocal, dbUser, dbPass);
          } catch (SQLException sqle) {
          System.err.println("ConnectToDb: Couldn't even connect to: dbName=" + dbNameLocal + ", dbUser=" + dbUser + ", dbPass=" + dbPass);
          //System.err.println("Print out a stack trace and exit.");
          se.printStackTrace();
          }
          }
        */
        OracleDataSource ods = null;
        try{
            ods = new OracleDataSource();

            //	    ods.setURL("jdbc:oracle:thin:@//decore.ilab.sztaki.hu:1521/mine");
            ods.setURL( "jdbc:oracle:thin:@//" + dbName );

            //	    ods.setUser("sztaki");
            ods.setUser(dbUser);

            //	    ods.setPassword("britsdine");
            ods.setPassword(dbPass);

            c = ods.getConnection();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (c == null) {
            System.err.println("ConnectToDb: Error c is null");
        }

        optimize();
    }

    // Copy constructor
    public ConnectToDb(ConnectToDb ctdb) {
        OracleDataSource ods = null;
        try{
            ods = new OracleDataSource();

            //	    ods.setURL("jdbc:oracle:thin:@//decore.ilab.sztaki.hu:1521/mine");
            ods.setURL( "jdbc:oracle:thin:@//" + ctdb.dbName );

            //	    ods.setUser("sztaki");
            ods.setUser(ctdb.dbUser);

            //	    ods.setPassword("britsdine");
            ods.setPassword(ctdb.dbPass);

            c = ods.getConnection();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (c == null) {
            System.err.println("ConnectToDb: Error c is null");
        }
        this.preserveConnection = ctdb.preserveConnection;
        optimize();        
    }

    public Connection getConnection() {
        return c;
    }

    public void optimize(){
        /*
        //	this.execute("SET enable_seqscan = off");
        this.execute("SET ENABLE_SEQSCAN TO OFF");
        this.execute("SET ENABLE_NESTLOOP TO OFF");
        this.execute("SET enable_mergejoin TO OFF");               
        */
    }

    public String explain(String cmd){
        ResultSet rs = exec("explain "+cmd).getResult();
        String res="";
        try{ 
            while (rs.next()){
                res += rs.getString(1) + "\n";
            }
        } catch  (SQLException se) {
            System.err.println("Exception during reading from ResultSet");
            se.printStackTrace();
        }
        return res;
    }

    public void executeOptim(String cmd) {	
        setAutoCommit(false);
        optimize();
        System.out.println("explain: " + explain(cmd) );
        execute(cmd);
        setAutoCommit(true);
    }

    public ResultSet execOptim(String cmd) {
        setAutoCommit(false);
        optimize();
        System.out.println("explain: " + explain(cmd) );
        ResultSet rs = exec(cmd).getResult();
        setAutoCommit(true);
        return rs;
    }

    // Returns false on error, true on success
    public boolean execute(String command) {
        Statement s = null;
        try {
            s = c.createStatement();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: error at createStatement");
            se.printStackTrace();
            return false;
            //System.exit(1);
        }

        try {
            s.executeUpdate(command);
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while executing command:" + command);
            se.printStackTrace();
            return false;
        }

        try {
            s.close();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while closing statement.");
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public ResultAndStatement exec(String command) {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = c.createStatement();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: error at createStatement");
            se.printStackTrace();
            //System.exit(1);
        }
        try {
            rs = s.executeQuery(command);
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while executing command:" + command);
            se.printStackTrace();
        }
        return new ResultAndStatement(rs, s);
    }

    //createStatement-ben kulonbozi az exec-tol
    public ResultAndStatement execc(String command) {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                  ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException se) {
            System.err.println("ConnectToDb: error at createStatement");
            se.printStackTrace();
            //System.exit(1);
        }
        try {
            rs = s.executeQuery(command);
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while executing command:" + command);
            se.printStackTrace();
        }
        return new ResultAndStatement(rs, s);
    }

    public void deleteTable(String tableName, boolean cascade) {
        String cas = cascade ? " CASCADE" : "";
        String table = null;
        String schema = null;
        int i = tableName.indexOf('.');
        if (-1 == i) {
            table = tableName;
            schema = "public";
        } else {
            table = tableName.substring(i + 1);
            schema = tableName.substring(0, i);
        }

        ResultSet rs = exec("select * from information_schema.tables " +
                            "where table_name = '" + table.toLowerCase() + "' " +
                            " and table_schema = '" + schema.toLowerCase() + "' ").getResult();
        try {
            if (rs.next()) {
                execute("DROP TABLE " + tableName + cas);
                System.out.println("drop table " + tableName + cas);
            } else {
                System.out.println("ConnectToDb.deleteTable :Nincs ilyen tabla: " + tableName + " ( schema=" + schema + ", table=" + table + ")");
            }
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while rs.next()");
            se.printStackTrace();
        }

        try {
            rs.close();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while closing rs.");
            se.printStackTrace();
        }
    }

    public void deleteTable(String tableName) {
        deleteTable(tableName, false);
    }

    public void createTable(String tableName, String[] fieldNames, String[] fieldTypes, 
                            ArrayList<Integer> prKeys) {
        int fieldNum;
        if (fieldTypes.length < fieldNames.length) {
            fieldNum = fieldTypes.length;
        } else {
            fieldNum = fieldNames.length;
        }

        String create = "create table " + tableName + " (";

        for (int i = 0; i < fieldNum - 1; ++i) {
            create += "\"" + fieldNames[i] + "\"" + " " + fieldTypes[i] + ", ";
        }
        create += "\""+fieldNames[fieldNum - 1] + "\"" + " " + fieldTypes[fieldNum - 1];

        //MOD - 20080716 (kivettuk azt, hogy ha ures a prKeys, akkor elso oszlopra epul idx)
        if (prKeys.size() != 0) {
            create += ", PRIMARY KEY (";
            for (int i = 0; i < prKeys.size() - 1; ++i) {
                create += fieldNames[prKeys.get(i)] + ", ";
            }
            create += fieldNames[prKeys.get(prKeys.size() - 1)] + ")";
        }
        create += ")";

        execute(create);
        /*
          CREATE TABLE example (
          a integer,
          b integer,
          c integer,
          PRIMARY KEY (a, c)
        */
    }

    // generate an integer for each row (incremental)
    public void createTableWithIdGen(String tableName, String[] fieldNames, String[] fieldTypes, 
                                     String idName) {
        int fieldNum;
        if (fieldTypes.length < fieldNames.length) {
            fieldNum = fieldTypes.length;
        } else {
            fieldNum = fieldNames.length;
        }

        String create = "create table " + tableName + " ( " + idName + " INT NOT NULL, ";

        for (int i = 0; i < fieldNum - 1; ++i) {
            create += "\"" + fieldNames[i] + "\"" + " " + fieldTypes[i] + ", ";
        }
        create += "\""+fieldNames[fieldNum - 1] + "\"" + " " + fieldTypes[fieldNum - 1];

        create += ", PRIMARY KEY (" + idName + " ) ";
        create += ")";

        execute(create);
    }

    public void createTable(String tableName, String[] fieldNames, String[] fieldTypes) {
        createTable(tableName, fieldNames, fieldTypes, new ArrayList<Integer>());
    }

    public void addColumn(String table_name, String column_name, String type, boolean onlyIfNotExists) {
        if (onlyIfNotExists) {
            if (!columnExists(table_name, column_name))
                addColumn(table_name, column_name, type);
        } else
            addColumn(table_name, column_name, type);
    }

    public void addColumn(String table_name, String column_name, String type) {
        execute("ALTER TABLE " + table_name + " ADD " + column_name + " " + type);
    }

    public boolean columnExists(String table_name, String column_name) {
        boolean ret = false;
        String schema = table_name.substring(0, table_name.indexOf('.'));
        String table = table_name.substring(table_name.indexOf('.')+1);
        ResultSet rs = exec("select 1 from pg_class c left join pg_namespace n on n.oid = c.relnamespace and n.nspname = '" + schema + 
                            "' join pg_attribute a on a.attrelid = c.oid and a.attnum > 0 and not a.attisdropped and a.attname = '" + column_name +
                            "' where c.relname = '" + table +
                            "' and c.relkind = 'r';" ).getResult();
        try {
            if (rs.next()) {
                ret = true;
            } else {
                ret = false;
            }
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while rs.next()");
            se.printStackTrace();
        }

        try {
            rs.close();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while closing rs.");
            se.printStackTrace();
        }
        return ret;
    }

    public void close() {
        try {
            c.close();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while closing statement.");
            se.printStackTrace();
        }
    }

    public void close(Statement s) {
        try {
            s.close();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while closing statement.");
            se.printStackTrace();
        }
    }

    public void close(ResultSet rs) {
        try {
            rs.getStatement().close();
            rs.close();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: We got an exception while closing result set.");
            se.printStackTrace();
        }
    }

    public void finalize() {
        try {
            if (!preserveConnection) {
                c.close();
            }
        } catch (SQLException se) {
            System.err.println("ConnectToDb finalize: We got an exception while closing connection.");
            se.printStackTrace();
        }
    }

    public void setAutoCommit(boolean b) {
        try {
            c.setAutoCommit(b);
        } catch (SQLException se) {
            System.err.println("ConnectToDb: setAutoCommit(" + b + ")");
            se.printStackTrace();
        }
    }

    public void commit() {
        try {
            c.commit();
        } catch (SQLException se) {
            System.err.println("ConnectToDb: commit");
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {

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

        /*
          ConnectToDb db;
          db = new ConnectToDb(new DbConfig());

          ArrayList<String> names = new ArrayList<String>();
          names.add("elso");
          names.add("masodik");
          names.add("harmadik");
          names.add("negyedik");
          String[] s = new String[names.size()];
          names.toArray(s);

          ArrayList<String> types = new ArrayList<String>();
          types.add("VARCHAR");
          types.add("VARCHAR");
          types.add("VARCHAR");
          types.add("VARCHAR");
          String[] t = new String[types.size()];
          types.toArray(t);

          db.createTable("palitmp0", s, t);

          ArrayList<Integer> pk = new ArrayList<Integer>();
          pk.add(1);
          pk.add(3);
          db.createTable("palitmp1", s, t, pk);

          db.deleteTable("palitmp0", true);
          db.deleteTable("palitmp1", false);

          db.close();
        */
    }
}
