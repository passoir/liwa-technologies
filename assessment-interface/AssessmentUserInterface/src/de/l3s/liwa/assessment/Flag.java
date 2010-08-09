package de.l3s.liwa.assessment;

import java.util.List;
//import javax.faces.model.SelectItem;
//import javax.faces.component.UIInput;



public class Flag /*extends UIInput*/ {

    String name;
    String nameOnUI;
    String value;

    public Flag(String name, String nameOnUI, String valueInDB) {
        this.name = name;
        this.nameOnUI = nameOnUI;
        this.value = valueInDB;
        //this.setValue(valueInDB);
    }
    
    public String getNameOnUI() {
        return nameOnUI;
    }
 
    public void setNameOnUI(String name) {
        //super.setValue(name);
        this.nameOnUI = name;
    }

    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String v) {
        value = v;
    }
}
