package de.l3s.liwa.assessment;

import java.net.URL;

/** Contains a specific type of information about a host. The concrete names 
    and types of attributes will be read from a config xml file. */
public class HostAttribute {

    /** Stores names and the type of the value object. */
    HostAttributeType type;  
    
    /** This will be a String or Integer or Double or URL or List<Url> object,
        corresponding to the "type". */
    Object value; 


    public HostAttribute (HostAttributeType type, Object value) {
        // TODO : check if type corresponds to the class of "value".
        this.type = type;
        this.value = value;
    }

    public String getHtmlString() {
        // TODO: UrlList
        if (type.getType() == HostAttributeType.AttributeType.URL){
            return "<a href=" + value.toString() +" >";
        } else {
            return value.toString();
        }
    }

    // used in the web user interface to display in tables
    public String getUiString() {
        return value.toString();
    }

    public HostAttributeType getType() {
        return type;
    }
    
    public void setType (HostAttributeType t) {
        type = t;
    }
}

