package de.l3s.liwa.assessment;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/**
 * This strategy gives random hosts to assess at the beginning and 
 * after enough different hosts have been labelled, it gives 
 * the assessed hosts to a small number of different other users 
 * to produce a more reliable dataset.
 */
public class SmartNextHostStrategy implements NextHostProvider {

    // provides data about users and hosts and labels
    private final DataSource dataSource; 
    private Random random;

    // new: separate values for each language (en, de, fr)
    private Map<String, Integer> numLabelsForAHostMap = 
        new HashMap<String, Integer>(); 
    private Map<String, Integer> minDifferentHostsMap = 
        new HashMap<String, Integer>(); 

    // excluded hosts
    private List<String> excludeHostEndings;

    // to check if we can use the cached numbers
    private User lastUser;

    // Caching these numbers so that we dont have to call the database for each getNextHost()
    // number of not excluded different hosts of the users language
    private int numberOfHosts = -3;
    // number of all hosts (generate random id in the range)
    private int numOfAllHosts = -3;
    private int numOfEvalHostsByUser = -3;
    // number of different hosts that got at least one label with that lang.
    private int numOfAssessedHosts = -3;
    // number of all labels with that language
    private int numOfLabels = -3;

    private List<Integer> candidates; // candidate hosts to assess for the last a user

    // querying the database when the counter reaches 0 - to sync real numbers
    private int maxCounter = 15;
    private int counter = maxCounter;

    // hack
    private ArrayList<Host> probHosts;
    private int pcounter = 0;

    /** Constructor. Sets the random seed. */
    public SmartNextHostStrategy(DataSource dataSource, int seed) {
        this.dataSource = dataSource;
        this.random = new Random(seed);
        // fill languages-related variables
        numLabelsForAHostMap.put("en", 2);
        minDifferentHostsMap.put("en", 2500);
        numLabelsForAHostMap.put("de", 2);
        minDifferentHostsMap.put("de", 2500);
        numLabelsForAHostMap.put("fr", 2);
        minDifferentHostsMap.put("fr", 2500);

    }


    // querying database
    private void getDataFromDataBase(User user) {
        System.out.println(" NextHostToAssess: getDataFromDatabase");
        String lang = user.getLanguage();
        counter = maxCounter;
        numberOfHosts = dataSource.getNumberOfNotExcludedHosts(lang);
        numOfEvalHostsByUser = dataSource.getNumOfHostsEvaluatedByUser(user);
        numOfAllHosts = dataSource.getNumberOfHosts();
        int numOfAssessedHosts = dataSource.getNumOfLabelledHosts(lang);

        int numOfLabels = dataSource.getNumOfAllLabels(lang);

        // check if the values can be increased (if both requirements are ok)
        if (numOfLabels > minDifferentHostsMap.get(lang)
            * numLabelsForAHostMap.get(lang)) {
            // if we have so many labels: increase value
            minDifferentHostsMap.put(lang, 2 * minDifferentHostsMap.get(lang));
            numLabelsForAHostMap.put(lang, numLabelsForAHostMap.get(lang)+1 );
            System.out.println(" SmartNextHostStrategy: new values: MinDiffHosts: "
                               + minDifferentHostsMap.get(lang)
                               + "  required labels for each: "
                               + numLabelsForAHostMap.get(lang));
        }
    }
    
    // NextHostProvider interface:

    /** Gives a Host to the user to evaluate. Returns null if each of the hosts 
        were evaluated by this user. */
    public Host getNextHost(User user) {

        // If the user's saved queue of hosts to assess is not empty...
        if (dataSource.hasHostToAssess(user)) {
            Host ret = dataSource.getNextHostFromList(user);
            if (ret != null) {
                System.out.println("Returning host to assess from the " +
                                   "saved queue: " + ret.getOrigAddress());
                return ret;
            }
        }

        boolean newUser = false;
        // checking last user and counter
        if (lastUser != null) {
            if (user.getId() != lastUser.getId() ||
                counter <= 0) {
                getDataFromDataBase(user);
                lastUser = user;
                counter = maxCounter;
                newUser = true;
            } else {
                System.out.println(" SKIPPED NextHostToAssess: getDataFromDatabase");
                // using cached numbers, no db query
            }     
        } else {
            getDataFromDataBase(user);
            lastUser = user;
            counter = maxCounter;
            newUser = true;
        }

        // check if user has evaluated every possible host
        if (numOfEvalHostsByUser >= numberOfHosts) {
            System.out.println("getNextHost returns null!");
            return null;
        }

        // otherwise return a host with the users language
        String lang = user.getLanguage();

        // check if we have enough assessed hosts so far
        if (numOfAssessedHosts < minDifferentHostsMap.get(lang)) {
            // give a new, random host to assess
            stepCounter();
            return getNextRandomHost(user, numberOfHosts);

        } else {
            // give a host that was assessed by another user, but not this user,
            // and at most "numLabelForAHost"
            // getting all assessed hosts with less than x labels
            if (newUser) { // need to update candidate list
                candidates =
                    dataSource.getHostsToAssess(user, numLabelsForAHostMap.get(lang));
            }

            if (candidates.size() > 0) {
                // choose a random one
                int randomIndex = random.nextInt(candidates.size());
                int hostID = candidates.get(randomIndex);
                candidates.remove(randomIndex);
                stepCounter();
                return dataSource.getHost(hostID);
            } else { // no candidates : give a random host
                stepCounter();
                return getNextRandomHost(user, numberOfHosts);
            }
        }        
    }

    // increase cached numbers, decrease counter
    private void stepCounter() {
        counter--;
        numOfEvalHostsByUser++;
        numOfAssessedHosts++;
        numOfLabels++;
    }


    private Host getNextRandomHost(User user, int numberOfHosts) {
        // id-s begin from 1, not 0 
        int randomIndex = random.nextInt(numOfAllHosts) + 1;
        Host candidate = dataSource.getHost(randomIndex);
        while (dataSource.isHostLabelledByUser(candidate, user) ||
               !candidate.getLanguage().equals(user.getLanguage()) ||
               isExcluded(candidate)) {
            // get the next candidate
            randomIndex = random.nextInt(numOfAllHosts) + 1;
            candidate = dataSource.getHost(randomIndex);
        }
        return candidate;
    }

    private boolean isExcluded (Host h) {
        for (int i=0; i<excludeHostEndings.size(); i++) {
            if (h.getOrigAddress().endsWith(excludeHostEndings.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void setExcludedHostEndings(List<String> ends) {
        this.excludeHostEndings = ends;
    }


}