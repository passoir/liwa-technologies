package org.liwa.coherence.schedule;


public class HottestSchedule extends OfflineSchedule {

	@Override
	protected Position getNextPosition(int visit) {
		Position p = new Position();
		p.page = pages.get(-visit);
		p.revisit = -visit;
		return p;
	}

}
