package org.liwa.coherence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;

import org.liwa.coherence.sitemap.PublishedUrl;

public class SitemapsBean {
	private List<String> sitemaps;

	public List<String> getSitemaps() {
		return sitemaps;
	}

	public void setSitemaps(List<String> sitemaps) {
		this.sitemaps = sitemaps;
	}

	public SitemapSet loadSitemapSet(String sitemap) {
		SitemapSet sitemapSet = new SitemapSet();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					sitemap)));
			String line = reader.readLine();
			String domain = line.substring(0, line.indexOf(" "));
			URL u = new URL(domain);
			sitemapSet.setDomain(u.getHost());
			while(line != null){
				String[] tokens = line.split(" ");
				PublishedUrl url = new PublishedUrl(tokens[0], tokens[1], null, Double.parseDouble(tokens[2]));
				sitemapSet.addUrl(url);
				line = reader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sitemapSet;
	}

}
