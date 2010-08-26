package org.liwa.coherence.schedule;


public class BreadthFirstOfflineSchedule extends OfflineSchedule {

	
	@Override
	public void setDatasetProvider(DatasetProvider datasetProvider) {
		this.datasetProvider = datasetProvider;
		pages.addAll(datasetProvider.getDataset().getPages());
		size = pages.size();
		this.timeMapper = datasetProvider.getTimestampMapper();
	}
	@Override
	protected Position getNextPosition(int visit) {
		Position p = new Position();
		p.page = pages.get(-visit);
		p.revisit = -visit;
		return p;
	}

}
