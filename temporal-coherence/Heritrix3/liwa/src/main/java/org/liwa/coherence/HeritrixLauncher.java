package org.liwa.coherence;

import org.archive.crawler.Heritrix;

public class HeritrixLauncher {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Heritrix heritrix = new Heritrix();
		heritrix.instanceMain(args); 
		System.setProperty("coherence.profile-auto", "selective-auto");
		CoherenceControllerFactory.setRobotsHeritrix(heritrix, "coherence-controller/coherence-controller.cxml");
	}
}
