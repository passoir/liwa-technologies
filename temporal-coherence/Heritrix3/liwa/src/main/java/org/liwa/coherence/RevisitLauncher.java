package org.liwa.coherence;

import java.io.IOException;
import java.util.List;

import org.archive.crawler.Heritrix;
import org.archive.crawler.framework.CrawlController;
import org.archive.crawler.framework.CrawlJob;
import org.liwa.coherence.metadata.CoherenceMetadata;
import org.liwa.coherence.seeds.RevisitSeedModule;

public class RevisitLauncher {
	private Heritrix heritrix;
	
	public RevisitLauncher(Heritrix heritrix){
		this.heritrix = heritrix;
	}
	
	public void launchRevisit(CrawlController crawlController, List<String> revisits){
		System.out.println("Revisit requested for " + crawlController.getMetadata().getJobName());
		CrawlJob crawlJob = heritrix.getEngine().getJob(crawlController.getMetadata().getJobName().trim());
		try {
			String revisitName = "revisit-"+crawlJob.getShortName();
			CrawlJob revisitJob = heritrix.getEngine().getJob(revisitName);
			if(revisitJob == null){
				CrawlJob revisitProfile = heritrix.getEngine().getJob("revisit");
				heritrix.getEngine().copy(revisitProfile, revisitName, false);
				revisitJob = heritrix.getEngine().getJob(revisitName);
			}
			revisitJob.validateConfiguration();
			CoherenceMetadata metadata = (CoherenceMetadata)revisitJob.getJobContext().getBean("metadata");
			metadata.setRecrawling(true);
			metadata.setJobName(revisitName);
			CoherenceMetadata oldMetadata = (CoherenceMetadata)crawlController.getMetadata();
			System.out.println("revisit for " + oldMetadata.getCrawlId());
			metadata.setRecrawledId(oldMetadata.getCrawlId());
			RevisitSeedModule seeds = (RevisitSeedModule)revisitJob.getJobContext().getBean("seeds");
			seeds.setRevisits(revisits);
			revisitJob.launch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
