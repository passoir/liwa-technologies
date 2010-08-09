package de.l3s.liwa.assessment;

import java.util.Random;
import java.util.List;

public class RandomNextHostStrategy implements NextHostProvider {

    private final DataSource dataSource;  // provides data about users and hosts and labels
    private final int seed;  // random seed
    private final Random random;
    

    /** Constructor. Sets the random seed to 1. */
    public RandomNextHostStrategy(DataSource dataSource) {
        this.dataSource = dataSource;
        this.seed = 1;
        this.random = new Random(seed);
    }

    /** Constructor with random seed. */
    public RandomNextHostStrategy(DataSource dataSource, int randomSeed) {
        this.dataSource = dataSource;
        this.seed = randomSeed;
        this.random = new Random(seed);
    }

    // NextHostProvider interface:

    /** Gives a Host to the user to evaluate. Returns null if each of the hosts were evaluated by this user. */
    public Host getNextHost(User user) {
        int numberOfHosts = dataSource.getNumberOfHosts();
        // check if user has evaluated every possible host
        int numberOfEvaluatedHosts = dataSource.getNumOfHostsEvaluatedByUser(user);
        if (numberOfEvaluatedHosts >= numberOfHosts) {
            System.out.println("getNextHost returns null!");
            return null;
        }

        int randomIndex = random.nextInt(numberOfHosts) + 1; // id-s begin from 1, not 0 
        Host candidate = dataSource.getHost(randomIndex);
        while (dataSource.isHostLabelledByUser(candidate, user)) {
            // get the next candidate
            randomIndex = random.nextInt(numberOfHosts) + 1;
            candidate = dataSource.getHost(randomIndex);
        }
        return candidate;
    }

}