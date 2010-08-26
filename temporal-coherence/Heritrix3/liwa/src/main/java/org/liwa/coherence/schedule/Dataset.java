package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
	private List<SchedulablePage> pages = new ArrayList<SchedulablePage>();

	public List<SchedulablePage> getPages() {
		return pages;
	}

	
	public void addPage(SchedulablePage p){
		pages.add(p);
	}
}
