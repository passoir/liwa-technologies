package de.l3s.liwa.assessment;

/** Represents a type of data related to a Host. The concrete names 
    and types of attributes will be read from a config xml file. */
public class HostAttributeType {

    String attributeName; // The name displayed on the web-UI

    String attributeId; // The name of the corresponding column in the database (or csv file)
 
    AttributeType attributeType; // Type: 4 different possibilities

    // These types are allowed:
    public enum AttributeType {
        STRING, INT, DOUBLE, URL, URLLIST;
    }


    // Constructor.
    public HostAttributeType(String name, String id, String type) {
        this.attributeName = name;
        this.attributeId = id;
        this.setType(type);
    }

    public void setName(String attributeName){
        this.attributeName = attributeName;
    }

    public String getName() {
        return attributeName;
    }
    
    public void setId(String attributeId){
        this.attributeId = attributeId;
    }

    public String getId() {
        return attributeId;
    }

    public void setType(String typeString){
        if ( typeString.equals("String") ){
            attributeType = AttributeType.STRING; 
            return;
        }
        if ( typeString.equals("Double") ){
            attributeType = AttributeType.DOUBLE; 
            return;
        }
        if ( typeString.equals("Integer") ){
            attributeType = AttributeType.INT; 
            return;
        }
        if ( typeString.equals("Url") ){
            attributeType = AttributeType.URL; 
            return;
        }
        if ( typeString.equals("UrlList") ){
            attributeType = AttributeType.URLLIST; 
            return;
        }
        System.err.println("ERROR: attribute type \"" + typeString + "\" not is not a legal attribute type.");
    }

    public AttributeType getType() {
        return attributeType; 
    }

    public String getTypeString() {
        if (attributeType == AttributeType.STRING) {
            return "String";
        }
        if (attributeType == AttributeType.INT) {
            return "Integer";
        }
        if (attributeType == AttributeType.DOUBLE) {
            return "Double";
        }
        if (attributeType == AttributeType.URL) {
            return "Url";
        }
        if (attributeType == AttributeType.URLLIST) {
            return "UrlList";
        }
        return null;
    }
}