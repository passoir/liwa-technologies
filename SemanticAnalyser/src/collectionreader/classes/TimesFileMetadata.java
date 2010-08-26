/**
 * 
 */
package collectionreader.classes;

/**
 * @author tereza
 * 
 */
public class TimesFileMetadata {

	public TimesFileMetadata(String title, String date, String text, int year) {
		super();
		this.title = title;
		this.date = date;
		this.text = text;
		this.year = year;
	}

	private String title;
	private String date;
	private String text;
	private int year;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
