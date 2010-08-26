package org.liwa.coherence.events;

import org.archive.crawler.event.CrawlStateEvent;
import org.archive.crawler.framework.CrawlController;
import org.springframework.context.ApplicationEvent;

public class SingleJobCrawlListener extends CrawlListener {

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof CrawlStateEvent) {
			CrawlStateEvent cse = (CrawlStateEvent) event;
			CrawlController cc = (CrawlController) cse.getSource();
			switch (cse.getState()) {
			case NASCENT:
				break;
			case PREPARING:
				crawlPrepared(cc);
				break;
			case PAUSING:
				break;
			case PAUSED:
				crawlPaused(cc);
				break;
			case RUNNING:
				break;
			case STOPPING:
				break;
			case FINISHED:
				crawlFinished(cc);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void crawlPaused(CrawlController cc) {
				cc.requestCrawlResume();
	}

	@Override
	protected void crawlFinished(CrawlController cc) {
		System.exit(0);
	}
}
