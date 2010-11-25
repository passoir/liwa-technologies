package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSchedule implements Schedule {

	protected DatasetProvider datasetProvider;

	protected List<Integer> visits;

	protected List<Integer> revisits;

	public AbstractSchedule() {
		visits = new ArrayList<Integer>();
		revisits = new ArrayList<Integer>();
	}

	public DatasetProvider getDatasetProvider() {
		return datasetProvider;
	}

	public void setDatasetProvider(DatasetProvider datasetProvider) {
		this.datasetProvider = datasetProvider;
	}

	public List<Integer> getRevisits() {
		// TODO Auto-generated method stub
		return revisits;
	}

	public List<Integer> getVisits() {
		// TODO Auto-generated method stub
		return visits;
	}

}
