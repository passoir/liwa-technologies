package org.liwa.coherence;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.liwa.coherence.schedule.AbstractSchedule;
import org.liwa.coherence.schedule.ChangeRateProvider;
import org.liwa.coherence.schedule.DatasetProvider;
import org.liwa.coherence.sitemap.CompressedUrl;
import org.liwa.coherence.sitemap.PublishedUrl;
import org.liwa.coherence.sitemap.SitemapChangeRateProvider;
import org.liwa.coherence.sitemap.SitemapHandler;
import org.liwa.coherence.sitemap.SitemapLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class DefaultSitemapController implements ApplicationListener,
		JobListener, SitemapHandler, CoherenceController {

	private Engine engine;

	private PathSharingContext ac;

	private List<SitemapSet> sitemaps;

	private Configuration configuration;

	private PublishedUrlDao publishedUrlDao;

	private RobotFileDao robotsDao;

	private int finished = 0;

	public DefaultSitemapController(File cxml, Engine engine) {
		this.engine = engine;
		ac = new PathSharingContext(new String[] { "file:"
				+ cxml.getAbsolutePath() }, false, null);
		// ac = new PathSharingContext(new String[]
		// {primaryConfig.getAbsolutePath()},false,null);
		ac.addApplicationListener(this);
		ac.refresh();
		ac.validate();
		configuration = (Configuration) ac.getBean("coherenceConfiguration");
		publishedUrlDao = (PublishedUrlDao) ac.getBean("publishedUrlDao");
		robotsDao = (RobotFileDao) ac.getBean("robotsFileDao");

		SitemapsBean bean = (SitemapsBean) ac.getBean("sitemaps");
		List<String> s = bean.getSitemaps();
		sitemaps = new ArrayList<SitemapSet>();
		for (String sitemap : s) {
			sitemaps.add(bean.loadSitemapSet(sitemap));
		}
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

	public void startCoherenceJobs() {
		for (SitemapSet s : sitemaps) {
			System.out.println(s.getDomain());
			startJob(s);
		}
	}

	private void startJob(SitemapSet sitemapSet) {
		String domain = sitemapSet.getDomain();
		int domainId = this.insertDomain(domain);
		publishedUrlDao.setRobotFileId(domainId);
		List<CompressedUrl> urlList = compressUrls(sitemapSet.getUrls());
		// startThresholdJob(configuration, domain, sitemapList, pj);
		startHottestJob(configuration, domain, urlList, domainId);
	}

	private List<CompressedUrl> compressUrls(List<PublishedUrl> published) {
		List<CompressedUrl> compressed = new ArrayList<CompressedUrl>();
		for (PublishedUrl p : published) {
			int id = saveUrl(p.getLocation(), p.getChangeRate(), p
					.getPriority(), p.getLastModified());
			CompressedUrl c = new CompressedUrl();
			c.setId(id);
			c.setChangeRate(SitemapChangeRateProvider.CHANGE_RATE_MAP.get(p
					.getChangeRate().trim()));
			p.setPriority(p.getPriority());
			compressed.add(c);
		}
		return compressed;
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

	private CrawlController startHottestJob(Configuration configuration,
			String domain, List<CompressedUrl> sitemaps, int robotFileId) {
		String name = configuration.getHottest();
		return startJob(name, domain, sitemaps, robotFileId);
	}

	private CrawlController startJob(String jobName, String domain,
			List<CompressedUrl> urls, int robotFileId) {
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
			cl.addJobListener(this);
			changeRateProvider.setSitemaps(sl);
			changeRateProvider.afterSitemapSet();
			AbstractSchedule schedule = (AbstractSchedule) job.getJobContext()
					.getBean("schedule");
			schedule.setDatasetProvider((DatasetProvider) job.getJobContext()
					.getBean("datasetProvider"));
			job.launch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cj.getCrawlController();

	}

	public void jobPaused(CrawlController cc) {
		cc.requestCrawlResume();

	}

	public void jobPrepared(CrawlController cc) {
		System.out.println(cc.getMetadata().getJobName() + " prepared");

	}

	public void jobFinished(CrawlController cc) {
		String jobName = cc.getMetadata().getJobName();
		CrawlJob cj = engine.getJob(jobName);
		cj.teardown();
		if (cj.getJobContext() != null) {
			cj.getJobContext().close();
		}
		engine.getJobConfigs().remove(jobName);
		finished++;
		checkIfRunning();
		System.runFinalization();
		System.gc();
	}

	private void checkIfRunning() {
		if (finished == sitemaps.size()) {
			System.out.println("all jobs are done");
			System.exit(0);
		}
	}

	public int insertDomain(String url) {
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
			id = publishedUrlDao.insertPublishedUrl(url, frequency, priority,
					lastModified);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public void revisitPending(CrawlController oldController, CrawlJob revisitJob) {
		// TODO Auto-generated method stub
		
	}

}
