package org.liwa.coherence.seeds;

import java.io.File;

import org.apache.commons.httpclient.URIException;
import org.archive.modules.CrawlURI;
import org.archive.modules.SchedulingConstants;
import org.archive.modules.seeds.SeedModule;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;
import org.liwa.coherence.schedule.Schedule;

public class ScheduledVisitsSeedModule extends SeedModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4575156995982437511L;
	private Schedule schedule;
	
	public Schedule getSchedule() {
		return schedule;
	}
	
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	@Override
	public void actOn(File f) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSeed(CrawlURI curi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void announceSeeds() {
		for(String uri: schedule.getVisits()){
			try {
	            UURI uuri = UURIFactory.getInstance(uri);
	            CrawlURI curi = new CrawlURI(uuri);
	            curi.setSeed(true);
	            curi.setSchedulingDirective(SchedulingConstants.MEDIUM);
	            if (getSourceTagSeeds()) {
	                curi.setSourceTag(curi.toString());
	            }
	            publishAddedSeed(curi);
	        } catch (URIException e) {
	            // try as nonseed line as fallback
	        }
		}
		publishConcludedSeedBatch();
	}

}
