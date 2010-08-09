package de.l3s.liwa.assessment;

import java.util.List;
import java.util.ArrayList;

public class LabelSetGroup {
    String type=""; // for example: "IfNotTheDefaultIsSelectedThenDone", "Dependant"
    ArrayList<LabelSet> labelSetList; // will containl label-sets

    public LabelSetGroup() {
        labelSetList = new ArrayList<LabelSet>();
    }
    
    public String getType() {
        return type;
    }

    public void setType(String t) {
        this.type = t;
    }

    public void addLabelSet(LabelSet s) {
        labelSetList.add(s);
    }
    
    public LabelSet getLabelSet(int i) {
        return labelSetList.get(i);
    }

    public List<LabelSet> getLabelSets() {
        return labelSetList;
    }
 
}
