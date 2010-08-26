/**
 * 
 */
package collectionreader.classes;

/**
 * @author tereza
 * 
 */
public class ARCFileMetadata {

	public ARCFileMetadata(String title, String date, String text) {
		super();
		this.title = title;
		this.date = date;
		this.text = text;

	}

	private String title;
	private String date;
	private String text;

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

}
