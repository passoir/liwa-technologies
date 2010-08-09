package de.l3s.liwa.assessment;

import java.util.List;
import java.util.ArrayList;

/** Represents a set of values from what a single one element can be chosen on the web-ui. */
public class LabelSet {
    String name; // name of the labelset; for example: "Language"
    // This list will containl labels like "English", "German", etc.
    ArrayList<LabelType> typesList; 

    // List of requirements to make this labelset enabled on the UI. 
    ArrayList<String> requiredList;

    // If the default falue depends on something from the java side, 
    // this string contains the ID of the (hidden) html element that 
    // will communicate the necessary information to the javascript 
    // part (so that the JS can set the default value by default).
    String defaultSource = "";

    // If this is set to true then in case of non-default 
    // selection the comment must be filled on the web-ui.
    boolean commentRequired = false; 

    String tooltip = "";
    
    public LabelSet(String name) {
        this.name = name;
        typesList = new ArrayList<LabelType>();
        requiredList = new ArrayList<String>();
    }
    
    public String getName() {
        return name;
    }

    public void addLabel(LabelType t) {
        typesList.add(t);
    }
    
    public LabelType getLabel(int i) {
        return typesList.get(i);
    }

    public LabelType getLabel(String labelname) {
        for (int i=0; i<typesList.size(); i++) {
            if (typesList.get(i).getLabel().equals(labelname)) {
                return typesList.get(i);
           }
        }
        return null;
    }

    public List<LabelType> getLabels() {
        return typesList;
    }
 
    public void setRequired(String val) {
        requiredList.add(val);
    }
 
    public List<String> getRequiredList() {
        return requiredList;
    }
    
    public boolean hasRequirements() {
        if (requiredList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasDefaultSource() {
        if (defaultSource.length() > 0) {
            return true; 
        } else {
            return false;
        }
    }
    
    public String getDefaultSource() {
        return defaultSource;
    }

    public void setDefaultSource(String ds) {
        this.defaultSource = ds;
    }
    
    public boolean isCommentRequired() {
        return commentRequired;
    }

    public void setCommentRequired(boolean c) {
        commentRequired = c;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String t) {
        tooltip = t;
    }
}
