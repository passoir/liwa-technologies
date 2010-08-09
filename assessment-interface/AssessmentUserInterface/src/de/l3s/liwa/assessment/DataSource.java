package de.l3s.liwa.assessment;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

/** An abstract class which is able to handle data requests about Users and 
    Hosts. */

public abstract class DataSource {

    protected NextHostProvider provider;
    //    protected Configuration conf;
    protected int attribNo;
    protected List<HostAttributeType> attrTypes;
    protected List<LabelSetGroup> labelSetGroups;
    protected Map<String, LabelSet> labelsMap; // mapping labelSet's names to the labelSets themselves

    // Constructor
    public DataSource(Configuration conf) {
        //  this.conf = conf;
        attrTypes = conf.getHostAttributeTypes();
        attribNo = attrTypes.size();
        for (HostAttributeType type : attrTypes) {
            // fill static list of attribute types in Host
            Host.addHostAttributeType(type);
        }
        labelSetGroups = conf.getLabelSetGroups();
        Calendar cal = Calendar.getInstance();
        long time = cal.getTimeInMillis();
        int seed = (int)(time % 1000);
        //provider = new RandomNextHostStrategy(this, seed);
        provider = new SmartNextHostStrategy(this, seed);
        ((SmartNextHostStrategy)provider).setExcludedHostEndings(conf.getExcludedHostEndings());
        labelsMap = new HashMap<String, LabelSet>();
        for (LabelSetGroup lgroup : labelSetGroups) {
            for (LabelSet lset : lgroup.getLabelSets()) {
                labelsMap.put(lset.getName(), lset);
            }
        }
    }

    public Host getNextHost(User user) {
        Host nextHost = provider.getNextHost(user);
        if (nextHost != null) {
            return nextHost;
        }
        // no more hosts for tis user to evaluate
        return null; // what else could we do? an 'isNextHost' method?
    }

    public List<LabelType> getPossibleLabels(String labelSetName) {
        return (labelsMap.get(labelSetName)).getLabels();
    }

    public List<LabelSetGroup> getLabelSetGroups() {
        return labelSetGroups;
    }


    // abstract data queries

    public abstract boolean isValidUserPass(String username, String password);

    public abstract Host getHost(int id);

    public abstract Host getHost(String address);

    public abstract User getUser(int id);

    public abstract User getUser(String name, String password);

    public abstract User getUserByName(String username);

    public abstract boolean isHostLabelled(Host host);

    public abstract boolean isHostLabelledByUser(Host host, User user);
 
    public abstract List<Host> getLabelledHosts();

    public abstract Integer getNumOfLabelledHosts();

    public abstract Integer getNumOfLabelledHosts(String language);

    public abstract Integer getNumOfAllLabels();

    public abstract Integer getNumOfAllLabels(String language);

    public abstract List<Host> getHostsEvaluatedByUser(User user);

    public abstract Integer getNumOfHostsEvaluatedByUser(User user);

    //not used now but probably will be used by other nextHostStrategies
    // public abstract List<Host> getHostsWithConflictingLabels();

    public abstract List<Label> getLabelsOfHost(Host host);

    public abstract List<Label> getLabelsOfUser(User user);

    public abstract List<Label> getLabelsOfHostByUser(Host host, User user);

    public abstract List<String> getCommentsOfHost(Host host);

    public abstract int getNumberOfHosts();

    public abstract int getNumberOfHosts(String language);

    public abstract int getNumberOfNotExcludedHosts(String lang);

    public abstract List<String> getLinkedHosts(Host h);

    public abstract List<String> getLinkingHosts(Host h);

    public abstract List<String> getSamplePagesOfHost(Host h);

    public abstract List<Integer> getHostsToAssess(User user, int maxLabels);

    public abstract void addHostToAssess(User user, Host h);

    public abstract boolean hasHostToAssess(User user);

    public abstract Host getNextHostFromList(User user);


    // saving data, return true on success

    public abstract boolean saveLabel(Label label); // saves data to the label's user and host as well

    // other

}
