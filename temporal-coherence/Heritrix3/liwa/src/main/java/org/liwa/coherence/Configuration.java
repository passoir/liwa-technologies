package org.liwa.coherence;

public class Configuration {
	private RobotsListLoader robotsListLoader;
	private String selective;
	private String hottest;
	private String threshold;
	private String breadthFirst;
	private String highestPriority;
	
	private boolean deleteOldJobs=true;
	
	private int parallelSites = 5;
	private int runs;
	
	
	
	
	public int getParallelSites() {
		return parallelSites;
	}

	public void setParallelSites(int parallelSites) {
		this.parallelSites = parallelSites;
	}

	public boolean isDeleteOldJobs() {
		return deleteOldJobs;
	}

	public void setDeleteOldJobs(boolean deleteOldJobs) {
		this.deleteOldJobs = deleteOldJobs;
	}

	public String getHighestPriority() {
		return highestPriority;
	}

	public void setHighestPriority(String highestPriority) {
		this.highestPriority = highestPriority;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	public RobotsListLoader getRobotsListLoader() {
		return robotsListLoader;
	}

	public void setRobotsListLoader(RobotsListLoader sitemapListLoader) {
		this.robotsListLoader = sitemapListLoader;
	}

	public String getSelective() {
		return selective;
	}

	public void setSelective(String selective) {
		this.selective = selective;
	}

	public String getBreadthFirst() {
		return breadthFirst;
	}

	public void setBreadthFirst(String breadthFirst) {
		this.breadthFirst = breadthFirst;
	}

	public String getHottest() {
		return hottest;
	}

	public void setHottest(String hottest) {
		this.hottest = hottest;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}	
	
	
}
