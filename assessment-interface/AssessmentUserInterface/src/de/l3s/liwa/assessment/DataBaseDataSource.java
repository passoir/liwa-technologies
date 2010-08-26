package de.l3s.liwa.assessment;

import de.l3s.liwa.assessment.db.ConnectToDb;
import de.l3s.liwa.assessment.db.ConnectToDb.ResultAndStatement;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DataBaseDataSource extends DataSource {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    
    String tableSpace;
    String tableNamePrefix; 
    String prefix;
    
    Map<String, String> labelSetMap; // mapping labelSet names to database column names
    List<String> exclEndings; // endings of excluded hosts (from config)
    // database connection
    private ConnectToDb db;

    // Constructor with the data files
    public DataBaseDataSource(Configuration conf) {
        super(conf);
        init(conf);
    }

    public void setConnection(ConnectToDb db) {
        this.db = db;
    }

    private void init(Configuration conf) {
        tableSpace = "liwa";
        //tableNamePrefix = "test_";
        tableNamePrefix = "f_";
        prefix = tableSpace + "." + tableNamePrefix;
        HashMap<String, String> map = new HashMap<String, String>();
        for (LabelSetGroup lgroup : labelSetGroups) { 
            for (LabelSet lset : lgroup.getLabelSets()) { 
                map.put(lset.getName(), lset.getName()); // now the names are the same, can be changed
            }
        }
        labelSetMap = java.util.Collections.unmodifiableMap(map);
        exclEndings = conf.getExcludedHostEndings();
    }

    // -----------------------------------------
    // - implementing the DataSource interface -
    // -----------------------------------------

    public boolean isValidUserPass(String username, String password) {
        String cmd = "select \"UserID\" from " +prefix+"user_table " +
            "where \"Password\"='"+password +"' " +
            "and \"UserName\"='"+username+"'";

        ResultAndStatement rs = db.exec(cmd);

        try {
            if (rs==null) {
                System.err.println("Internal Error! (isValidUserPass)"); 
                return false;
            }
            if (!rs.getResult().next()) { // no rows returned
                return closeRsAndReturnFalse(rs);
            }
                
            // the previous "rs.getResult().next()" inside the condition moves the cursor to the first result row
            Integer id = rs.getResult().getInt(1);

            if (rs.getResult().next()) {
                System.err.println("Internal Error: this user (" + username + ") is multiplicated!");
                return closeRsAndReturnFalse(rs);
            }
            // ok, we have exactly one user with the username and password
            rs.close();
            return true;

        } catch (SQLException se){
            System.err.println("Internal Error: isValidUserPass");
            se.printStackTrace();
            return closeRsAndReturnFalse(rs);
        }
    }

    public Host getHost(int id) {
        String cmd = "select * "+
            "from " + prefix + "host_table " +
            "where HOSTID="+id ;
        
        ResultAndStatement rs = db.exec(cmd);
        try {
            if (!rs.getResult().next()) {
                System.err.println("Internal Error: host for id="+id+
                                   " is not found!");
                return closeRsAndReturnNullHost(rs);
            }     
            String mainUrl = rs.getResult().getString(2);
            String lang = rs.getResult().getString(3);
            Host host = new Host(this, id, mainUrl, lang);
            setHostAttributes(host, rs.getResult());
            if (rs.getResult().next()) {
                System.err.println("Internal Error: this host (id="+id+") is multiplicated!");
                return closeRsAndReturnNullHost(rs);
            }
            // ok
            rs.close();
            return host;
        } catch (SQLException se) {
            System.err.println("Internal Error: getHost");
            se.printStackTrace();
            return closeRsAndReturnNullHost(rs);
        }
    }

    private Host getHostSimple(String address) {
        String cmd = "select * "+
            "from " + prefix + "host_table " +
            "where \"MainURL\"='"+address+ "'" ;

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (!rs.getResult().next()) {
                return closeRsAndReturnNullHost(rs);
            }
            int hostId = rs.getResult().getInt(1);
            String lang = rs.getResult().getString(2);
            Host host = new Host(this, hostId, address, lang);
            setHostAttributes(host, rs.getResult());
            // ok
            rs.close();
            return host;

        } catch (SQLException se) {
            System.err.println("Internal Error: getHostSimple");
            se.printStackTrace();
            return closeRsAndReturnNullHost(rs);
        }
    }
    

    // Cuts the "http://" part from the adress before trying to find it in the database.
    private Host getHostWithoutHttp(String address) {
        int where = address.indexOf("http://");
        String newaddress;
        if (where >=0) {
            newaddress = address.substring(where + 7);
            System.out.println("new address: "+ newaddress);
        } else {
            newaddress = address;
        }
        String cmd = "select * "+
            "from " + prefix + "host_table " +
            "where \"MainURL\"='"+ newaddress + "'" ;
        ResultAndStatement rs = db.exec(cmd);
        try {
            if (!rs.getResult().next()) {
                return closeRsAndReturnNullHost(rs);
            }
            int hostId = rs.getResult().getInt(1);
            String lang = rs.getResult().getString(2);
            Host host = new Host(this, hostId, address, lang);
            setHostAttributes(host, rs.getResult());
            rs.close();
            return host;

        } catch (SQLException se){
            System.err.println("Internal Error: getHostWithoutHttp");
            se.printStackTrace();
            return closeRsAndReturnNullHost(rs);
        }
    }


    public Host getHost(String address) {
        Host ret = getHostSimple(address);
        if (ret == null) {
            ret = getHostWithoutHttp(address);
        }
        if (ret == null) {
            System.err.println("Internal Error: host for address="+address+
                               " is not found!");
        }
        return ret;
    }

    public User getUser(int id) {
        String cmd = "select \"UserName\", \"Password\", \"Language\" "+
            "from " + prefix + "user_table " +
            "where \"UserID\"="+id ;

        ResultAndStatement rs = db.exec(cmd);
        try {
            if(!rs.getResult().next()){
                System.err.println("Internal Error: user for id="+id+
                                   " is not found!");
                return closeRsAndReturnNullUser(rs);
            }
           
            String userName = rs.getResult().getString(1);
            String pass = rs.getResult().getString(2);
            String lang = rs.getResult().getString(3);
            
            User user = new User(this, id, userName, pass);
            user.setLanguage(lang);
            if (rs.getResult().next()) {
                System.err.println("Internal Error: this user (id="+id+") is multiplicated!");
                return closeRsAndReturnNullUser(rs);
            }
            // ok
            rs.close();
            return user;
        } catch (SQLException se){
            System.err.println("Internal Error: getUser");
            se.printStackTrace();
            return closeRsAndReturnNullUser(rs);
        }
    }

    public User getUser(String name, String password) {
        String cmd = "select \"UserID\", \"Language\"  "+
            "from " + prefix + "user_table " +
            "where \"UserName\"='"+name + "' AND \"Password\"='"+password+"'" ;
        
        ResultAndStatement rs = db.exec(cmd);
        try {
            if(!rs.getResult().next()) {
                System.err.println("Internal Error: user for user="+name+
                                   " is not found!");
                return closeRsAndReturnNullUser(rs);
            }
            int userId = rs.getResult().getInt(1);
            String lang = rs.getResult().getString(2);

            User user = new User(this, userId, name, password);
            user.setLanguage(lang);
            if (rs.getResult().next()) {
                System.err.println("Internal Error: this user (name="+name+") is multiplicated!");
                return closeRsAndReturnNullUser(rs);
            }
            rs.close();
            return user;
        } catch (SQLException se) {
            System.err.println("Internal Error: getUser2");
            se.printStackTrace();
            return closeRsAndReturnNullUser(rs);
        }
    }

    public User getUserByName(String name) {
        String cmd = "select \"UserID\", \"Password\", \"Language\" "+
            "from " + prefix + "user_table " +
            "where \"UserName\"='"+name +"'";
        
        ResultAndStatement rs = db.exec(cmd);
        try {
            if(!rs.getResult().next()){
                System.err.println("Internal Error: user for user="+name+
                                   " is not found!");
                return closeRsAndReturnNullUser(rs);
            }
            int userId = rs.getResult().getInt(1);
            String pass = rs.getResult().getString(2);
            String lang = rs.getResult().getString(3);
            
            User user = new User(this, userId, name, pass);
            user.setLanguage(lang);
            if (rs.getResult().next()) {
                System.err.println("Internal Error: this user (name="+name+") is multiplicated!");
                return closeRsAndReturnNullUser(rs);
            }
            rs.close();
            return user;
        } catch (SQLException se){
            System.err.println("Internal Error: getUserByName");
            se.printStackTrace();
            return closeRsAndReturnNullUser(rs);
        }
    }

    public boolean isHostLabelled(Host host) {
        boolean ret = false; // default return value on error
        String hostUrl = host.getOrigAddress();
        String cmd = "select count(*) "+
            "from " + prefix + "assessment_history_table " +
            "where \"HostURL\"='" + hostUrl + "'"; 

        ResultAndStatement rs = db.exec(cmd);
        try {
            if(!rs.getResult().next()){
                System.err.println("Internal Error: isHostLabelled: wrong sql.");
                return closeRsAndReturnFalse(rs);
            }           
            int count = rs.getResult().getInt(1);
            rs.close();
            if (count < 1) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException se){
            System.err.println("Internal Error: isHostLabelled");
            se.printStackTrace();
            return closeRsAndReturnFalse(rs);
        }
    }

    public boolean isHostLabelledByUser(Host host, User user) {
        String hostUrl = host.getOrigAddress();
        int userId = user.getId();
        String cmd = "select count(*) "+
            "from " + prefix + "assessment_history_table " +
            "where  \"HostURL\"='" + hostUrl + "' AND \"UserID\"=" +userId; 

        ResultAndStatement rs = db.exec(cmd);
        try {
            if(!rs.getResult().next()){
                System.err.println("Internal Error: isHostLabelledByUser: wrong sql.");
                return closeRsAndReturnFalse(rs);
            }           
            int count = rs.getResult().getInt(1);
            rs.close();
            if (count < 1) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException se){
            System.err.println("Internal Error: isHostLabelledByUser"); 
            se.printStackTrace();
            return closeRsAndReturnFalse(rs);
        }
    }

    public List<Host> getLabelledHosts() {
        List<Host> ret = new ArrayList<Host>(); 
        String cmd = "select "+ prefix + "host_table.\"MainURL\" "+
            "from " + prefix + "assessment_history_table , " + prefix + "host_table " +
            "where " + 
            prefix + "assessment_history_table.\"HostURL\"="+ 
            prefix + "host_table.\"MainURL\" " + 
            "group by " + prefix + "host_table.\"MainURL\" " ; 

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                int hostId = rs.getResult().getInt(1);
                Host host = getHost(hostId);
                ret.add(host);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLabelledHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullHostList(rs);
        }
    }

    public Integer getNumOfLabelledHosts() {
        Integer ret = -1; 
        String cmd = "select count(distinct "+ prefix + 
            "assessment_history_table.\"HostURL\" ) "+
            "from " + prefix + "assessment_history_table "; 

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (rs.getResult().next()) {
                ret = rs.getResult().getInt(1);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getNumOfLabelledHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullInteger(rs);
        }
    }

    public Integer getNumOfLabelledHosts(String language) {
        Integer ret = -1; 
        String cmd = "select count(distinct "+ prefix + 
            "assessment_history_table.\"HostURL\" ) "+
            "from " + prefix + "assessment_history_table, " +
            prefix + "host_table where " + prefix + 
            "assessment_history_table.\"HostURL\" = " + prefix + 
            "host_table.\"MainURL\" and " + prefix + 
            "host_table.\"Language\"='" + language + "'";
        for (int i=0; i<exclEndings.size(); i++) {            
            cmd = cmd + " and regexp_instr("+prefix+"HOST_TABLE.\"MainURL\", '"
                + exclEndings.get(i)+"')<=0 ";
        }

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (rs.getResult().next()) {
                ret = rs.getResult().getInt(1);
            }
            rs.close();
            return ret;

        } catch (SQLException se) {
            System.err.println("Internal Error: getNumOfLabelledHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullInteger(rs);
        }
    }

    public Integer getNumOfAllLabels() {
        Integer ret = -1; 
        String cmd = "select count("+ prefix + 
            "assessment_history_table.\"HostURL\" ) "+
            "from " + prefix + "assessment_history_table where "
            + prefix + "assessment_history_table.\"IsLast\"=1"; 

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (rs.getResult().next()) {
                ret = rs.getResult().getInt(1);
            }
            rs.close();
            return ret;

        } catch (SQLException se) {
            System.err.println("Internal Error: getLabelledHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullInteger(rs);
        }
    }

    public Integer getNumOfAllLabels(String language) {
        Integer ret = -1; 
        String cmd = "select count("+ prefix + 
            "assessment_history_table.\"HostURL\" ) "+
            "from " + prefix + "assessment_history_table, " +
            prefix + "host_table where " + prefix + 
            "assessment_history_table.\"HostURL\" = " + prefix + 
            "host_table.\"MainURL\" and " + prefix + 
            "host_table.\"Language\"='" + language + "' and " +
            prefix + "assessment_history_table.\"IsLast\"=1"; 
        for (int i=0; i<exclEndings.size(); i++) {            
            cmd = cmd + " and regexp_instr(" + prefix + "host_table.\"MainURL\", '"
                + exclEndings.get(i)+"')<=0 ";
        }


        ResultAndStatement rs = db.exec(cmd);
        try {
            if (rs.getResult().next()) {
                ret = rs.getResult().getInt(1);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLabelledHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullInteger(rs);
        }
    }


    public List<Host> getHostsEvaluatedByUser(User user) {
        int userId = user.getId();
        List<Host> ret = new ArrayList<Host>(); 
        String cmd = "select "+ prefix + "host_table.HostID "+
            "from " + prefix + "assessment_history_table , " + prefix + "host_table " +
            "where " + 
            prefix + "assessment_history_table.\"HostURL\"="+ 
            prefix + "host_table.\"MainURL\" AND " + 
            prefix + "assessment_history_table.\"UserID\"=" + userId + 
            " group by " + prefix + "host_table.HostID " ; 
        //System.err.println("getHostsEvaluatedByUser: "+cmd);

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                int hostId = rs.getResult().getInt(1);
                Host host = getHost(hostId);
                ret.add(host);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getHostsEvaluatedByUser"); 
            se.printStackTrace();
            return closeRsAndReturnNullHostList(rs);
        }
    }

    public Integer getNumOfHostsEvaluatedByUser(User user) {
        int userId = user.getId();
        Integer ret = -1; 
        String cmd = "select count(distinct " + prefix + "host_table.HostID)"+
            "from " + prefix + "assessment_history_table , " + prefix + "host_table " +
            "where " + 
            prefix + "assessment_history_table.\"HostURL\"="+ 
            prefix + "host_table.\"MainURL\" AND " + 
            prefix + "assessment_history_table.\"UserID\"=" + userId; 
        ResultAndStatement rs = db.exec(cmd);
        try {
            if (rs.getResult().next()) {
                ret = rs.getResult().getInt(1);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getHostsEvaluatedByUser"); 
            se.printStackTrace();
            return closeRsAndReturnNullInteger(rs);
        }
    }


    public List<Label> getLabelsOfHost(Host host) {
        String hostUrl = host.getOrigAddress();
        List<Label> ret = new ArrayList<Label>(); 
        String cmd = "select \"UserID\", \"Comment\", \"IsLast\" ";
        for (String label : labelSetMap.values()) {
            cmd += ", \""+ label +"\"";
        }

        cmd += " from " + prefix + "assessment_history_table " + 
            "where \"HostURL\"='" + hostUrl + "'";

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                int userId = rs.getResult().getInt(1);
                String comment = rs.getResult().getString(2);
                int isLast = rs.getResult().getInt(3);
                boolean isLastB;
                if (isLast > 0) {
                    isLastB = true;
                } else {
                    isLastB = false;
                }
                Label label = new Label(hostUrl, userId, comment, isLastB);
                for (String labelName : labelSetMap.values()) { 
                    String labelValue = rs.getResult().getString(labelName);
                    label.setLabel(labelName, labelValue);
                }
                ret.add(label);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLabelsOfHost"); 
            se.printStackTrace();
            return closeRsAndReturnNullLabelList(rs);
        }
    }

    public List<Label> getLabelsOfUser(User user) {
        int userId = user.getId();
        ArrayList<Label> ret = new ArrayList<Label>();
        String cmd = "select \"HostURL\", \"Comment\", \"IsLast\" ";
        for (String label : labelSetMap.values()) {
            cmd += ", \""+ label +"\"";
        }
        cmd += "  from " + prefix + "assessment_history_table "
            + "where \"UserID\"=" + userId; 

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                String hostUrl = rs.getResult().getString(1);
                String comment = rs.getResult().getString(2);
                int isLast = rs.getResult().getInt(3);
                boolean isLastB;
                if (isLast > 0) {
                    isLastB = true;
                } else {
                    isLastB = false;
                }
                Label label = new Label(hostUrl, userId, comment, isLastB);
                for (String labelName : labelSetMap.values()) { 
                    String labelValue = rs.getResult().getString(labelName);
                    label.setLabel(labelName, labelValue);
                }
                ret.add(label);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLabelsOfUser"); 
            se.printStackTrace();
            return closeRsAndReturnNullLabelList(rs);
        }
    }

    public List<Label> getLabelsOfHostByUser(Host host, User user) {
        String hostUrl = host.getOrigAddress();
        int userId = user.getId();
        ArrayList<Label> ret = new ArrayList<Label>();
        String cmd = "select \"Comment\", \"IsLast\" ";
        for (String label : labelSetMap.values()) {
            cmd += ", \""+ label +"\"";
        }
        cmd += "  from " + prefix + "assessment_history_table " + 
            "where \"UserID\"=" +userId + " AND \"HostURL\"='" + hostUrl + "'";

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                String comment = rs.getResult().getString(1);
                int isLast = rs.getResult().getInt(2);
                boolean isLastB;
                if (isLast > 0) {
                    isLastB = true;
                } else {
                    isLastB = false;
                }
                Label label = new Label(hostUrl, userId, comment, isLastB);
                for (String labelName : labelSetMap.values()) { 
                    String labelValue = rs.getResult().getString(labelName);
                    label.setLabel(labelName, labelValue);
                }
                ret.add(label);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLabelsOfHostByUser"); 
            se.printStackTrace();
            return closeRsAndReturnNullLabelList(rs);
        }
    }

    public List<String> getCommentsOfHost(Host host) {
        ArrayList<String> ret = new ArrayList<String>();
        String cmd = "select  \"Comment\"  from "
            + prefix + "assessment_history_table " + 
            "where \"HostURL\"='" + host.getOrigAddress() + "'";

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                String comment = rs.getResult().getString(1);
                if (comment != null) {
                    if (comment.trim().length() > 0) {
                        ret.add(comment);
                    }
                }
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLabelsOfHostByUser"); 
            se.printStackTrace();
            if (rs != null) {
                rs.close(); 
            } 
            return null;
        }
    }


    public int getNumberOfHosts() {
        String cmd = "select count( * ) from "
            + prefix + "host_table ";

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (!rs.getResult().next()) {
                System.err.println("Internal Error: getNumberOfHosts");
                return 0;
            }
            int count = rs.getResult().getInt(1);
            rs.close();
            return count;

        } catch (SQLException se) {
            System.err.println("Internal Error: getNumberOfHosts"); 
            se.printStackTrace();
            if (rs != null) {
                rs.close(); 
            } 
            return 0;
        }
    }

    public int getNumberOfHosts(String lang) {
        String cmd = "select count( HOSTID ) from "
            + prefix + "host_table where "
            + prefix + "host_table.\"Language\"='" + lang + "'";

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (!rs.getResult().next()) {
                System.err.println("Internal Error: getNumberOfHosts");
                return 0;
            }
            int count = rs.getResult().getInt(1);
            rs.close();
            return count;

        } catch (SQLException se) {
            System.err.println("Internal Error: getNumberOfHosts"); 
            se.printStackTrace();
            if (rs != null) {
                rs.close(); 
            } 
            return 0;
        }
    }

    public int getNumberOfNotExcludedHosts(String lang) {
        String cmd = "select count(*) from "
            + prefix + "host_table where "
            + prefix + "host_table.\"Language\"='" + lang + "'";
        for (int i=0; i<exclEndings.size(); i++) {            
            cmd = cmd + " and regexp_instr("+prefix+"HOST_TABLE.\"MainURL\", '"
                + exclEndings.get(i)+"')<=0 ";
        }

        ResultAndStatement rs = db.exec(cmd);
        try {
            if (!rs.getResult().next()) {
                System.err.println("Internal Error: getNumberOfNotExcludedHosts");
                return 0;
            }
            int count = rs.getResult().getInt(1);
            rs.close();
            return count;

        } catch (SQLException se) {
            System.err.println("Internal Error: getNumberOfNotExcludedHosts"); 
            se.printStackTrace();
            if (rs != null) {
                rs.close(); 
            } 
            return 0;
        }
    }


    // return host id only for efficiency
    public List<Integer> getHostsToAssess(User user, int maxLabels) {
        /* SQL: for userid=4
           select * from (
           select liwa.test_host_table.HostID as myhostid, count(liwa.test_host_table.HostID) as mycount from liwa.test_assessment_history_table , liwa.test_host_table 
           where liwa.test_assessment_history_table."HostURL"=liwa.test_host_table."MainURL" 
           AND liwa.test_assessment_history_table."UserID"!=4 
           and regexp_instr(liwa.TEST_HOST_TABLE."MainURL", 'palace\.eu')<=0
           group by liwa.test_host_table.HostID 
           ) where mycount>0 and mycount<3
           and
           NOT EXISTS ( 
           select liwa.test_host_table.HostID  from liwa.test_assessment_history_table, liwa.test_host_table where liwa.test_assessment_history_table."UserID"=4 
           AND liwa.test_assessment_history_table."HostURL"=liwa.test_host_table."MainURL" AND myhostid=liwa.test_host_table.HostID
           )
        */

        int userId = user.getId();
        
        List<Integer> ret = new ArrayList<Integer>();
        String cmd = "SELECT * FROM ( SELECT " + prefix +
            "host_table.HostID as myhostid, count(" + prefix +
            "host_table.HostID) as mycount FROM " + prefix +
            "assessment_history_table, " + prefix + "host_table " +
            "where " + prefix + "assessment_history_table.\"HostURL\"=" +
            prefix + "host_table.\"MainURL\" AND " + prefix +
            "assessment_history_table.\"UserID\"!=" + userId;

        for (int i=0; i<exclEndings.size(); i++) {            
            cmd = cmd + " AND regexp_instr("+prefix+"HOST_TABLE.\"MainURL\", '"
                + exclEndings.get(i)+"')<=0 ";
        }
        

        cmd = cmd + "GROUP BY "+ prefix + "host_table.HostID ) " +
            "WHERE mycount>0 AND mycount<" + maxLabels +
            " AND NOT EXISTS (" +
            "SELECT " + prefix + "host_table.HostID from " + prefix +
            "assessment_history_table, " + prefix + "host_table WHERE " +
            prefix + "assessment_history_table.\"UserID\"=" + userId +
            " AND " + prefix + "assessment_history_table.\"HostURL\"=" +
            prefix + "host_table.\"MainURL\" AND myhostid=" +
            prefix + "host_table.HostID )";

        ResultAndStatement rs = db.exec(cmd);
        try {
            while (rs.getResult().next()) {
                int hostId = rs.getResult().getInt(1);
                //Host host = getHost(hostId);
                ret.add(hostId);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getHostsToAssess"); 
            se.printStackTrace();
            return closeRsAndReturnNullIntegerList(rs);
        }        
    }

    // Returns true on success, false on error
    public boolean saveLabel(Label label) {
        // updating a previous label to "isLast=0" in the same commit!
        db.setAutoCommit(false);
        String hostUrl = label.getHostUrl();
        String cmd1 = "update " + prefix + "assessment_history_table " +
            "set \"IsLast\"=0 where \"IsLast\"=1 and \"UserID\"=" + label.getUserId() +
            " and \"HostURL\"='" + hostUrl + "'";
        boolean success1 = db.execute(cmd1);
        if (!success1) {
            return false;
        }
        // collecting non-null values from Label 
        ArrayList<String> labelnames = new ArrayList<String>();
        ArrayList<String> labelvalues = new ArrayList<String>();
        
        for (String labelName : labelSetMap.values()) { 
            String val = label.getLabel(labelName);
            if (val != null && !val.equals("null")) {
                labelnames.add(labelName);
                labelvalues.add(val);
            }
        }

        String cmd2 = "insert into " + prefix + "assessment_history_table " +
            "(\"UserID\", \"HostURL\", \"Date\", \"Comment\", \"IsLast\" ";
        for (String labell : labelnames) {
            cmd2 += ",\"" + labell + "\"";
        }
        cmd2 += " ) values ( " + label.getUserId() +", '"+ hostUrl +
            "', TO_DATE( '"+ nowDate() +"', 'YYYY-MM-DD HH24:MI:SS' ) , "
            + " '" + label.getComment() + "', '1'"; 
        for (String labelv : labelvalues) { 
            cmd2 += ", '" + labelv + "'";
        }
        cmd2 += ")";
        boolean success2 = db.execute(cmd2);
        //System.out.println("Committing new label: host:" + label.getHostUrl() +
        //" label:" + label.toString());

        // Check if this was a host from the users to-be-assessed list. 
        // Then update the list (set this host's "assessed" flag)
        User u = getUser(label.getUserId());
        if (hasHostToAssess(u)) {
            if (getNextHostFromList(u, false).getOrigAddress().
                equals(hostUrl)) {
                // update so that the user wont get it anymore
                String cmd3 = "update " + prefix + "users_list_table " +
                    "set \"Assessed\"=3 where \"Assessed\">0 and"+
                    " \"Assessed\"<3 and \"HostURL\"='" +hostUrl+ "'";

                boolean success3 = db.execute(cmd3);
                if (!success3) {
                    System.err.println("Error: could not update list item in saveLabel!"); 
                }
            }
        }


        db.commit();
        db.setAutoCommit(true);
        if (!success2) {
            return false;
        } else {
            System.out.println("Committed new label by user: " + label.getUserId());
            return true;
        }
    }

    // List of Hosts that are linked by Host "h".
    public List<String> getLinkedHosts(Host h) {
        String fromHostUrl= h.getOrigAddress();
        String cmd = "select \"ToHostURL\" from "
            + prefix + "host_link_table where \"FromHostURL\"='" + fromHostUrl + "'";

        ResultAndStatement rs = db.exec(cmd);
        List<String> ret = new ArrayList<String>();
        try {
            while (rs.getResult().next()) {            
                String toHostUrl = rs.getResult().getString(1);
                ret.add(toHostUrl);
            }
            rs.close();
            //System.err.println("getLinkedHosts returns : " + ret.size() + "hosts" ); 
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLinkedHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullStringList(rs);
        }
    }
    
    // List of Hosts that are linking to Host "h".
    public List<String> getLinkingHosts(Host h) {
        String toHostUrl= h.getOrigAddress();
        String cmd = "select \"FromHostURL\" from "
            + prefix + "host_link_table where \"ToHostURL\"='" + toHostUrl + "'";

        ResultAndStatement rs = db.exec(cmd);
        List<String> ret = new ArrayList<String>();
        try {
            while (rs.getResult().next()) {         
                String fromHostUrl = rs.getResult().getString(1);
                ret.add(fromHostUrl);
            }
            rs.close();
            //System.err.println("getLinkingHosts returns : " + ret.size() + "hosts" ); 
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getLinkingHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullStringList(rs);
        }
    }

    public List<String> getSamplePagesOfHost(Host h) {
        String hostUrl= h.getOrigAddress();
        String cmd = "select \"PageURL\" from "
            + prefix + "host_pages_table where \"HostURL\"='" + hostUrl + "'";

        ResultAndStatement rs = db.exec(cmd);
        List<String> ret = new ArrayList<String>();
        try {
            while (rs.getResult().next()) {         
                String pageUrl = rs.getResult().getString(1);
                ret.add(pageUrl);
            }
            rs.close();
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getSamplePagesOfHosts"); 
            se.printStackTrace();
            return closeRsAndReturnNullStringList(rs);
        }
        
    }


    public void addHostToAssess(User user, Host host) {
        // todo
        /*insert  into f_users_list_table 
          (FIFO_COUNTER, "UserID", "HostURL", "Assessed") 
          values(USERS_LIST_SEQ.NEXTVAL+1, 27, 'abc.com', 0)
        */
        String cmd = "insert into " + prefix + "users_list_table " +
            "(FIFO_COUNTER, \"UserID\", \"HostURL\", \"Assessed\") " +
            "values(FIFO_COUNTER_SEQ.NEXTVAL, "+user.getId()+", '"
            + host.getOrigAddress() + "', 0)";
        ResultAndStatement rs = db.exec(cmd);
        
    }

    public boolean hasHostToAssess(User user) {
        String cmd = "select count(*) from " + prefix + "USERS_LIST_TABLE " +
            " where " + prefix + "USERS_LIST_TABLE.\"UserID\"=" + user.getId() +
            " and " + prefix + "USERS_LIST_TABLE.\"Assessed\"<3";

        ResultAndStatement rs = db.exec(cmd);
        try {
            if(!rs.getResult().next()){
                System.err.println("Internal Error: hasHostToAssess: wrong sql.");
                return closeRsAndReturnFalse(rs);
            }           
            int count = rs.getResult().getInt(1);
            rs.close();
            if (count < 1) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException se){
            System.err.println("Internal Error: hasHostToAssess"); 
            se.printStackTrace();
            return closeRsAndReturnFalse(rs);
        }
    }


    public Host getNextHostFromList(User user) {
        return getNextHostFromList(user, true);
    }

    private Host getNextHostFromList(User user, boolean increase) {
        int userId = user.getId();
        Host ret = null;
        /*

          select userid, url,
          counter 
          from
          (
          select liwa.F_USERS_LIST_TABLE."UserID" as userid,
          liwa.F_USERS_LIST_TABLE."HostURL" as url,
          liwa.F_USERS_LIST_TABLE.FIFO_COUNTER as counter,
          min(liwa.F_USERS_LIST_TABLE.FIFO_COUNTER) over 
          (partition by liwa.F_USERS_LIST_TABLE."UserID") max_my_counter
          from liwa.F_USERS_LIST_TABLE
          where liwa.F_USERS_LIST_TABLE."Assessed"=0
          and liwa.F_USERS_LIST_TABLE."UserID"=27
          )
          where counter = max_my_counter
        */
             
        int counter = -1;
        String cmd1 ="select userid, url, counter from " +
            "(select " + prefix + "USERS_LIST_TABLE.\"UserID\" as userid, " +
            prefix + "USERS_LIST_TABLE.\"HostURL\" as url, " +
            prefix + "USERS_LIST_TABLE.FIFO_COUNTER as counter, " +
            "min(" + prefix + "USERS_LIST_TABLE.FIFO_COUNTER) over " +
            "(partition by " + prefix + "USERS_LIST_TABLE.\"UserID\")" +
            " max_my_counter " +
            "from " +prefix + "USERS_LIST_TABLE " +
            "where " + prefix + "USERS_LIST_TABLE.\"Assessed\"<3 " +
            "and " + prefix + "USERS_LIST_TABLE.\"UserID\"=" + userId +
            ") where counter = max_my_counter";

        ResultAndStatement rs = db.exec(cmd1);
        try {
            while (rs.getResult().next()) {
                String hostUrl = rs.getResult().getString(2);
                ret = getHost(hostUrl);
                counter = rs.getResult().getInt(3);
            }
            rs.close();

            if (increase) {
                // increase state : if a host is seen 3 times but not saved then it wil be skipped
                String cmd2 = "update " + prefix + "users_list_table " +
                    "set \"Assessed\"=\"Assessed\"+1 where FIFO_COUNTER=" + counter;

                boolean success2 = db.execute(cmd2);
                if (!success2) {
                    System.err.println("Error: could not update list item!"); 
                }
                if (ret==null) {
                    System.err.println("Error: Null Host list item!"); 
                }
            }
            return ret;

        } catch (SQLException se){
            System.err.println("Internal Error: getHostsEvaluatedByUser"); 
            se.printStackTrace();
            return closeRsAndReturnNullHost(rs);
        }
    }

    // ------------------
    // - helper methods -
    // ------------------

    // Sets the attributes defined in the configuration to the host
    // TODO: handle arbitrary-numbered attributes (for example "outlinks"), 
    // these will be stored as hostID-key-value triples in the database
    // (This is not used now, we have no attributes!)
    private void setHostAttributes(Host host, ResultSet rs) {
        for (int i = 0; i < attribNo; i++) {
            HostAttributeType t = attrTypes.get(i);
            Object value = null;
            try {
                if (t.getType() == HostAttributeType.AttributeType.URL) {
                    String attrData = rs.getString(t.getName());
                    if (!attrData.contains("://")) {
                        attrData = "http://" + attrData;
                    }
                    try {
                        value = new URL(attrData);
                    } catch (java.net.MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                } else if (t.getType() == HostAttributeType.AttributeType.INT) {
                    int attrData = rs.getInt(t.getName());
                    value = new Integer(attrData);
                } else if (t.getType() == HostAttributeType.AttributeType.DOUBLE) {
                    double attrData = rs.getDouble(t.getName());
                    value = new Double(attrData);
                } else if (t.getType() == HostAttributeType.AttributeType.STRING) {
                    String attrData = rs.getString(t.getName());
                    value = (String)attrData;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            HostAttribute attr = new HostAttribute(t, value);
            host.addAttribute(attr);
        }
    }

    public static String nowDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }


    private Host closeRsAndReturnNullHost(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }

    private List<Host> closeRsAndReturnNullHostList(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }


    private User closeRsAndReturnNullUser(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }

    private boolean closeRsAndReturnFalse(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return false;                
    }

    private List<Label> closeRsAndReturnNullLabelList(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }

    private List<String> closeRsAndReturnNullStringList(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }

    private Integer closeRsAndReturnNullInteger(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }

    private List<Integer> closeRsAndReturnNullIntegerList(ResultAndStatement rs) {
        if (rs != null) {
            rs.close();
        }
        return null;                
    }


    // -----------------
    // - other methods -
    // -----------------
    // TODO - if needed
    public boolean isHostWithConflictingLabels(Host host) {
        return false;
    }

}
