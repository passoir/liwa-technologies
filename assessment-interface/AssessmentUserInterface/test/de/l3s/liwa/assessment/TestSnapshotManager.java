package de.l3s.liwa.assessment;

import java.util.List;
import java.util.ArrayList;
import junit.framework.*;
import junit.extensions.*;
import static junit.framework.Assert.*;


public class TestSnapshotManager extends TestCase {

    Configuration conf;

    public TestSnapshotManager(String name) {
        super(name);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {

    }

    public static Test suite() {
        return new TestSuite(TestSnapshotManager.class);
    }


    public void testGetSnapshot() {
        Configuration conf = Configuration.getTestInstance("sample_config2.xml");
        SnapshotManager snapshotManager = new SnapshotManager(conf);

        String url = "www.teamdiscovery.co.uk/information/news.php";
        ArrayList<Long> snapshots = snapshotManager.get(url);
        System.out.println("url: "+url);
        System.out.println("found: "+snapshots.size());
        for(Long snapshot : snapshots){
            System.out.println("snpashot="+snapshot);
        }

        url = "ahpr.co.uk/";
        snapshots = snapshotManager.get(url);
        System.out.println("url: "+url);
        System.out.println("found: "+snapshots.size());
        for(Long snapshot : snapshots){
            System.out.println("snpashot="+snapshot);
        }

    }

}
