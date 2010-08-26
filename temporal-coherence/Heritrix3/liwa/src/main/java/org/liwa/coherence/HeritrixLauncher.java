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
		RevisitLauncherFactory.setHeritrix(heritrix);
	}
}
