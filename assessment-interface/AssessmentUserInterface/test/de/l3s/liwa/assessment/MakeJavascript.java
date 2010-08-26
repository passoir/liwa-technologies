package de.l3s.liwa.assessment;
import junit.framework.*;
import junit.extensions.*;


/** Dummy class for generating the "formhandler "javascript file based on 
  * the configuration xml's content. */
public class MakeJavascript extends TestCase {

   public MakeJavascript(String name) {
        super(name);
    }


    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }

    protected void setUp() {
    }

    public static Test suite() {
        return new TestSuite(MakeJavascript.class);
    }

    public void testOne() {
        Configuration c = Configuration.getTestInstance("main_config.xml");
        System.out.println("Javascript generated.");
    }

}
