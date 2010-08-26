package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSchedule implements Schedule {

	protected DatasetProvider datasetProvider;

	protected List<String> visits;

	protected List<String> revisits;

	public AbstractSchedule() {
		visits = new ArrayList<String>();
		revisits = new ArrayList<String>();
	}

	public DatasetProvider getDatasetProvider() {
		return datasetProvider;
	}

	public void setDatasetProvider(DatasetProvider datasetProvider) {
		this.datasetProvider = datasetProvider;
	}

	public List<String> getRevisits() {
		// TODO Auto-generated method stub
		return revisits;
	}

	public List<String> getVisits() {
		// TODO Auto-generated method stub
		return visits;
	}

}
