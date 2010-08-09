package de.l3s.liwa.assessment;

import java.util.List;
import java.util.ArrayList;
import junit.framework.*;
import junit.extensions.*;
import java.io.*;

public class SnapshotManager {
    Configuration configuration;

    public SnapshotManager(Configuration _configuration){
        configuration = _configuration;
    }

    public ArrayList<Long> get(String host){
        return getBashWget(host);
    }

    private ArrayList<Long> getBashWget(String host){
        ArrayList<Long> result = new ArrayList<Long>();
        String s = null;
        String warcUrl = configuration.getWarcBrowserUrl();
        
        try {
            
            String[] command = new String[]{ "/bin/bash", "-c", "TMP=`mktemp`; wget "+warcUrl+"/archive/*/"+host+" -O $TMP -o /dev/null; cat $TMP | grep \""+host+"\" | awk -v FS='\"' 'NR>1{print $2}' | awk -v FS='/' '{print $3}' ; rm $TMP" };

            Process p = Runtime.getRuntime().exec(command);
            
            BufferedReader stdInput = new BufferedReader(new 
                                                         InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                                                         InputStreamReader(p.getErrorStream()));
            
            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                result.add(Long.valueOf(s));
            }
            
            while ((s = stdError.readLine()) != null) {
                //System.out.println(s);
            }

        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }

        return result;
    }
}
