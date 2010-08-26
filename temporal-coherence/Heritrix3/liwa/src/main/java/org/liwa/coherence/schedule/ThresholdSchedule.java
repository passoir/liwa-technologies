package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.List;

public class ThresholdSchedule extends OfflineSchedule {

	private double tau = 0.7;
	private List<SchedulablePage> hopeless = new ArrayList<SchedulablePage>();
	private List<SchedulablePage> hopeful = new ArrayList<SchedulablePage>();
	
	@Override
	protected void init() {
		schedulePages();
	}
	
	private void schedulePages() {
		for (int i = size - 1; i >= 0; i--) {
			if (!isHopeless(pages.get(i), i)) {
				hopeful.add(0, pages.get(i));
			} else {
				hopeless.add(pages.get(i));
			}
		}
	}

	private boolean isHopeless(SchedulablePage p, int i) {
		return 1-Math.pow(1 - p.getChangeRate(), 2*i*timeMapper.getDelta()) > tau;
	}
	
	@Override
	protected Position getNextPosition(int visit) {
		Position p = new Position();
		int hopefulSize = hopeful.size();
		if(-visit < hopefulSize){
			p.page = hopeful.get(-visit);
		}else{
			int hopelessIndex = hopeless.size()+hopeful.size()+visit-1;
			p.page = hopeless.get(hopelessIndex);
		}
		p.revisit = -visit;
		return p;
	}

}
