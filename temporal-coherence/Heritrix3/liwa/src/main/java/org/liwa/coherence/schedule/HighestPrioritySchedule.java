package org.liwa.coherence.schedule;

import java.util.Collections;


public class HighestPrioritySchedule extends OfflineSchedule {

	@Override
	public void setDatasetProvider(DatasetProvider datasetProvider) {
		super.setDatasetProvider(datasetProvider);
		Collections.sort(this.pages, SchedulablePage.PRIORITY_COMPARATOR);
	}
	
	@Override
	protected Position getNextPosition(int visit) {
		Position p = new Position();
		p.page = pages.get(-visit);
		p.revisit = -visit;
		return p;
	}

}
