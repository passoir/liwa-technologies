package org.liwa.coherence;

import java.io.File;
import java.io.IOException;
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
import org.liwa.coherence.events.CrawlListener;
import org.liwa.coherence.events.JobListener;
import org.liwa.coherence.metadata.CoherenceMetadata;
import org.liwa.coherence.processors.CoherenceProcessor;
import org.liwa.coherence.schedule.AbstractSchedule;
import org.liwa.coherence.schedule.ChangeRateProvider;
import org.liwa.coherence.schedule.DatasetProvider;
import org.liwa.coherence.sitemap.Sitemap;
import org.liwa.coherence.sitemap.SitemapLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class CoherenceController implements ApplicationListener, JobListener {

	private Engine engine;

	private PathSharingContext ac;

	private List<ParallelJobs> pjs;

	private Configuration configuration;

	private Map<ParallelJobs, ParallelJobs> revisitMap;

	private int run = 0;

	public CoherenceController(File cxml, Engine engine) {
		this.pjs = new ArrayList<ParallelJobs>();
		this.revisitMap = new HashMap<ParallelJobs, ParallelJobs>();
		this.engine = engine;
		ac = new PathSharingContext(new String[] { "file:"
				+ cxml.getAbsolutePath() }, false, null);
		// ac = new PathSharingContext(new String[]
		// {primaryConfig.getAbsolutePath()},false,null);
		ac.addApplicationListener(this);
		ac.refresh();
		ac.validate();
		configuration = (Configuration) ac.getBean("coherenceConfiguration");
	}

	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void startCoherenceJobs() {
		Map<String, List<String>> sitemaps = configuration
				.getRobotsListLoader().getSitemaps();
		pjs = new ArrayList<ParallelJobs>();
		revisitMap = new HashMap<ParallelJobs, ParallelJobs>();

		for (String robotsTxt : sitemaps.keySet()) {
			ParallelJobs pj = new ParallelJobs();
			pjs.add(pj);
			String domain = robotsTxt.substring(robotsTxt.indexOf("://")
					+ "://".length(), robotsTxt.indexOf("robots") - 1);
			List<Sitemap> sitemapList = SitemapLoader.loadSitemaps(sitemaps
					.get(robotsTxt));
			startThresholdJob(configuration, domain, sitemapList, pj);
			startHottestJob(configuration, domain, sitemapList, pj);
			startBreadthFirstJob(configuration, domain, sitemapList, pj);
			startHighestPriorityJob(configuration, domain, sitemapList, pj);
			startSelectiveJob(configuration, domain, sitemapList, pj);
		}
	}

	private CrawlController startSelectiveJob(Configuration configuration,
			String domain, List<Sitemap> sitemaps, ParallelJobs pj) {
		String name = configuration.getSelective();
		return startJob(name, domain, sitemaps, pj);
	}

	private CrawlController startHighestPriorityJob(
			Configuration configuration, String domain, List<Sitemap> sitemaps,
			ParallelJobs pj) {
		String name = configuration.getHighestPriority();
		return startJob(name, domain, sitemaps, pj);
	}

	private CrawlController startThresholdJob(Configuration configuration,
			String domain, List<Sitemap> sitemaps, ParallelJobs pj) {
		String name = configuration.getThreshold();
		return startJob(name, domain, sitemaps, pj);
	}

	private CrawlController startHottestJob(Configuration configuration,
			String domain, List<Sitemap> sitemaps, ParallelJobs pj) {
		String name = configuration.getHottest();
		return startJob(name, domain, sitemaps, pj);
	}

	private CrawlController startBreadthFirstJob(Configuration configuration,
			String domain, List<Sitemap> sitemaps, ParallelJobs pj) {
		String name = configuration.getBreadthFirst();
		return startJob(name, domain, sitemaps, pj);
	}

	private CrawlController startJob(String jobName, String domain,
			List<Sitemap> sitemaps, ParallelJobs pj) {
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
			
			sl.setSitemaps(sitemaps);
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
		for (int i = 0; i < pjs.size(); i++) {
			ParallelJobs pj = pjs.get(i);
			if (pj.hasController(cc)) {
				pj.jobFinished(cc);
			}
		}
		for (ParallelJobs pj : revisitMap.values()) {
			if (pj.hasController(cc)) {
				pj.jobFinished(cc);
			}
		}
		checkIfRunning();
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

}
