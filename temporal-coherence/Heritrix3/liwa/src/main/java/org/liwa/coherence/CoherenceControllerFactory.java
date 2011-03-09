package org.liwa.coherence;

import java.io.File;

import org.archive.crawler.Heritrix;

public class CoherenceControllerFactory {
	private static Heritrix heritrix;
	private static RevisitLauncher launcher;

	public static void setRobotsHeritrix(Heritrix heritrixParam, String file) {
		heritrix = heritrixParam;
		String singleJob = System.getProperty("coherence.singleJob");
		if (singleJob != null) {
			heritrix.getEngine().requestLaunch(singleJob);
		}
		RobotsCoherenceController coherenceController = new RobotsCoherenceController(new File(file), heritrix
						.getEngine());
		launcher = new RevisitLauncher(heritrix);
		launcher.setCoherenceController(coherenceController);
		coherenceController.deleteOldJobs();
		coherenceController.startCoherenceJobs();
	}
	
	public static void setSitemapHeritrix(Heritrix heritrixParam, String file) {
		heritrix = heritrixParam;
		String singleJob = System.getProperty("coherence.singleJob");
		if (singleJob != null) {
			heritrix.getEngine().requestLaunch(singleJob);
		}
		SitemapCoherenceController coherenceController = new SitemapCoherenceController(new File(file), heritrix
						.getEngine());
		launcher = new RevisitLauncher(heritrix);
		launcher.setCoherenceController(coherenceController);
		coherenceController.deleteOldJobs();
		coherenceController.startCoherenceJobs();
	}
	
	public static void setDefaultSitemapHeritrix(Heritrix heritrixParam, String file) {
		heritrix = heritrixParam;
		String singleJob = System.getProperty("coherence.singleJob");
		if (singleJob != null) {
			heritrix.getEngine().requestLaunch(singleJob);
		}
		DefaultSitemapController coherenceController = new DefaultSitemapController(new File(file), heritrix
						.getEngine());
//		launcher = new RevisitLauncher(heritrix);
//		launcher.setCoherenceController(coherenceController);
		coherenceController.deleteOldJobs();
		coherenceController.startCoherenceJobs();
	}

	public static RevisitLauncher createNewRevisitLauncher()
			throws NullPointerException {
		if (heritrix == null) {
			throw new NullPointerException();
		}
		if(launcher == null){
			launcher = new RevisitLauncher(heritrix);
		}
		return launcher;
	}

}
