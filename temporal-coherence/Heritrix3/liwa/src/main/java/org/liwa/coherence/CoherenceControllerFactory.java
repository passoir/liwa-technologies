package org.liwa.coherence;

import java.io.File;

import org.archive.crawler.Heritrix;

public class CoherenceControllerFactory {
	private static Heritrix heritrix;
	private static RevisitLauncher launcher;

	public static void setHeritrix(Heritrix heritrixParam) {
		heritrix = heritrixParam;
		String singleJob = System.getProperty("coherence.singleJob");
		if (singleJob != null) {
			heritrix.getEngine().requestLaunch(singleJob);
		}
		CoherenceController coherenceController = new CoherenceController(
				new File("coherence-controller/coherence-controller.cxml"), heritrix
						.getEngine());
		launcher = new RevisitLauncher(heritrix);
		launcher.setCoherenceController(coherenceController);
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
