package de.l3s.liwa.assessment;

import java.io.*;
import java.util.*;
import java.sql.Timestamp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.l3s.liwa.assessment.db.ConnectToDb;
import java.sql.SQLException;
import java.sql.ResultSet;

// The values will come fro man XML configuration file. 
public class Configuration {

    // To display a snaphot of the web: a crawl's time and displayed name (2009 July for example)
    public class Snapshot{
        private String name;
        private Timestamp time;

        public void setName(String _name){
            name = _name;
        }

        public void setTimestamp(Timestamp _time){
            time = _time;
        }

        public void setTimestamp(long _time){
            time = new Timestamp(_time);
        }

        public Timestamp getTimestamp(){
            return time;
        }
        
    }

    public enum WebPageSource { WARC, WAYBACK }

    private static Configuration instance; // singleton
    // The main configutration xml file
    private static final String configFileName = "main_config.xml";

    private String dataSourceType; // dummy(csv) or database connection
    // names and types of attributes will be read from the config xml 
    private ArrayList<HostAttributeType> hostAttributeList;
    // a snapshot corresponds to a specific crawl of the web
    private ArrayList<Snapshot> snapshotList;
    // list of url-endings that we want to exclude from manual assessment
    private ArrayList<String> excludedHostListEndsWith;
    //url of warc browser
    private String warcBrowser;
    //url of wayback browser
    private String waybackBrowser;
    // warc or wayback?
    private WebPageSource webPageSource;
    // labels that a user can choose to evaluate a host
    private ArrayList<LabelSetGroup> labelSetGroupList;
    // flags that a user can choose to evaluate a host
    private ArrayList<Flag> flagList;

    private ConnectToDb connection;
    private DataSource dataSource;

    private Configuration(){
        init(this.configFileName);
    }

    private Configuration(String configfile){
        init(configfile);
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    // Hack: To allow another config for testing
    public static Configuration getTestInstance(String otherConfigFile) {        
        return new Configuration(otherConfigFile);
    }

    private static String getTagValue(Element eElement, String sTag) {
        NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0); 
        return nValue.getNodeValue(); 
    }

    private static Element getChild(Element eElement, String sTag) {
        NodeList nlList= eElement.getElementsByTagName(sTag);
        return (Element) nlList.item(0); 
    }

    /**
     * Read string by tagname
     *
     * @param ele xml element
     * @param tagName xml tagnev
     *
     * @return String content
     */
    private static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            Node firstChild = el.getFirstChild();
            if(firstChild!=null) {
                textVal = firstChild.getNodeValue();
            }else{
                return null;
            }
        }
        return textVal;
    }

    void init(String configFileName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputStream is = Configuration.class.getClassLoader().getResourceAsStream(configFileName);
            // in = new BufferedReader(new InputStreamReader(is));


            //            Document dom = db.parse(configFileName);
            Document dom = db.parse(is);
            Element root = dom.getDocumentElement();

            parseHostAttributes( getChild(root, "HostAttributes") );
            
            parseLabelTypes( getChild(root, "AssessmentLabels") );
            parseFlags( getChild(root, "AssessmentLabels") );
            parseExcludedHosts(getChild(root, "ExcludedHosts"));
            generateJavascriptFile();

            parseSnapshots( getChild(root, "Snapshots") );

            warcBrowser = getTextValue(root,"WarcBrowser");
            //System.out.println("WarcBrowser:" + warcBrowser);
            waybackBrowser = getTextValue(root,"WaybackBrowser");
            String webPageServer = getTextValue(root,"WebPageServer");
            if (webPageServer.equals("wayback")) {
                webPageSource = WebPageSource.WAYBACK;
            } else if (webPageServer.equals("warc")) {
                webPageSource = WebPageSource.WARC;
            } else { // default: warc
                webPageSource = WebPageSource.WARC;                
            }
            parseDataSource( getChild(root, "Datasource") );

        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch(SAXException se) {
            se.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        //System.out.println("Configuration xml parsing ready.");	
    }

    void parseExcludedHosts(Element excElement) {
        excludedHostListEndsWith = new ArrayList<String>();
        NodeList excEndNodes = excElement.getElementsByTagName("UrlEndsWith");

        for (int h=0; h<excEndNodes.getLength(); h++) {
            Node node = excEndNodes.item(h); 
            if (node instanceof Element) {
                Element elem = (Element) node;
                String urlEnding;
                Node firstChild = elem.getFirstChild();
                if(firstChild!=null){
                    urlEnding = firstChild.getNodeValue();
                } else {  
                    urlEnding=null;
                }
                excludedHostListEndsWith.add(urlEnding);
            }
        }
    }

    void parseDataSource(Element datasourceElement) {
        dataSourceType = getTextValue(datasourceElement,"Type");
        //System.out.println("dataSourceType=" +dataSourceType);
        Element datasourceConfigElement = getChild(datasourceElement, "Config");

        createDataSource(datasourceConfigElement, dataSourceType);

    }

    void createDataSource(Element datasourceConfigElement, String dataSourceType){
        if(dataSourceType.equals("DataBaseDataSource")) {
            String dataBaseType = getTextValue(datasourceConfigElement,"Type");
            //System.out.println("dataBaseType=" +dataBaseType);
            if(dataBaseType.equals("oracle")){
                Element oracleConfigElement = getChild(datasourceConfigElement, "Config");
                String host = getTextValue(oracleConfigElement,"Host");
                String sid = getTextValue(oracleConfigElement,"SID");
                String user = getTextValue(oracleConfigElement,"User");
                String password = getTextValue(oracleConfigElement,"Password");
                connection = new ConnectToDb( host+"/"+sid, user, password, host+"/"+sid, false );
                //ResultSet rs = db.exec("select 2,3,4 from dual union select 22,23,24 from dual");

                if (connection != null) {
                    dataSource = new DataBaseDataSource(this);
                    ((DataBaseDataSource)dataSource).setConnection(connection);
                } else {
                    System.err.println("Invalid Datasource settings : cannot make database connection!");
                    System.err.println("\t host: " + host);
                    System.err.println("\t sid: " + sid);
                    System.err.println("\t user: " + user);
                    System.err.println("\t pass: " + password);
                }

            }
        } else {
            System.err.println("Unknown Datasource was specified in the config xml file: "+ configFileName +"!");
        }

    }

    void parseHostAttributes(Element hostAttributeElement) {
        hostAttributeList = new ArrayList<HostAttributeType>();
        NodeList nodes = hostAttributeElement.getElementsByTagName("Attribute");
        for(int i=0; i<nodes.getLength(); i++){
            Node node = nodes.item(i);
            if(node instanceof Element){
                Element child = (Element) node;
                String attributeName = getTextValue(child,"Name");
                String attributeId = getTextValue(child,"Id");
                String attributeType = getTextValue(child,"Type");
                //System.out.println("attribute: name=" +attributeName +" id=" + attributeId + " type=" +attributeType);
                HostAttributeType hostAttributeType = new HostAttributeType(attributeName,attributeId,attributeType);
                hostAttributeList.add(hostAttributeType);
            }
        }
    }

    // This is for parsing the more complex "label set groups" and "label sets"
    void parseLabelTypes(Element assessmentLabelsElement) {
        labelSetGroupList = new ArrayList<LabelSetGroup>();
        NodeList groupNodes = assessmentLabelsElement.getElementsByTagName("LabelSetGroup");

        for (int h=0; h<groupNodes.getLength(); h++) {
            Node groupNode = groupNodes.item(h); 
            if (groupNode instanceof Element) {
                LabelSetGroup lsg = new LabelSetGroup();
                Element groupElem = (Element) groupNode;
                if (groupElem.hasAttribute("type")) {
                    String groupType = groupElem.getAttribute("type");
                    lsg.setType(groupType);
                }


                NodeList setNodes = ((Element)groupNode).getElementsByTagName("LabelSet");
                for (int i=0; i<setNodes.getLength(); i++) {
                    Node setNode = setNodes.item(i);
                    String name = "";
                    String type = "";
                    String reqs = "";
                    if (setNode instanceof Element) {
                        Element setElem = (Element) setNode;
                        if (setElem.hasAttribute("name")) {
                            name = setElem.getAttribute("name");
                        } else {
                            System.err.println("ERROR in config xml file: LabelSet" + 
                                               " has no 'name' attribute!");
                        }
                        //if (setElem.hasAttribute("type")) {
                        //    type = setElem.getAttribute("type");
                        //} else {
                        //    System.err.println("ERROR in config xml file: LabelSet" + 
                        //                       " has no 'type' attribute!");
                        //}

                        LabelSet lset = new LabelSet(name/*, type*/);

                        if (setElem.hasAttribute("requires")) {
                            reqs = setElem.getAttribute("requires");
                            String[] reqsA = reqs.split(",");
                            for (int k=0; k<reqsA.length; k++) {
                                lset.setRequired(reqsA[k]);
                            }
                        }

                        if (setElem.hasAttribute("defaultSource")) {
                            String defS = setElem.getAttribute("defaultSource");
                            lset.setDefaultSource(defS);
                        }

                        if (setElem.hasAttribute("fillCommentRequired")) {
                            String commS = setElem.getAttribute("fillCommentRequired");
                            if (commS.toLowerCase().equals("yes") 
                                || commS.toLowerCase().equals("true")) {

                                lset.setCommentRequired(true);
                            }
                        }

                        if (setElem.hasAttribute("tooltip")) {
                            String ttS = setElem.getAttribute("tooltip");
                            lset.setTooltip(ttS);
                        }

                        NodeList nodes = setElem.getElementsByTagName("Label");                
                        for (int j=0; j<nodes.getLength(); j++) {
                            Node node = nodes.item(j);
                            if (node instanceof Element) {
                                Element child = (Element) node;

                                String labelNameUi = getTextValue(child,"NameOnUi");
                                String label = getTextValue(child,"LabelName");
                                //System.out.println("label: name=" +labelNameUi +
                                //" label=" + label);
                                LabelType labelType = new LabelType(labelNameUi, label);
                                lset.addLabel(labelType);
                            }
                        }
                        lsg.addLabelSet(lset);
                    }
                }
                labelSetGroupList.add(lsg);
            }
        }
    }

    // parse flags (that will appear as checkboxes on the UI)
    void parseFlags(Element assessmentLabelsElement) {
        flagList = new ArrayList<Flag>();
        NodeList flagnodes = assessmentLabelsElement.getElementsByTagName("Flag");
        for (int i=0; i<flagnodes.getLength(); i++) {
            Node flagnode = flagnodes.item(i);
            String name = "";
            if (flagnode instanceof Element) {
                Element flagElem = (Element) flagnode;
                if (flagElem.hasAttribute("name")) {
                    name = flagElem.getAttribute("name");
                } else {
                    System.err.println("ERROR in config xml file: Flag" + 
                                       " has no 'name' attribute!");
                }
                String flagNameUi = getTextValue(flagElem,"NameOnUi");
                String flagName = getTextValue(flagElem,"FlagName");
                Flag flag = new Flag(name, flagNameUi, flagName);

                flagList.add(flag);
            }
        }
    }

    // Inserts a line based on the parsed labelsets and their requirements 
    // (to enable/disable menus automatically)
    // generates "formhandler.jsp" from "formjandler.js"
    void generateJavascriptFile() {
        boolean inputExists = false;
        try {
            // check js file
            inputExists = 
                (new File
                 ("AssessmentUserInterface/WebContent/pages/formhandler.js")
                 ).exists(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        if (!inputExists) {
            return;
        }
        // generate the string to be inserted: encodes the requirements
        // Example: ";;0:LEnglish;0:LGerman,0:LOther;;;;;;;;;;1:HTNormal"
        StringBuffer reqSb = new StringBuffer();
        // generate the string to be inserted: encodes the default values
        // that must be default to make a real assessment
        // Example: "LEnglish;HTNormal;;;;;;;"
        StringBuffer defaultInfoSb = new StringBuffer();

        // generate the string to be inserted: encodes the default values' sources
        StringBuffer defValsSb = new StringBuffer();

        // generate the string to be inserted: contains the default values where
        // a non-defalult selected value requires a comment 
        StringBuffer needsCommSb = new StringBuffer();

        StringBuffer selectMinOneSb = new StringBuffer();

        // "global" labelSetList for all labelsets
        List<LabelSet> labelSetList = new ArrayList<LabelSet>();

        boolean added = false;
        int grpCounter = 0;
        for (LabelSetGroup lsg : labelSetGroupList) {
            grpCounter++;
            List<LabelSet> lset = lsg.getLabelSets();
            for (LabelSet ls : lset) {
                if (lsg.getType().equals("IfNotTheDefaultIsSelectedThenDone")) {
                    defaultInfoSb.append(ls.getLabels().get(0).getGuiName());
                }
                if (lsg.getType().equals("SelectOneAtLeast")) {
                    selectMinOneSb.append(grpCounter + ":");
                    selectMinOneSb.append(ls.getLabels().get(0).getGuiName());
                }

                selectMinOneSb.append(";");
                defaultInfoSb.append(";");
                added = true;
            }
        }
        // delete last ; separator
        if (added) {
            defaultInfoSb.deleteCharAt(defaultInfoSb.length()-1);
        }
        if (grpCounter > 0) {
            selectMinOneSb.deleteCharAt(selectMinOneSb.length()-1);
        }

        for (LabelSetGroup lsg : labelSetGroupList) {
            labelSetList.addAll(lsg.getLabelSets());
        }
        boolean reqWasAddedAtAll = false;
        int cnt = 0;
        for (LabelSet lset : labelSetList) {

            if (lset.isCommentRequired()) {
                needsCommSb.append(lset.getLabels().get(0).getGuiName());
            }
            needsCommSb.append(";");
            
            boolean reqWasAdded = false;
            if (lset.hasDefaultSource()) {
                String defS = lset.getDefaultSource();
                defValsSb.append(cnt + "-" + defS + ";");
            }
            if (lset.hasRequirements()) {
                List<String> reqsList = lset.getRequiredList();
                for (int i=0; i<reqsList.size(); i++) {
                    String req = reqsList.get(i);
                    String firstPart = req.substring(0,req.indexOf(':'));
                    // find this name within labelSets and get the index
                    int nth = -1;
                    for (int j=0; j<labelSetList.size();j++){
                        if (labelSetList.get(j).getName().equals(firstPart)) {
                            nth = j;
                            break;
                        }
                    }
                    if (nth==-1) {
                        System.out.println("Label for requirement \""
                                           + req + "\" is not found!");
                    } else {
                        // append part from ":"
                        reqSb.append(nth);
                        reqSb.append(req.substring(req.indexOf(':')) + ",");
                        reqWasAdded = true;
                        reqWasAddedAtAll = true;
                    }
                }
                if (reqWasAdded) {
                    // delete last , separator
                    reqSb.deleteCharAt(reqSb.length()-1);
                }
            }
            reqSb.append(";");
            cnt++;
        }

        // delete last ; separator
        if (reqWasAddedAtAll) {
            reqSb.deleteCharAt(reqSb.length()-1);
        }
        if (cnt > 0) {
            needsCommSb.deleteCharAt(needsCommSb.length()-1);
        }


        String requirements = reqSb.toString();
        String defaults = defaultInfoSb.toString();
        String needsComment = needsCommSb.toString();
        // debug
        //System.out.println(" \n\n\t reqstring:" + requirements);
        //System.out.println("DEFAULTINFO: " + defaultInfoSb.toString());
        try {
            // read js file
            FileInputStream fis  = new FileInputStream(new File("AssessmentUserInterface/WebContent/pages/formhandler.js"));
            BufferedReader in = new BufferedReader
                (new InputStreamReader(fis));

            // output jsf file
            File outFile = new File("AssessmentUserInterface/WebContent/pages/formhandler.jsp");
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter out = new PrintWriter(fos);

            // beginning of file: put <script> tags arounf javascript code
            out.println("<script type=\"text/javascript\">");
            // append the info about requirements 
            out.println("// This and the next 5 lines were generated from a config xml file"
                        + " by  de.l3s.liwa.assessment.Configuration.java");
            out.println("var ruleStr = \"" + requirements + "\";");
            out.println("var defStr = \"" + defaults + "\";");
            out.println("var defValuesStr = \"" + defValsSb.toString() + "\";");
            out.println("var needsCommentStr = \"" + needsComment + "\";");
            out.println("var selectMinOneStr = \"" + selectMinOneSb.toString() + "\";");
            // appending .js file's content
            String line = "";
            while ((line = in.readLine()) != null) {
                out.println(line);
            }

            out.println("</script>");

            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void parseSnapshots(Element hostAttributeElement){
        snapshotList = new ArrayList<Snapshot>() ;
        NodeList nodes = hostAttributeElement.getElementsByTagName("Snapshot");
        for (int i=0; i<nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                String snapshotName = getTextValue(child,"Name");
                String snapshotTimestamp = getTextValue(child,"Timestamp");
                Snapshot snapshot = new Snapshot();
                snapshot.setName(snapshotName);
                snapshot.setTimestamp( Long.valueOf(snapshotTimestamp) * 1000);

                //System.out.println("snapshot: name=" +snapshotName +" timestamp=" +snapshotTimestamp + " timestamp=" + snapshot.getTimestamp().toString() );
            }
        }
    }

    public List<HostAttributeType> getHostAttributeTypes() {
        return hostAttributeList;
    }

    public String getWarcBrowserUrl() {
        return warcBrowser;
    }

    public String getWaybackBrowserUrl() {
        return waybackBrowser;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public DataSource getDataSource() {
        if (dataSource instanceof DataBaseDataSource) {
            DataBaseDataSource newds = 
                new DataBaseDataSource(this);
            newds.setConnection(new ConnectToDb(connection));
            return newds;
        } else {
	    return dataSource;
	}
    }

    public ConnectToDb getDbConnection() {
        return connection;
    }

    public WebPageSource getWebPageSource() {
        return webPageSource;
    }

    public List<LabelSetGroup> getLabelSetGroups() {
        return labelSetGroupList;
    }

    public List<LabelSet> getLabelSets() {
        List<LabelSet> ret = new ArrayList<LabelSet>();
        for (LabelSetGroup lsg : labelSetGroupList) {
            List<LabelSet> set = lsg.getLabelSets();
            for (LabelSet ls : set) {
                ret.add(ls);
            }
        }
        return ret;
    }

    public List<Flag> getFlagList() {
        return flagList;
    }

    public List<String> getExcludedHostEndings() {
        return excludedHostListEndsWith;
    }

}
