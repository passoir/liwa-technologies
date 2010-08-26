package de.l3s.liwa.assessment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents labels for a host
 * @author daniel, aszabo
 *
 */
public class Label implements  Serializable{
    private String hostUrl;
    private int userId;
    private Map<String,String> labels;
    private String comment;
    private long datestamp;
    private boolean isLast;


    /**
     * Constructs a new label with a host, user, and comment.
     * @param hostUrl The assessed host's URL
     * @param userId The user's ID who created this label
     * @param comment The comment from the user
     * @param isLast Specifies if this is tha last label from the 
     *        current user regarding the current host
     */
    public Label(String hostUrl, int userId, String comment, boolean isLast) {
	this.hostUrl = hostUrl;
	this.userId = userId;
        this.labels = new HashMap<String,String>();
        this.isLast = isLast;
        if (comment == null) {
            this.comment = "";
        } else {
            this.comment = comment;
        }
    }

    /**
     * Returns the host URL.
     * @return The host URL
     */
    public String getHostUrl() {
	return hostUrl;
    }

    /**
     * Sets the host URL.
     * @param host URL
     */
    public void setHostUrl(String host) {
	this.hostUrl = host;
    }

    /**
     * Returns the user ID (the ID of the user who created this label).
     * @return the user ID
     */
    public int getUserId() {		
	return userId;
    }

    /**
     * Returns the label as String
     */
    public String getLabel(String labelname) {
	return labels.get(labelname);
    }

    /**
     * Sets the label
     * @param label
     */
    public void setLabel(String name, String value) {
	labels.put(name, value);
    }

    /**
     * Returns the comment as String.
     * @return The comment as String
     */
    public String getComment() {
	return comment;
    }

    /**
     * Sets the comment.
     * @param comment
     */
    public void setComment(String comment) {
        if (comment !=null) {
            this.comment = comment;
        }
    }

    public boolean isLast() {
        return isLast;
    }
		
    public void setNonLast() {
        isLast = false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("LABEL user: " + userId +  "  url: ");
        sb.append(hostUrl);
        for (String l: labels.keySet()) {
            sb.append("\n\t" + l + " - " + labels.get(l));
        }
        return new String(sb);
    }

}
