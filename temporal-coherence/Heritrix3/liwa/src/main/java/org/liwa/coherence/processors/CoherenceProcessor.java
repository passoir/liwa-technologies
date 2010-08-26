package org.liwa.coherence.processors;

import java.sql.SQLException;

import org.archive.checkpointing.Checkpointable;
import org.archive.modules.CrawlURI;
import org.archive.modules.Processor;
import org.liwa.coherence.dao.PageDao;
import org.liwa.coherence.metadata.CoherenceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;

public class CoherenceProcessor extends Processor implements Lifecycle,
		Checkpointable {

	private static final long serialVersionUID = -1366129749686865803L;

	private CoherenceMetadata metadata;

	private PageDao pageDao;

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public CoherenceMetadata getMetadata() {
		return metadata;
	}

	@Autowired
	public void setMetadata(CoherenceMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	protected void innerProcess(CrawlURI uri) throws InterruptedException {
		try {
			Thread.sleep(1000);
			pageDao.insertPage(this.metadata.getCrawlId(), uri);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected boolean shouldProcess(CrawlURI uri) {
		return !uri.isPrerequisite() && uri.isSuccess();
	}

}
