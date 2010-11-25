package org.liwa.coherence.schedule;

import java.util.List;

public interface Schedule {

	void runSchedule();
	List<Integer> getVisits();
	List<Integer> getRevisits();
}
