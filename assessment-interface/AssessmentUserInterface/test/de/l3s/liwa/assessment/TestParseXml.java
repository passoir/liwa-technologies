package de.l3s.liwa.assessment;

import java.util.List;
import junit.framework.*;
import junit.extensions.*;

public class TestParseXml extends TestCase {

    public TestParseXml(String name) {
        super(name);
    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {
    }

    public static Test suite() {
        return new TestSuite(TestParseXml.class);
    }

    public void testParse() {
        Configuration c = Configuration.getTestInstance("sample_config.xml");
    }

    public void testParse2() {
        Configuration c = Configuration.getTestInstance("sample_config2.xml");
    }

}
