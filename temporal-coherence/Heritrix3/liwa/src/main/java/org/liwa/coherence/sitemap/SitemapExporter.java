package org.liwa.coherence.sitemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SitemapExporter implements SitemapHandler {
	private List<String> sitemapUrls;

	private Map<String, List<PublishedUrl>> map;

	private Set<String> urls;
	private String fileName = "bbc";
	private String path = "seeds";
	
	
	private int size = 500;

	public SitemapExporter() {
		map = new HashMap<String, List<PublishedUrl>>();
		urls = new HashSet<String>();
	}

	public int saveUrl(String url, String frequency, double priority,
			Date lastModified) {
		if (urls.add(url)) {
			PublishedUrl pUrl = new PublishedUrl(url, frequency, lastModified,
					priority);
			String key = frequency + " " + priority;
			List<PublishedUrl> groupedUrl = map.get(key);
			if(groupedUrl == null){
				groupedUrl = new ArrayList<PublishedUrl>();
				map.put(key, groupedUrl);
			}
			groupedUrl.add(pUrl);
		}
		return 0;
	}

	public void loadSitemaps() {
		SitemapLoader.loadCompressedUrls(sitemapUrls, this);
	}
	
	public void export() throws Exception{
		File f = new File(path, fileName);
		PrintStream ps = new PrintStream(new FileOutputStream(f));
		List<String> keys = new ArrayList<String>();
		keys.addAll(map.keySet());
		List<PublishedUrl> filtered = new ArrayList<PublishedUrl>();
		int keyIndex = 0;
		int urlsLeft = size;
		while(filtered.size() < 200 && urlsLeft > 0 && keys.size() > 0){
			if(keyIndex >= keys.size()){
				keyIndex = 0;
			}
			List<PublishedUrl> urlSet = map.get(keys.get(keyIndex));
			if(urlSet.size() > 0){
				PublishedUrl u = urlSet.remove(0);
				filtered.add(u);
				ps.println(u.getLocation() + " " + u.getChangeRate() + " "  + u.getPriority());
				urlsLeft--;
			}
			keyIndex++;
		}
	}

	public static void main(String[] args) {
		SitemapExporter exporter = new SitemapExporter();
		List<String> sitemaps = new ArrayList<String>();
		exporter.fileName = args[0];
		for(int i = 1; i < args.length; i++){
			sitemaps.add(args[i]);
		}
		
		exporter.sitemapUrls = sitemaps;
		exporter.loadSitemaps();
		try {
			exporter.export();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
