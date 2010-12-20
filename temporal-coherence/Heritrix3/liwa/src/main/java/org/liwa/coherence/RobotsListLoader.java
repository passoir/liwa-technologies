package org.liwa.coherence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotsListLoader {
	private String robotsFile;

	private int maxSites = 200;

	private int startSite = 0;

	public int getMaxSites() {
		return maxSites;
	}

	public void setMaxSites(int maxSites) {
		this.maxSites = maxSites;
	}

	public int getStartSite() {
		return startSite;
	}

	public void setStartSite(int startSite) {
		this.startSite = startSite;
	}

	public String getRobotsFile() {
		return robotsFile;
	}

	public void setRobotsFile(String robotsFile) {
		this.robotsFile = robotsFile;
	}

	private List<String> readRobotList() {
		String startProp = System.getProperty("robots.start");
		String countProp = System.getProperty("robots.count");
		if(startProp != null && startProp.trim().length() > 0){
			startSite = Integer.parseInt(startProp);
		}
		if(countProp != null && startProp.trim().length() > 0){
			maxSites = Integer.parseInt(countProp);
		}
		List<String> list = new ArrayList<String>();
		File f = new File(robotsFile);
		int count = 0;
		int endSite = startSite + maxSites;
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String s = bf.readLine();

			while (s != null && count < endSite) {
				System.out.println(s);
				if (!s.trim().startsWith("#")) {
					if (count >= startSite && count < endSite) {
						list.add(s);
					}
					count++;
				}
				s = bf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private List<String> readSitemaps(String robotTxt) {
		List<String> list = new ArrayList<String>();
		try {
			System.out.println("reading robots.txt file:" + robotTxt);
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new URL(robotTxt).openStream()));
			String s = bf.readLine();
			while (s != null) {
				if (s.startsWith("Sitemap: ") || s.startsWith("sitemap: ")) {
					list.add(s.substring("Sitemap: ".length()));
				}
				s = bf.readLine();
			}
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception at " + robotTxt);
			System.out.println("Going on with the next sitemap");

		}
		return list;
	}

	public Map<String, List<String>> getSitemaps() {
		List<String> robots = readRobotList();
		System.out.println("Robotlist read.");
		Map<String, List<String>> sitemaps = new HashMap<String, List<String>>();
		for (String r : robots) {
			List<String> detectedSitemaps = readSitemaps(r);
			if (detectedSitemaps.size() > 0) {
				sitemaps.put(r, detectedSitemaps);
			}
		}
		System.out.println("All robots.txt files read.");
		return sitemaps;
	}
}
