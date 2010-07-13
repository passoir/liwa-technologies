/**
 * 
 */
package collectionreader.classes;

/**
 * @author tereza
 * 
 */
public class TimesDocumentStatistics {
	public TimesDocumentStatistics(int year, String filename, double length,
			int nbWords) {
		super();
		this.year = year;
		this.filename = filename;
		this.length = length;
		this.nbWords = nbWords;
	}

	private int year;
	private String filename;
	private double length;
	private int nbWords;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public int getNbWords() {
		return nbWords;
	}

	public void setNbWords(int nbWords) {
		this.nbWords = nbWords;
	}

}
