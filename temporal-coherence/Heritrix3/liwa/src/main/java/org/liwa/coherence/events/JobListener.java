package org.liwa.coherence.events;

import org.archive.crawler.framework.CrawlController;

public interface JobListener {
	public void jobPrepared(CrawlController cc);
	public void jobPaused(CrawlController cc);
	public void jobFinished(CrawlController cc);
	
}
