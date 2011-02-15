package org.liwa.coherence;

import org.archive.crawler.framework.CrawlController;
import org.archive.crawler.framework.CrawlJob;

public interface CoherenceController {
	void revisitPending(CrawlController oldController,
			CrawlJob revisitJob);
}
