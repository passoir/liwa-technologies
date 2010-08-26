package de.l3s.liwa.assessment;

import java.util.List;
import javax.faces.model.SelectItem;


public class LabelType extends SelectItem {

    // SelectItem has getters/setters for: String label, value, description; bool disabled

    SessionHandler shandler; // to be able to answer some questions about number of labels of hosts without getting parameters (because jsf does not support that.)
    DataSource dataSource;

    public LabelType(String name, String labelStr) {
        super.setLabel(name);
        this.setValue(labelStr);
    }
    
    public String getGuiName() {
        return (String)super.getValue();
    }

    public void setLabel(String label) {
        super.setLabel(label);
    }
    
    public String getLabel() {
        return super.getLabel();
    }
    
    public void setGuiName(String name) {
        super.setValue(name);
    }

    public void setSessionHandler(SessionHandler sh) {
        shandler = sh;
    }

    public void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    // queries from the web-ui
    /*
    public int getCurrentHostsLabelNum() {
        Host currentHost = shandler.getCurrentHost();
        if (currentHost == null) {
            System.out.println("currentHost in LabelType null!!");
        }
        List<Label> labels = dataSource.getLabelsOfHost(currentHost);
        int ret = 0;
        for (Label l : labels) {
            if (l.getLabel().equals(this.getValue())) {
                ret++;
            }
        }
        return ret;
        }*/

}
