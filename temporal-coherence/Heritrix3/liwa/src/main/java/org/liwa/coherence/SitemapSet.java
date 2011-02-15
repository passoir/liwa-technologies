package org.liwa.coherence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.liwa.coherence.sitemap.CompressedUrl;

public class SitemapSet {
	private String domain;
	private List<String> sitemaps;
	private List<CompressedUrl> urls;
	
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public List<String> getSitemaps() {
		return sitemaps;
	}
	public void setSitemaps(List<String> sitemaps) {
		this.sitemaps = sitemaps;
	}
	public List<CompressedUrl> getUrls() {
		return urls;
	}
	public void setUrls(List<CompressedUrl> urls) {
		Map<String, List<CompressedUrl>> groupedUrls = new HashMap<String, List<CompressedUrl>>();
		for(CompressedUrl url: urls){
			String key = url.getChangeRate()+ " " + url.getPriority();
			List<CompressedUrl> urlSet = groupedUrls.get(key);
			if(urlSet == null){
				urlSet = new ArrayList<CompressedUrl>();
				groupedUrls.put(key, urlSet);
			}
			urlSet.add(url);
		}
		
		List<String> keys = new ArrayList<String>();
		keys.addAll(groupedUrls.keySet());
		this.urls = new ArrayList<CompressedUrl>();
		int keyIndex = 0;
		int urlsLeft = urls.size();
		while(this.urls.size() < 200 && urlsLeft > 0){
			if(keyIndex >= keys.size()){
				keyIndex = 0;
			}
			List<CompressedUrl> urlSet = groupedUrls.get(keys.get(keyIndex));
			if(urlSet.size() > 0){
				this.urls.add(urlSet.remove(0));
				urlsLeft--;
			}
			keyIndex++;
		}
	}
	
	

}
