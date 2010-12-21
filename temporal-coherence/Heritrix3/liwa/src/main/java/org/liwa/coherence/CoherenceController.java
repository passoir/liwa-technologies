package org.liwa.coherence;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archive.crawler.framework.CrawlController;
import org.archive.crawler.framework.CrawlJob;
import org.archive.crawler.framework.Engine;
import org.archive.spring.PathSharingContext;
import org.liwa.coherence.dao.PublishedUrlDao;
import org.liwa.coherence.dao.RobotFileDao;
import org.liwa.coherence.events.CrawlListener;
import org.liwa.coherence.events.JobListener;
import org.liwa.coherence.metadata.CoherenceMetadata;
import org.liwa.coherence.processors.CoherenceProcessor;
import org.liwa.coherence.schedule.AbstractSchedule;
import org.liwa.coherence.schedule.ChangeRateProvider;
import org.liwa.coherence.schedule.DatasetProvider;
import org.liwa.coherence.sitemap.CompressedUrl;
import org.liwa.coherence.sitemap.SitemapHandler;
import org.liwa.coherence.sitemap.SitemapLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class CoherenceController implements ApplicationListener, JobListener,
		SitemapHandler {

	private Engine engine;

	private PathSharingContext ac;

	private List<ParallelJobs> pjs;

	private List<String> robotTxtList;

	private Configuration configuration;

	private Map<ParallelJobs, ParallelJobs> revisitMap;

	private Map<String, List<String>> sitemaps;

	private List<CrawlJob> jobsToDelete;
	
	private PublishedUrlDao publishedUrlDao;
	private RobotFileDao robotsDao;

	private int run = 0;

	private int jobCursor = 0;

	public CoherenceController(File cxml, Engine engine) {
		this.pjs = new ArrayList<ParallelJobs>();
		this.revisitMap = new HashMap<ParallelJobs, ParallelJobs>();
		this.engine = engine;
		this.jobsToDelete = new ArrayList<CrawlJob>();
		ac = new PathSharingContext(new String[] { "file:"
				+ cxml.getAbsolutePath() }, false, null);
		// ac = new PathSharingContext(new String[]
		// {primaryConfig.getAbsolutePath()},false,null);
		ac.addApplicationListener(this);
		ac.refresh();
		ac.validate();
		configuration = (Configuration) ac.getBean("coherenceConfiguration");
		robotsDao = (RobotFileDao) ac.getBean("robotsFileDao");
		publishedUrlDao = (PublishedUrlDao) ac.getBean("publishedUrlDao");
	}

	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

	}

	
	public PublishedUrlDao getPublishedUrlDao() {
		return publishedUrlDao;
	}

	public void setPublishedUrlDao(PublishedUrlDao publishedUrlDao) {
		this.publishedUrlDao = publishedUrlDao;
	}

	public RobotFileDao getRobotsDao() {
		return robotsDao;
	}

	public void setRobotsDao(RobotFileDao robotsDao) {
		this.robotsDao = robotsDao;
	}

	public void startCoherenceJobs() {
		sitemaps = configuration.getRobotsListLoader().getSitemaps();
		if(sitemaps.size()==0){
			System.out.println("no sitemaps");
			System.exit(0);
		}
		robotTxtList = new ArrayList<String>();
		robotTxtList.addAll(sitemaps.keySet());
		pjs = new ArrayList<ParallelJobs>();
		revisitMap = new HashMap<ParallelJobs, ParallelJobs>();
		for (jobCursor = 0; jobCursor < robotTxtList.size()
				&& jobCursor < configuration.getParallelSites(); jobCursor++) {
			startJob(robotTxtList.get(jobCursor));
		}
	}

	private void startJob(String robotsTxt) {
		ParallelJobs pj = new ParallelJobs();
		pjs.add(pj);
		String domain = robotsTxt.substring(robotsTxt.indexOf("://")
				+ "://".length(), robotsTxt.indexOf("robots") - 1);
		int robotFileId = this.insertRobotFile(robotsTxt);
		publishedUrlDao.setRobotFileId(robotFileId);
		List<CompressedUrl> urlList = SitemapLoader.loadCompressedUrls(sitemaps
				.get(robotsTxt), this);
		// startThresholdJob(configuration, domain, sitemapList, pj);
		startHottestJob(configuration, domain, urlList, robotFileId, pj);
		startBreadthFirstJob(configuration, domain, urlList, robotFileId, pj);
		startHighestPriorityJob(configuration, domain, urlList, robotFileId, pj);
		startSelectiveJob(configuration, domain, urlList, robotFileId, pj);
	}

	public void deleteOldJobs() {
		if (configuration.isDeleteOldJobs()) {
			Map<String, CrawlJob> jobs = engine.getJobConfigs();
			for (String key : jobs.keySet()) {
				if (isOldJob(key)) {
					try {
						engine.deleteJob(jobs.get(key));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		System.runFinalization();
		System.gc();
	}

	private boolean isOldJob(String jobName) {
		return jobName.contains(configuration.getBreadthFirst() + "-")
				|| jobName.contains(configuration.getHighestPriority() + "-")
				|| jobName.contains(configuration.getHottest() + "-")
				|| jobName.contains(configuration.getSelective() + "-")
				|| jobName.contains(configuration.getThreshold() + "-");
	}

	private CrawlController startSelectiveJob(Configuration configuration,
			String domain, List<CompressedUrl> sitemaps, int robotFileId,
			ParallelJobs pj) {
		String name = configuration.getSelective();
		return startJob(name, domain, sitemaps, robotFileId, pj);
	}

	private CrawlController startHighestPriorityJob(
			Configuration configuration, String domain,
			List<CompressedUrl> sitemaps, int robotFileId, ParallelJobs pj) {
		String name = configuration.getHighestPriority();
		return startJob(name, domain, sitemaps, robotFileId, pj);
	}

	private CrawlController startHottestJob(Configuration configuration,
			String domain, List<CompressedUrl> sitemaps, int robotFileId,
			ParallelJobs pj) {
		String name = configuration.getHottest();
		return startJob(name, domain, sitemaps, robotFileId, pj);
	}

	private CrawlController startBreadthFirstJob(Configuration configuration,
			String domain, List<CompressedUrl> sitemaps, int robotFileId,
			ParallelJobs pj) {
		String name = configuration.getBreadthFirst();
		return startJob(name, domain, sitemaps, robotFileId, pj);
	}

	private CrawlController startJob(String jobName, String domain,
			List<CompressedUrl> urls, int robotFileId, ParallelJobs pj) {
		CrawlJob cj = engine.getJob(jobName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hhmm");
		String newName = jobName + "-" + domain + "-" + sdf.format(new Date());
		System.out.println(newName);
		try {
			engine.copy(cj, newName, false);
			CrawlJob job = engine.getJob(newName);
			job.validateConfiguration();
			CoherenceMetadata metadata = (CoherenceMetadata) job
					.getCrawlController().getMetadata();
			metadata.setJobName(newName);
			SitemapLoader sl = (SitemapLoader) job.getJobContext().getBean(
					"sitemaps");
			CrawlListener cl = (CrawlListener) job.getJobContext().getBean(
					"crawlListener");
			PublishedUrlDao otherDao = (PublishedUrlDao) job.getJobContext()
					.getBean("publishedUrlDao");

			otherDao.setRobotFileId(robotFileId);
			sl.setCompressedUrls(urls);
			ChangeRateProvider changeRateProvider = (ChangeRateProvider) job
					.getJobContext().getBean("changeRateProvider");
			pj.addCrawlController(job.getCrawlController());
			cl.addJobListener(this);
			changeRateProvider.setSitemaps(sl);
			changeRateProvider.afterSitemapSet();
			AbstractSchedule schedule = (AbstractSchedule) job.getJobContext()
					.getBean("schedule");
			schedule.setDatasetProvider((DatasetProvider) job.getJobContext()
					.getBean("datasetProvider"));
			CoherenceProcessor cp = (CoherenceProcessor) job.getJobContext()
					.getBean("coherenceProcessor");
			cp.addProcessorListener(pj);
			job.launch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cj.getCrawlController();

	}

	public void jobPaused(CrawlController cc) {
		for (int i = 0; i < pjs.size(); i++) {
			ParallelJobs pj = pjs.get(i);
			if (pj.hasController(cc)) {
				pj.jobPaused(cc);
			}
		}

	}

	public void jobPrepared(CrawlController cc) {
		System.out.println(cc.getMetadata().getJobName() + " prepared");

	}

	public void jobFinished(CrawlController cc) {
		boolean visitDone = false;
		for (int i = 0; i < pjs.size() && !visitDone; i++) {
			ParallelJobs pj = pjs.get(i);
			if (pj.hasController(cc)) {
				pj.jobFinished(cc);
				visitDone = true;
			}
		}
		if (!visitDone) {
			ParallelJobs pjVisit = null;
			ParallelJobs pjRevisit = null;
			for (ParallelJobs pjV : revisitMap.keySet()) {
				ParallelJobs pj = revisitMap.get(pjV);
				if (pj.hasController(cc)) {
					pj.jobFinished(cc);
					if (pj.areJobsDone()) {
						pjVisit = pjV;
						pjRevisit = pj;
						break;
					}
				}
			}
			if (pjVisit != null && pjRevisit != null) {
				synchronized (this) {
					// try {
					// while(!jobsToDelete.isEmpty()){
					// engine.deleteJob(jobsToDelete.remove(0));
					// }
					// } catch (IOException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					pjs.remove(pjVisit);
					revisitMap.remove(pjVisit);
					jobCursor++;
					if (jobCursor < robotTxtList.size()) {
						startJob(robotTxtList.get(jobCursor));
					}
					// jobsToDelete.add(cj);
				}
			}
		}
		String jobName = cc.getMetadata().getJobName();
		CrawlJob cj = engine.getJob(jobName);
		cj.teardown();
		if (cj.getJobContext() != null) {
			cj.getJobContext().close();
		}
		engine.getJobConfigs().remove(jobName);
		checkIfRunning();
		System.runFinalization();
		System.gc();
	}

	private void checkIfRunning() {
		if (revisitMap.size() == pjs.size()) {
			boolean allDone = true;
			for (ParallelJobs pj : revisitMap.values()) {
				allDone &= pj.areJobsDone();
			}
			if (allDone) {
				run++;
				if (run < configuration.getRuns()) {
					startCoherenceJobs();
				}else{
					System.out.println("all jobs are done");
					System.exit(0);
				}
			}
		}
	}

	public void revisitPending(CrawlController oldController,
			CrawlJob revisitJob) {
		for (int i = 0; i < pjs.size(); i++) {
			ParallelJobs pj = pjs.get(i);
			if (pj.hasController(oldController)) {
				ParallelJobs revisitPj = revisitMap.get(pj);
				if (revisitPj == null) {
					revisitPj = new ParallelJobs();
					revisitMap.put(pj, revisitPj);
				}
				revisitPj.addCrawlController(revisitJob.getCrawlController());
				CoherenceProcessor cp = (CoherenceProcessor) revisitJob
						.getJobContext().getBean("coherenceProcessor");
				cp.addProcessorListener(revisitPj);
				CrawlListener cl = (CrawlListener) revisitJob.getJobContext()
						.getBean("crawlListener");
				cl.addJobListener(this);

			}
		}
	}

	public int insertRobotFile(String url) {
		int id = -1;
		try {
			id = robotsDao.insertCrawledRobotFile(url);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public int saveUrl(String url, String frequency, double priority,
			Date lastModified) {
		int id = -1;
		try {
			id = publishedUrlDao.insertPublishedUrl(url, frequency, priority, lastModified);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

}
