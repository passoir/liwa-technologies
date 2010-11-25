package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectiveSchedule extends OfflineSchedule {

	private Selective autoPsi;

	@Override
	public void setDatasetProvider(DatasetProvider datasetProvider) {
		// TODO Auto-generated method stub
		super.setDatasetProvider(datasetProvider);
		Collections.sort(this.pages, SchedulablePage.PRIORITY_COMPARATOR);
		autoPsi = new Selective(pages);
	}

	@Override
	protected Position getNextPosition(int visit) {
		// TODO Auto-generated method stub
		return null;
	}

	public void runSchedule() {
		List<SchedulablePage> hopeful = new ArrayList<SchedulablePage>();
		List<SchedulablePage> hopeless = new ArrayList<SchedulablePage>();
		if (pages.size() > 0) {
			expected = autoPsi.selective(hopeful, hopeless, timeMapper
					.getDelta());

			for (int i = 0; i < hopeful.size(); i++) {
				int index = -i;
				visitMap.put(index, hopeful.get(i));
				revisitMap.put(hopeful.get(i), -index);
			}

			int lastIndex = size;
			for (int i = 0; i < hopeless.size(); i++) {
				lastIndex = lastIndex - 1;
				visitMap.put(-lastIndex, hopeless.get(i));
				revisitMap.put(hopeless.get(i), lastIndex);
			}
			super.setVisitRevisit();
		}
	}
}
