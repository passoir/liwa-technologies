package org.liwa.coherence.events;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.archive.crawler.event.CrawlStateEvent;
import org.archive.crawler.framework.CrawlController;
import org.liwa.coherence.CoherenceControllerFactory;
import org.liwa.coherence.RevisitLauncher;
import org.liwa.coherence.dao.CrawlDao;
import org.liwa.coherence.metadata.CoherenceMetadata;
import org.liwa.coherence.schedule.Schedule;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class CrawlListener implements ApplicationListener,
		ApplicationContextAware {

	protected CrawlDao crawlDao;

	protected ApplicationContext appCntxt;
	
	private List<JobListener> listeners = new ArrayList();
	
	public void addJobListener(JobListener listener){
		listeners.add(listener);
	}

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

	protected void crawlPaused(CrawlController cc) {
		if (cc.getMetadata() instanceof CoherenceMetadata) {
			if (((CoherenceMetadata) cc.getMetadata()).isRecrawling()) {
				cc.requestCrawlResume();
			}
		}
		for(JobListener l: listeners){
			l.jobPaused(cc);
		}
	}

	protected void crawlFinished(CrawlController cc) {
		if (cc.getMetadata() instanceof CoherenceMetadata) {
			if (!((CoherenceMetadata) cc.getMetadata()).isRecrawling()) {
				startRecrawl(cc);
			}
		}
		for(JobListener l: listeners){
			l.jobFinished(cc);
		}
	}

	private void startRecrawl(CrawlController oldCrawl) {
		if (appCntxt.containsBean("schedule")
				&& appCntxt.isTypeMatch("schedule", Schedule.class)) {
			Schedule schedule = (Schedule) appCntxt.getBean("schedule");
			RevisitLauncher revisitLauncher = CoherenceControllerFactory
					.createNewRevisitLauncher();
			revisitLauncher.launchRevisit(oldCrawl, schedule.getRevisits());
		}
	}

	public CrawlDao getCrawlDao() {
		return crawlDao;
	}

	public void setCrawlDao(CrawlDao crawlDao) {
		this.crawlDao = crawlDao;
	}

	protected void crawlPrepared(CrawlController cc) {
		try {
			if (cc.getMetadata() instanceof CoherenceMetadata) {
				runSchedule();
				CoherenceMetadata metadata = (CoherenceMetadata) cc
						.getMetadata();
				insertCrawl(metadata);
				for(JobListener l: listeners){
					l.jobPrepared(cc);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void insertCrawl(CoherenceMetadata metadata) throws SQLException {
		long crawlId = -1;
		if (metadata.isRecrawling()) {
			crawlId = crawlDao.insertRecrawl(metadata.getJobName(), metadata
					.getRecrawledId());
		} else {
			crawlId = crawlDao.insertCrawl(metadata.getJobName());
		}
		metadata.setCrawlId(crawlId);
	}

	public void setApplicationContext(ApplicationContext appCntxt)
			throws BeansException {
		this.appCntxt = appCntxt;
	}

	private void runSchedule() {
		if (appCntxt.containsBean("schedule")
				&& appCntxt.isTypeMatch("schedule", Schedule.class)) {
			Schedule schedule = (Schedule) appCntxt.getBean("schedule");
			schedule.runSchedule();
		}
	}
}
