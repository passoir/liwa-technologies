package org.liwa.coherence.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class OfflineSchedule extends AbstractSchedule{
	protected Map<Integer, SchedulablePage> visitMap;
	protected Map<SchedulablePage, Integer> revisitMap;
	protected List<SchedulablePage> pages;
	protected int size;
	protected double expected=-1;
	protected double confidenceThreshold=0.7;
	protected TimestampMapper timeMapper;
	
	public OfflineSchedule(){
		visitMap = new HashMap<Integer, SchedulablePage>();
		revisitMap = new HashMap<SchedulablePage, Integer>();
		pages = new ArrayList<SchedulablePage>();
	}

	
	
	public double getConfidenceThreshold() {
		return confidenceThreshold;
	}



	public void setConfidenceThreshold(double confidenceThreshold) {
		this.confidenceThreshold = confidenceThreshold;
	}



	@Override
	public void setDatasetProvider(DatasetProvider datasetProvider) {
		// TODO Auto-generated method stub
		super.setDatasetProvider(datasetProvider);
		pages.addAll(datasetProvider.getDataset().getPages());
		Collections.sort(pages, SchedulablePage.CHANGE_RATE_COMPARATOR);
		size = pages.size();
		this.timeMapper = datasetProvider.getTimestampMapper();
	}
	
	protected void computeExpected() {
		expected = 0.0d;
		for (int i = -(size-1); i <=0; i++) {
			SchedulablePage c = visitMap.get(i);
			Integer j = revisitMap.get(c);
			double startSeconds = timeMapper.getSeconds(i);
			double endSeconds = timeMapper.getSeconds(j);
			expected += Math.pow(Math.E, -c.getChangeRate()
					* (endSeconds - startSeconds));
		}
	}

	public void runSchedule() {
		init();
		doAlgorithm();
		computeExpected();
		finish();
		setVisitRevisit();
	}
	
	protected void setVisitRevisit() {
		List<Integer> positions = new ArrayList<Integer>();
		positions.addAll(visitMap.keySet());
		Collections.sort(positions);
		for(int i = 0; i < positions.size(); i++){
			visits.add(visitMap.get(positions.get(i)).getUrl());
		}
		
		Map<Integer, String> reversedRevisits = new HashMap<Integer, String>();
		for(SchedulablePage p: revisitMap.keySet()){
			reversedRevisits.put(revisitMap.get(p), p.getUrl());
		}
		
		positions.clear();
		positions.addAll(reversedRevisits.keySet());
		Collections.sort(positions);
		for(int i = 0; i < positions.size(); i++){
			revisits.add(reversedRevisits.get(positions.get(i)));
		}
		
	}

	protected void finish() {
		// TODO Auto-generated method stub
		
	}

	protected void init() {
		// TODO Auto-generated method stub
		
	}

	protected void doAlgorithm(){
		for(int i = -(size-1); i < 0; i++){
			Position position = getNextPosition(i);
			visitMap.put(i, position.page);
			if(position.revisit > 0){
				revisitMap.put(position.page, position.revisit);
			}
		}
		visitMap.put(0, pages.get(0));
		revisitMap.put(pages.get(0), 0);
	}
	
	/**
	 * 
	 * @param visit current visit, negative number
	 * @return a position object which containts the	 page which must be scheduled 
	 * at the position visit and as well as its revisit position
	 */
	protected abstract Position getNextPosition(int visit) ;

	protected void printSchedule() {
		
		for (int i = -(size - 1); i <= 0; i++) {
			SchedulablePage c = visitMap.get(i);
			System.out.println(timeMapper.getInterval(c.getIntLength(confidenceThreshold)) + " "
					+ timeMapper.getSeconds(i) + " " + 
					timeMapper.getSeconds(revisitMap.get(c)));

		}
	}
	
	public double getExpected() {
		return expected;
	}

}
