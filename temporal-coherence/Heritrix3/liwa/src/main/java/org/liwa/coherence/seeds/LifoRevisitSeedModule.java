package org.liwa.coherence.seeds;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.httpclient.URIException;
import org.archive.modules.CrawlURI;
import org.archive.modules.SchedulingConstants;
import org.archive.modules.seeds.SeedModule;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;
import org.liwa.coherence.dao.PageDao;
import org.liwa.coherence.metadata.CoherenceMetadata;

public class LifoRevisitSeedModule extends SeedModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 435644528457555751L;

	private PageDao pageDao;

	private CoherenceMetadata metadata;

	public CoherenceMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(CoherenceMetadata metadata) {
		this.metadata = metadata;
	}

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
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
		List<String> seeds = null;
		try {
			seeds = pageDao.getRevisitPages(metadata.getRecrawledId());
			for (String uri : seeds) {
				if (!uri.matches("[a-zA-Z][\\w+\\-]+:.*")) { // Rfc2396 s3.1
																// scheme,
					// minus '.'
					// Does not begin with scheme, so try http://
					uri = "http://" + uri;
				}
				try {
					UURI uuri = UURIFactory.getInstance(uri);
					CrawlURI curi = new CrawlURI(uuri);
					curi.setSeed(true);
					curi.setSchedulingDirective(SchedulingConstants.MEDIUM);
					publishAddedSeed(curi);
				} catch (URIException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
