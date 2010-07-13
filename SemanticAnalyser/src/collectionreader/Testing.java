/**
 * 
 */
package collectionreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ice.tar.*;

import org.apache.commons.io.IOUtils;
import org.apache.uima.util.Level;

/**
 * @author tereza
 * 
 */
public class Testing {

	public static final String ELEMENT_ARTICLE_TEXT = "ARTICLETEXT";
	public static final String ELEMENT_ARTICLE_TITLE = "TITLE";

	/**
	 * XML element of content ArticleId
	 */
	public static final String ELEMENT_ARTICLE_ID = "ARTICLEID";

	/**
	 * XML element of content IssueDate
	 */
	public static final String ELEMENT_ISSUE_DATE = "ISSUEDATE";

	/**
	 * Name of configuration parameter that must be set to the path of a
	 * directory containing input files.
	 */
	public static final String PARAM_INPUT_DIR = "InputDirectory";

	private static final String PARAM_INPUT_FILE = "InputFile";

	/**
	 * Name of configuration parameter that contains the character encoding used
	 * by the input files. If not specified, the default system encoding will be
	 * used.
	 */
	public static final String PARAM_ENCODING = "Encoding";

	/**
	 * Name of optional configuration parameter that contains the language of
	 * the documents in the input directory. If specified this information will
	 * be added to the CAS.
	 */
	public static final String PARAM_LANGUAGE = "Language";

	public static void main(String[] args) {

		File directory = new File("testdata");
		File[] dirFiles = directory.listFiles();
		for (int i = 0; i < dirFiles.length; i++) {
			if (!dirFiles[i].isDirectory()) {

				InputStream input;
				try {
					input = new FileInputStream(dirFiles[i]);

					input = new GZIPInputStream(input);
					TarInputStream tar = new TarInputStream(input);

					while (true) {
						TarEntry entry = tar.getNextEntry();
						if (entry == null)
							break;

						if (!entry.isDirectory()) {

							File tempFile = File.createTempFile("bla-", "");
							OutputStream out = null;
							out = new FileOutputStream(tempFile);
							tar.copyEntryContents(out);
							// IOUtils.copy(tar, out);
							// System.out.println(tempFile.toString());
							XMLInputFactory xmlif = XMLInputFactory
									.newInstance();
							XMLStreamReader xmlr = null;
							FileReader fr = null;
							fr = new FileReader(tempFile);
							boolean hasTitle = false;
							boolean hasDate = false;
							boolean hasText = false;

							try {
								xmlr = xmlif.createXMLStreamReader(fr);
								while (xmlr.hasNext()
										&& (!hasTitle || !hasText || !hasDate)) {
									switch (xmlr.getEventType()) {

									case XMLStreamConstants.START_ELEMENT:
										String elem = getName(xmlr);
										if (elem.equals(ELEMENT_ISSUE_DATE)
												&& xmlr.hasNext()) {
											String dateText = getTextUntilTag(
													xmlr, elem);
											System.out.println(dateText);
											// logger.log(Level.FINE,
											// "getNext(CAS) - titleDate=" +
											// dateText);
											hasDate = true;
										}
										if (elem.equals(ELEMENT_ARTICLE_TITLE)
												&& xmlr.hasNext()) {
											String titleText = getTextUntilTag(
													xmlr, elem);
											System.out.println(titleText);
											// logger.log(Level.FINE,
											// "getNext(CAS) - titleText=" +
											// titleText);
											hasTitle = true;
										}
										if (elem.equals(ELEMENT_ARTICLE_TEXT)
												&& xmlr.hasNext()) {
											String documentText = getTextUntilTag(
													xmlr, elem);
											// System.out.println(documentText);
											// logger.log(Level.FINE,
											// "getNext(CAS) - documentText=" +
											// documentText);

											hasText = true;
										}
										break;
									}
									if (xmlr.hasNext())
										xmlr.next();
								}
								xmlr.close();
								fr.close();
							} catch (XMLStreamException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							out.close();
							break;
						} else {
							System.out.println(entry.getName());
						}
					}

					tar.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// can now read the TAR contents
	}

	private List<File> getAllFilesFromDirectory(File directory) {

		// TODO delete method and use directory.listFiles() instead OR use this
		// method to exclude (sub)directories
		List<File> fileList = new ArrayList<File>();
		File[] dirFiles = directory.listFiles();
		for (int i = 0; i < dirFiles.length; i++) {
			if (!dirFiles[i].isDirectory()) {
				fileList.add(dirFiles[i]);
			}
		}

		return fileList;
	}

	/**
	 * xml parsing methods
	 */
	private static String getName(XMLStreamReader xmlr) {
		if (xmlr.hasName()) {
			String prefix = xmlr.getPrefix();
			String uri = xmlr.getNamespaceURI();
			String localName = xmlr.getLocalName();
			// printName(prefix, uri, localName);
			return localName;
		}
		return "";
	}

	private static String getTextUntilTag(XMLStreamReader xmlr, String tag) {

		String text = "";
		try {
			int exit = 0;
			while (xmlr.hasNext()) {
				switch (xmlr.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
					int start = xmlr.getTextStart();
					int length = xmlr.getTextLength();
					text += new String(xmlr.getTextCharacters(), start, length);
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (getName(xmlr) == tag)
						exit = 1;

					break;
				}
				if (exit == 1)
					break;
				if (xmlr.hasNext())
					xmlr.next();
				else
					break;
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}
}
