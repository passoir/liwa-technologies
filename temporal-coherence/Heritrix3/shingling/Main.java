/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpii.shingling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author amazeika
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException {
		String doc = "Three of the miners were discharged Thursday night, CNN Chile reported. CNN's sister network identified them as Juan Illanes, Edison Pena and Carlos Mamani. It aired video of several men and what appeared to be their families inside a red van departing the hospital grounds."
			+ " Illanes is a 52-year-old electrical mechanic and military retiree; Pena, 34, is an Elvis Presley fan; and Mamani is a 23-year-old Bolivian who started working in the mine five days before the collapse."
			+ " Mamani was the only miner recognizable in the network's video. He was still wearing the dark sunglasses that he and the other miners had on when they surfaced Wednesday.";

		String doc2 = "Three of the miners were discharged Thursday night, CNN Chile reported. CNN's sister network identified them as Juan Illanes, Edison Pena and Carlos Mamani. It aired video of several men and what appeared to be their families inside a red van departing the hospital grounds.";

		String doc3 = new String();

		URL url = new URL("http://www.cnn.com/");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		BufferedReader brr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String l = null;
		while ((l = brr.readLine()) != null) {
			doc3 += l + " ";
		}

		System.out.println(doc3);

		Signature sig = new Signature(doc, 7, 10, 3);
		Signature sig2 = new Signature(doc2, 7, 10, 3);
		Signature sig3 = new Signature(doc3, 7, 10, 3);

		System.out.println(sig.signatureToString() + "\n" + sig2.signatureToString()
			+ " " + sig.similarity(sig2)
			+ "\n" + sig3.signatureToString());;

	}
}
