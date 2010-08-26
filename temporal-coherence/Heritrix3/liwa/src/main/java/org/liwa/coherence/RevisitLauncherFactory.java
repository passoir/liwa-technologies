package org.liwa.coherence;

import org.archive.crawler.Heritrix;

public class RevisitLauncherFactory {
	private static Heritrix heritrix;
	
	public static void  setHeritrix(Heritrix heritrixParam){
		heritrix = heritrixParam;
		String singleJob = System.getProperty("coherence.singleJob");
		if(singleJob != null){
			System.out.println(heritrix.getEngine().getJobConfigs().keySet());
			heritrix.getEngine().requestLaunch(singleJob);
		}
	}
	
	public static RevisitLauncher createNewRevisitLauncher() throws NullPointerException{
		if(heritrix == null){
			throw new NullPointerException();
		}
		return new RevisitLauncher(heritrix);
	}
	
}
