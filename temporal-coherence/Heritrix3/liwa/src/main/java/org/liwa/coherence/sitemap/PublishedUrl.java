package org.liwa.coherence.sitemap;

import java.util.Date;

public class PublishedUrl {
	private String location;
	private String changeRate;
	private Date lastModified;
	
	public PublishedUrl(String location, Date lastModified) {
		this(location, ChangeRate.YEARLY, lastModified);
	}
	
	public PublishedUrl(String location, String changeRate, Date lastModified) {
		super();
		this.location = location;
		this.changeRate = changeRate;
		this.lastModified = lastModified;
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
		return "[Url: "+location+ ", lastModified " + lastModified + ", changeFrequency " +changeRate +"]";
	}
}
