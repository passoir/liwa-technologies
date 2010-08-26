package de.l3s.liwa.assessment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;

/**
 * Represents a Host with adress, labels, scores and revision labels
 * 
 * @author daniel, aszabo
 * 
 */
public class Host implements Serializable {
    

    private URL address; // with "http://"
    private String origAddress;
    private int id;
    // this will contain any kind of "scores"
    private ArrayList<HostAttribute> attributes;

    // global storage for the types of attributes
    private static ArrayList<HostAttributeType> attributeTypes = 
        new ArrayList<HostAttributeType>();

    // datasource object: delegate data-queries to this
    private DataSource dataSource;

    // the auto-detected language
    private String language;


    /**
     * Constructs a host with a DataSource and an ID. 
     * Gets address of host from the DataSource.
     */
    public Host(DataSource dataSource, int id, String addr, String lang) {
        this.dataSource = dataSource;
        this.id = id;
        origAddress = addr;
        language = lang;
        //String addr = dataSource.getHostAddress(id);
	this.attributes = new ArrayList<HostAttribute>();
        if (!addr.contains("://")) {
            addr = "http://" + addr;
        }        
	try {
	    this.address = new URL(addr);
        } catch (java.net.MalformedURLException ex) {
	    ex.printStackTrace();
        }
    }

    /** 
     * Adds a new attribute type to the "static" Host.
     */
    public static void addHostAttributeType(HostAttributeType type) {
        if (!attributeTypes.contains(type)) {
            attributeTypes.add(type);
        }
    }

    /** 
     * Adds a new attribute to the Host. 
     */
    public void addAttribute(HostAttribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Returns the ID of the Host.
     * 
     * @return The ID of the host
     */
    public int getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Returns the label address as String, extended with the protocol ("http://").
     * 
     * @return The address of the host
     */
    public String getAddress() {
        return address.toString();
    }

    /**
     * Returns the label address as String, as it was set.
     * 
     * @return The address of the host
     */
    public String getOrigAddress() {
        return origAddress;
    }

    /**
     * Returns all comments from this host as string.
     * 
     * @return The comments from this host as string
     */
    public String getCommentsAsString() {
	String commentString = "";
        List<Label> labels = dataSource.getLabelsOfHost(this);
        for (Label l : labels) {
            if (l.getComment() != null && l.getComment().trim().length() > 0 ) {
                commentString = commentString + l.getComment() + "; ";
            }
        }
	return commentString;
    }
    
    public ArrayList<HostAttribute> getAttributes() {
        return attributes;
    }


    public List<String> getComments() {
        return dataSource.getCommentsOfHost(this);
    }

    public List<String> getOutHosts() {
        return dataSource.getLinkedHosts(this);
    }

    public List<String> getInHosts() {
        return dataSource.getLinkingHosts(this);
    }

    public List<SamplePage> getSamplePages() {

        List<String> pages = dataSource.getSamplePagesOfHost(this);
        // create list of SamplePages Object to be able to 
        // keep the real url and another one that shows nicely 
        // (with line-breaks) on the ui.
        List<SamplePage> ret = new ArrayList<SamplePage>();

        for (int i=0; i < pages.size(); i++) {
            String orig = pages.get(i);
            String broken = addDashes(orig, 35);
            ret.add(new SamplePage(orig,broken));
        }
        return ret;
    }

    // Add dashes after every l characters without a dash or a slash
    private String addDashes(String input, int l) {

        if (input.length()<=l) 
            return input;

        // At first break at slashes
        String[] parts = input.split("/");
        for (int i=0; i<parts.length; i++) {

            if (parts[i].length()>l) {
                // if too long: break at dashes
                String[] parts2 = parts[i].split("-");
                for (int j=0; j<parts2.length; j++) {
                    if (parts2[j].length()>l) {
                        // if still too long: add new dashes in every l chars
                        int c = 1;
                        while (c*l < parts2[j].length()) {
                            parts2[j] = parts2[j].substring(0, c*l) +
                                "-" + parts2[j].substring(c*l);
                            c++;
                        }
                    }
                }
                // put the string together with the original "-"s
                StringBuffer sb2 = new StringBuffer();

                for (int j=0; j<parts2.length; j++) {
                    sb2.append(parts2[j]);
                    sb2.append("-");
                }
                // delete last
                sb2.deleteCharAt(sb2.length()-1);
                parts[i] = sb2.toString();
            }
        }

        // put the string together with the original "/"s
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<parts.length; i++) {
            sb.append(parts[i]);
            sb.append("/");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
