package org.liwa.coherence.sitemap;

import java.util.Date;

public class PublishedUrl {
	private String location;
	private String changeRate;
	private Date lastModified;
	private double priority = 1.0;
	
	public PublishedUrl(String location, Date lastModified) {
		this(location, ChangeRate.YEARLY, lastModified);
	}
	
	public PublishedUrl(String location, String changeRate, Date lastModified) {
		this(location, changeRate, lastModified, 0.5);
	}
	
	public PublishedUrl(String location, String changeRate, Date lastModified, double priority) {
		super();
		this.location = location;
		this.changeRate = changeRate;
		this.lastModified = lastModified;
		this.priority = priority;
	}
	
	
	
	
	public String getChangeRate() {
		return changeRate;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public String getLocation() {
		return location;
	}
	
	
	
	public void setChangeRate(String changeRate) {
		this.changeRate = changeRate;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Url: "+location+ ", lastModified " + lastModified + ", changeFrequency " +changeRate +", priority " +priority+"]";
	}

	public double getPriority() {
		return priority;
	}
	
	public void setPriority(double priority){
		this.priority = priority;
	}
}
