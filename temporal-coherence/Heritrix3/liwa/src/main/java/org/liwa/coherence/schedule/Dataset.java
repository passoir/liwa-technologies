package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dataset {
	private List<SchedulablePage> pages = new ArrayList<SchedulablePage>();
	private Map<String, SchedulablePage> pageMap = 
		new HashMap<String, SchedulablePage>();

	public List<SchedulablePage> getPages() {
		return pages;
	}

	public SchedulablePage getPage(String url) {
		return pageMap.get(url);
	}

	public void addPage(SchedulablePage p) {
		pages.add(p);
		pageMap.put(p.getUrl(), p);
	}
}
