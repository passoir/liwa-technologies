package org.liwa.coherence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RobotListManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File directory = new File("seeds");
		System.out.println(directory.isDirectory());
		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File pathname, String fileName) {
				return fileName.startsWith("robots-");
			}
		};

		String[] files = directory.list(fileFilter);
		Arrays.sort(files);
		List<String> allRobots = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			String s = files[i];
			List<String> robotFiles = readRobotList("seeds/"+s);
			allRobots.addAll(robotFiles);
		}

		File f = new File("seeds/allrobottxt");
		try {
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			for (int i = 0; i < allRobots.size(); i++) {
				fw.write(allRobots.get(i)+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<String> readRobotList(String robotsFile) {
		List<String> list = new ArrayList<String>();
		File f = new File(robotsFile);
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String s = bf.readLine();

			while (s != null) {
				if (!s.trim().startsWith("#")) {
					list.add(0, s);

				}
				s = bf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
