package org.liwa.coherence.seeds;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.URIException;
import org.archive.modules.CrawlURI;
import org.archive.modules.SchedulingConstants;
import org.archive.modules.seeds.SeedModule;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;
import org.liwa.coherence.dao.PublishedUrlDao;

public class RevisitSeedModule extends SeedModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3011497233129976414L;

	private List<Integer> revisits = new ArrayList<Integer>();

	private PublishedUrlDao publishedUrlDao;

	public List<Integer> getRevisits() {
		return revisits;
	}

	public PublishedUrlDao getPublishedUrlDao() {
		return publishedUrlDao;
	}

	public void setPublishedUrlDao(PublishedUrlDao publishedUrlDao) {
		this.publishedUrlDao = publishedUrlDao;
	}

	public void setRevisits(List<Integer> revisits) {
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
		for (int i = 1; i < revisits.size(); i++) {
			int urlId = revisits.get(i);

			try {
				String uri = publishedUrlDao.loadUrl(urlId);
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		publishConcludedSeedBatch();
	}

}
