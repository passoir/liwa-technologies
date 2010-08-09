package de.l3s.liwa.assessment;

import java.util.List;
import junit.framework.*;
import junit.extensions.*;
import static junit.framework.Assert.*;


public class TestDataSources extends TestCase {

    Configuration confdb;
    DataBaseDataSource dsdb;


    public TestDataSources(String name) {
        super(name);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {
        confdb = Configuration.getTestInstance("test_database_config.xml");
        dsdb = (DataBaseDataSource)(confdb.getDataSource());
    }

    public static Test suite() {
        return new TestSuite(TestDataSources.class);
    }


    /*    public void testAttributeTypes() {
        List<HostAttributeType> attrTypes = confdb.getHostAttributeTypes();
        assertEquals(attrTypes.size(), 5);
        for (int i= 0; i < attrTypes.size(); i++) {
            HostAttributeType hat = attrTypes.get(i);
            //System.out.println("Att name: " + hat.getName() + " type: " + 
            //                   hat.getTypeString());
            if (i==2) {
                assertEquals(hat.getName(),"MaxPrPageInLinks");
                assertEquals(hat.getTypeString(),"Integer");
            }
        }
    }
    */

    // test if the User objects are created correctly
    public void testUserObject() {
        System.err.println("testUserObject");
        // get user with id = 2
        //User user1 = ds1.getUser(2);
        User user2 = dsdb.getUser(2);
        assertEquals("b", user2.getName());
    }

    // test if the Host objects are created correctly
    public void testHostObject() {
        Host host = dsdb.getHost("http://www.neunow.eu");
        assertEquals("http://www.neunow.eu", host.getAddress());
        assertEquals(0, host.getAttributes().size());
    }

    /*    public void testLabelObject() {
        User userb = dsdb.getUserByName("b");
        Host host3 = dsdb.getHost("ahpr.co.uk");
        assertEquals("http://ahpr.co.uk", host3.getAddress());
        assertEquals(true, dsdb.isHostLabelledByUser(host3, userb));
    }
    */

    /*    public void testHostLinks() {
        Host host2 = dsdb.getHost("http://ahpr.co.uk");
        List<Host> outhosts = dsdb.getLinkedHosts(host2);
        List<Host> outhosts2 = host2.getOutHosts();
        assertEquals(outhosts.size(), outhosts2.size());

        }*/

    /*  public void testLabels() {
        // find an existing "last" label or create one if there is no label in the database
        Host host =  dsdb.getHost(3);
        List<Label> labels = dsdb.getLabelsOfHost(host);
        Label origlabel = null;  // the label to "overwrite" (to create a newer of its kind)

        // if the host has labels then we get one with "isLast == true"
        for (Label label : labels) {
            if (label.isLast()) {
                origlabel = label;
                break;
            }
        }
        
        if (origlabel == null) { 
            // create a label
            origlabel = new Label(host.getOrigAddress(), 2, "spam", "comment", true);
            // save it
            dsdb.saveLabel(origlabel);
        }
        
        // now we create another Label, with similar parameters (same host and user) 
        // and we will check if origlabel becomes non-last
        Label newlabel = new Label(origlabel.getHostUrl(), origlabel.getUserId(), "borderline", 
                                   "newcomment", true);
        // save new label
        dsdb.saveLabel(newlabel);
        
        // the new list of labels
        List<Label> labels2 = dsdb.getLabelsOfHost(host);
        boolean foundNewLabel = false;
        Label origlabel2 = null;
        // check new label and origlabel
        for (Label label : labels2) {
            if (label.getUserId() == newlabel.getUserId() &&
                label.getHostUrl().equals(newlabel.getHostUrl()) &&
                label.isLast()) {

                if ( (label.getComment()==null && newlabel.getComment()==null) || 
                     (label.getComment()!=null && newlabel.getComment()!=null &&
                      label.getComment().equals(newlabel.getComment())) ) {
                    
                    foundNewLabel = true;
                    if (origlabel2 != null) {
                        break;
                    }
                }
            }
            // orig label
            if (label.getUserId() == origlabel.getUserId() &&
                label.getHostUrl().equals(origlabel.getHostUrl()) &&
                label.getLabel().equals(origlabel.getLabel())) {
                
                if ( (label.getComment()==null && origlabel.getComment()==null) || 
                     (label.getComment()!=null && origlabel.getComment()!=null &&
                      label.getComment().equals(origlabel.getComment())) ) {

                    origlabel2 = label;
                    if (foundNewLabel) {
                        break;
                    }
                }
            }
        }
        if(!foundNewLabel) {
            fail("New label was not saved into the database!");
        }
        // check that origlabel is not last
        if (origlabel2.isLast()) {
            fail("An older label was not updated correctly, it is still 'isLast'");
        }
             
        }*/

}
