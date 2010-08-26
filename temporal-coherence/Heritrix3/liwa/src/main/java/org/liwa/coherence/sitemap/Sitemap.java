package org.liwa.coherence.sitemap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sitemap {
	private String location;
	private Date lastModified;
	private List<PublishedUrl> urlList;
	
	public Sitemap(String location, Date lastModified) {
		super();
		this.location = location;
		this.lastModified = lastModified;
		this.urlList = new ArrayList<PublishedUrl>();
	}

	public Date getLastModified() {
		return lastModified;
	}

	public String getLocation() {
		return location;
	}

	public List<PublishedUrl> getUrlList() {
		return urlList;
	}
	
	
	
	public void setUrlList(List<PublishedUrl> urlList) {
		this.urlList = urlList;
	}

	public void addUrl(PublishedUrl url){
		urlList.add(url);
	}
	
	@Override
	public String toString() {
		return "Sitemap: " + location + ", lastmodified = " + lastModified + ", urls" + urlList; 
	}
}
