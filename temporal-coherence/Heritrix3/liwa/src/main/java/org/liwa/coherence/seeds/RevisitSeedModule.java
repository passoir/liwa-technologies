package org.liwa.coherence.seeds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.URIException;
import org.archive.modules.CrawlURI;
import org.archive.modules.SchedulingConstants;
import org.archive.modules.seeds.SeedModule;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;

public class RevisitSeedModule extends SeedModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3011497233129976414L;
	private List<String> revisits = new ArrayList<String>();

	public List<String> getRevisits() {
		return revisits;
	}

	public void setRevisits(List<String> revisits) {
		this.revisits.clear();
		this.revisits.addAll(revisits);
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
		for(String uri: revisits){
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
