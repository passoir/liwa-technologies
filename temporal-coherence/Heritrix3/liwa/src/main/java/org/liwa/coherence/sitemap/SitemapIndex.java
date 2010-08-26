package org.liwa.coherence.sitemap;

import java.util.ArrayList;
import java.util.List;

public class SitemapIndex {
	private List<String> locations;
	public SitemapIndex(){
		locations = new ArrayList<String>();
	}
	
	public void addLocation(String location){
		locations.add(location);
	}
}
