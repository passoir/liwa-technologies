package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dataset {
	private List<SchedulablePage> pages = new ArrayList<SchedulablePage>();
	private Map<Integer, SchedulablePage> pageMap = 
		new HashMap<Integer, SchedulablePage>();

	public List<SchedulablePage> getPages() {
		return pages;
	}

	public SchedulablePage getPage(Integer url) {
		return pageMap.get(url);
	}

	public void addPage(SchedulablePage p) {
		pages.add(p);
		pageMap.put(p.getPublishedUrlId(), p);
	}
}
