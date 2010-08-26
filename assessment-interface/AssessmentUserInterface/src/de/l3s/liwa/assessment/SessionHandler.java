package de.l3s.liwa.assessment;

import java.util.List;
import java.util.ArrayList;
import javax.faces.event.ActionEvent;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputHidden;

/**
 * Managed Assessment Interface session
 * 
 * @author daniel, aszabo
 * 
 */
public class SessionHandler {

    private User user;
    private Host currentHost;
    private Host viewHost;
    private Host prevHost;
    private Host hostToAssess;  
    private String hostToAssessStr="";
    private String currentHostLabel;
    private String currentHostComment;

    private Configuration conf;
    private DataSource dataSource;

    private String loginPassword;
    private String loginName;
    private String propertyName;

    private String collectedLabels = "";

    private List<LabelSetGroup> labelSetGroups;
    private List<Flag> flagList;
    private Label lastLabel;

    // binding ui tables here to be able to follow clicks on host rows
    private HtmlDataTable labelsTable, flagsTable,
        dataTableOutLinks, dataTableInLinks, dataTableSamplePages; 
    private SnapshotManager snapshotManager;

    // this is to be able to indicate towards the user that the last
    // label could not be saved - there may be a database error
    private boolean wasLastSaveOk = true;

    /**
     * Constructs the session handler.
     */
    public SessionHandler() {
        conf = Configuration.getInstance();
        dataSource = conf.getDataSource();
        labelSetGroups = conf.getLabelSetGroups();
        flagList = conf.getFlagList();
        if (flagList==null || flagList.size()<1) {
            //System.out.println("NO FLAGS!!");
        }
        // setting sessionhandler and datasource for the label types
        for (LabelSetGroup lgroup: labelSetGroups) {
            List<LabelSet> labelSets = lgroup.getLabelSets();
            for (LabelSet ls : labelSets) {
                for (LabelType lt : ls.getLabels() ) {
                    lt.setSessionHandler(this);
                    lt.setDataSource(dataSource);
                }
            }
        }
        snapshotManager = new SnapshotManager(conf);
    }

    public LabelSetGroup[] getLabelSetGroups() {
        return (LabelSetGroup[]) labelSetGroups.toArray(new LabelSetGroup[labelSetGroups.size()]);
    }

    // not used yet
    public Flag[] getFlags() {
        return (Flag[]) flagList.toArray(new Flag[flagList.size()]);
    }

    public void actionNext() {
        saveAndNext();
    }

    public void actionBack() {
        saveAndBack();
    }

    /**
     * Returns the name of the session user.
     * @return The name of the session user
     */
    public String getUserName() {
        return user.getName();
    }

    public User getUser() {
        return user;
    }

    /**
     * Sets the login name (used form login.jsp).
     * @param name username
     */
    public void setLoginName(String name) {
        this.loginName = name;
    }

    /**
     * Returns the login name (used form login.jsp).
     * @return the user name
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * Sets the login password (used form login.jsp).
     * @param password the login password
     */
    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }

    /**
     * Returns an empty password (used form login.jsp).
     * @return
     */
    public String getLoginPassword() {
        return "";
    }

    /**
     * Starts the login. Initiates the User from the User class. 
     * Returns "success" if it was successful or "failure" if not. 
     * @return "success" if it was successful or "failure" if not
     */
    public String login() {

        if (dataSource.isValidUserPass(loginName, loginPassword)) {
            user = dataSource.getUser(loginName, loginPassword);
            currentHost = dataSource.getNextHost(user);
            viewHost = currentHost;
            prevHost = currentHost;
            if (currentHost != null && prevHost != null) {
                System.out.println("IN SessionHandler. CurrentHost : name: "
                                   + currentHost.getAddress() + "  prevHost : "
                                   + prevHost.getAddress());
            }
            return "success";
        } else {
            return "failure";
        }
    }

    /**
     * Returns the Host that has to be labeled next.
     * @return the next Host
     */
    public Host getCurrentHost() {
        return currentHost;
    }

    public void setCurrentHost(Host host) {
        prevHost = currentHost;
        viewHost = host;
        currentHost = host;
    }

    public Host getViewedHost() {
        return viewHost;
    }

    public void setViewedHost(Host host) {
        viewHost = host;
    }

    public String getViewedHostName() {
        if (viewHost==null) {
            viewHost = currentHost;
        }
        return viewHost.getOrigAddress();
    }

    public void setViewedHostName(String url) {
        viewHost = dataSource.getHost(url);
        setTablesToNull();
    }

    public void setHostToAssessStr(String hostUrl) {
        hostToAssessStr = hostUrl;
        //check is its a valid url
        try {
            java.net.URL url = new java.net.URL(hostUrl);
            if (url!=null) {
                hostToAssess = dataSource.getHost(hostUrl);
                if (hostToAssess == null) {
                    System.out.println("--- WRONG URL - not added to list");
                } else {
                    dataSource.addHostToAssess(user, hostToAssess);
                }
                hostToAssessStr = "";
                hostToAssess = null;
            }
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getHostToAssessStr() {
        return hostToAssessStr;
    }

    private void setTablesToNull() {
        this.dataTableSamplePages = null;
        this.dataTableInLinks = null;
        this.dataTableOutLinks = null;
    }

    public void setCurrentHostAddress(String url) {
        prevHost = currentHost;
        Host host = dataSource.getHost(url);
        currentHost = host;
        viewHost = host;
    }

    public String getCurrentHostAddress() {
        return currentHost.getAddress();
    }

    public void setAllLabels(String input) {
        collectedLabels = input;
    }

    public String getAllLabels() {
        return collectedLabels;
    }

    /**
     * Identifies the next Host and sets it to current Host.
     * @return "go_assessment"
     */
    public String nextHost() {
        Host nextHost = dataSource.getNextHost(user);
        if (nextHost == null) {
            return "go_hostsEmpty";
        }
        setCurrentHost(nextHost);
        return "go_assessment";
    }

    private String prevHost() {
        if (prevHost != null) {
            Host tmp = currentHost;
            currentHost = prevHost;
            viewHost = currentHost;
            prevHost = tmp;
        } else {
            prevHost = currentHost;
            viewHost = currentHost;
        }
        return currentHost.getAddress();
    }

    /**
     * Used from "assessment.jsp".
     * @return an empty String
     */
    public String getHostComment() {
        return "";
    }

    /**
     * Returns the current host's url
     * @return the current host's url
     */
    public String getCurrentHostUrl() {
        if (conf.getWebPageSource() == Configuration.WebPageSource.WAYBACK) {
            // wayback Machine
            System.err.println("getCurrentHostUrl returns : " +
                               getHostWaybackUrl(currentHost.getAddress(),
                                                 getWBDateString()));
            return getHostWaybackUrl(currentHost.getAddress(),
                                     getWBDateString());
        } else {
            // default: warc browser
            return getHostWarcUrl(currentHost.getOrigAddress());
        }
    }

    public String getHostWaybackUrl(String url, String date) { 
        return conf.getWaybackBrowserUrl() + date + "/" + url;
    }

    // TODO
    public String getWBDateString() {
        return "20100308191441";
    }

    public String getHostWarcUrl(String url) {
        // we need to go to a specific snapshot directly -> we need the 
        // timestamp for that
        List<Long> timestamps = snapshotManager.get(url);
        if (timestamps.size()<=0) {
            // return an error page
            return "error.jsp";
        } else {
            // go to the first snapshot directly
            List<Long> snapshots = 
                snapshotManager.get(currentHost.getOrigAddress());
            String ret = "http://" + conf.getWarcBrowserUrl() + "/archive/" 
                + snapshots.get(0) + "/" + url;
            //System.out.println(" getCurrentHostUrl returns: " + ret);
            return ret;
        }
    }

    /**
     * Sets the comment for the current host.
     * @param comment
     */
    public void setHostComment(String comment) {
        this.currentHostComment = comment;
    }

    /**
     * Sets the label for the current host.
     * @param label
     */
    public void setHostLabel(String label) {
        this.currentHostLabel = label;
    }

    /**
     * Saves the Label for the current host and returns the String for 
     * going to the next host.
     */
    public String saveAndNext() {
        wasLastSaveOk = saveCurrentLabel();
        return nextHost();
    }

    /**
     * Saves the Label for the current host and returns the String for 
     * going to the previous host.
     */
    public String saveAndBack() {
        wasLastSaveOk = saveCurrentLabel();
        return prevHost();
    }

    /**
     * Saves the Label for the current host.
     */
    public boolean saveCurrentLabel() {
        System.out.println("\n\t **** SH.saveLabel: \n collectedLabels: "
                           + collectedLabels);
        String comment;
        // a hack because String.split doesn't keep trailing empty fileds
        if (collectedLabels.endsWith(";;")) { // means empty comment at the end
            StringBuffer sb = new StringBuffer(collectedLabels);
            sb.deleteCharAt(sb.length()-1);
            sb.append("?;");
            comment = "";
            collectedLabels = sb.toString();
        } else {
            int where = collectedLabels.lastIndexOf(';', collectedLabels.length()-2);
            comment = collectedLabels.substring(where+1, collectedLabels.length()-1);
        }
        Label label = new Label(currentHost.getOrigAddress(), user.getId(),
                                comment, true);
        String[] splitLabels = collectedLabels.split(";");
        List<LabelSet> allLabelSets = new ArrayList<LabelSet>();
        for (int h=0; h<labelSetGroups.size(); h++) {
            allLabelSets.addAll(labelSetGroups.get(h).getLabelSets());
        }

        int inputSize = allLabelSets.size() + 1; //plus 1 for the comment field
        //+ flagList.size();
        int splitSize = splitLabels.length;

        if (inputSize != splitSize) {
            System.out.println("Error in SessionHandler.saveCurrentLabel: "
                               + "labelset sizes do not match!"
                               + "  inputSize: " + inputSize 
                               + " splitsize: " + splitSize);
            return false;
        }

        // fill labels
        for (int i=0; i < allLabelSets.size(); i++) {
            label.setLabel(allLabelSets.get(i).getName(), splitLabels[i]);
        }
        // !! check if spam ! and save lang.
        // ! this will not work if label names are changed in the config
        if (label.getLabel("Web Spam").equals("Spam")) {
            label.setLabel("Language", user.getLanguage());
        }
        boolean success = dataSource.saveLabel(label);
        return success;
    }

    public HtmlDataTable getOutLinksTable() {
        if (dataTableOutLinks == null) {
            dataTableOutLinks = new HtmlDataTable();
        }
        return dataTableOutLinks;
    }

    public void setOutLinksTable(HtmlDataTable table) {
        dataTableOutLinks = table;
    }

    public HtmlDataTable getInLinksTable() {
        if (dataTableInLinks == null) {
            dataTableInLinks = new HtmlDataTable();
        }
        return dataTableInLinks;
    }

    public void setInLinksTable(HtmlDataTable table) {
        dataTableInLinks = table;
    }

    public HtmlDataTable getLabelsTable() {
        if (labelsTable == null) {
            labelsTable = new HtmlDataTable();
        }
        return labelsTable;
    }

    public void setLabelsTable(HtmlDataTable table) {
        labelsTable = table;
    }

    public HtmlDataTable getFlagsTable() {
        if (flagsTable == null) {
            flagsTable = new HtmlDataTable();
        }
        return flagsTable;
    }

    public void setFlagsTable(HtmlDataTable table) {
        flagsTable = table;
    }

    public HtmlDataTable getSamplePagesTable() {
        if (dataTableSamplePages == null) {
            dataTableSamplePages = new HtmlDataTable();
        }
        return dataTableSamplePages;
    }

    public void setSamplePagesTable(HtmlDataTable table) {
        dataTableSamplePages = table;
    }

    public String getWarcUrl() {
        return conf.getWarcBrowserUrl();
    }

    public String getUsersLanguage() {
        return user.getLanguage();
    }

    public String getSaveSuccess() {
        if (wasLastSaveOk) return "true";
        else return "false";
    }

}
