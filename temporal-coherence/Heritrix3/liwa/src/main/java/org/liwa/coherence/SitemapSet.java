package org.liwa.coherence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.liwa.coherence.sitemap.CompressedUrl;
import org.liwa.coherence.sitemap.PublishedUrl;

public class SitemapSet {
	private String domain;
    private List<PublishedUrl> urls = new ArrayList<PublishedUrl>();
	
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public List<PublishedUrl> getUrls() {
		return urls;
	}
	public void addUrl(PublishedUrl url) {
		urls.add(url);
	}	
}
