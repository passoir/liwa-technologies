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

	public String getRobotsFile() {
		return robotsFile;
	}

	public void setRobotsFile(String robotsFile) {
		this.robotsFile = robotsFile;
	}

	private List<String> readRobotList() {
		List<String> list = new ArrayList<String>();
		File f = new File(robotsFile);
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String s = bf.readLine();
			while (s != null) {
				if( !s.trim().startsWith("#")){
				list.add(s);
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
				sitemaps.put(r, readSitemaps(r));
			}
		}
		System.out.println("All robots.txt files read.");
		return sitemaps;
	}
}
