package org.liwa.coherence;

import org.archive.crawler.Heritrix;

public class SitemapHeritrixLauncher {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Heritrix heritrix = new Heritrix();
		heritrix.instanceMain(args); 
		System.setProperty("coherence.profile-auto", "selective-auto");
		CoherenceControllerFactory.setSitemapHeritrix(heritrix, "coherence-controller/sitemap-coherence-controller.cxml");
	}
}
