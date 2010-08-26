/**
 * 
 */
package collectionreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.poi.hssf.record.formula.functions.Maxa;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.arc.ARCRecord;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

import collectionreader.classes.ARCFileMetadata;
import collectionreader.classes.TimesDocumentStatistics;
import collectionreader.classes.TimesFileMetadata;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.ExtractorBase;
import de.l3s.database.LiwaDatabase;

import types.ArticleText;
import types.Crawl;
import types.Date;
import types.Title;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.soap.Text;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author tereza
 * 
 */
public class ARCCollectionReader extends CollectionReader_ImplBase {
	/**
	 * UIMA Logger for this class
	 */
	private static Logger logger;
	/**
	 * XML element of article title
	 */
	public static final String ELEMENT_ARTICLE_TITLE = "TITLE";

	/**
	 * XML element of content text
	 */
	public static final String ELEMENT_ARTICLE_TEXT = "ARTICLETEXT";

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
	public static final String PARAM_MAX_ARCHIVES = "MaxArchives";
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

	/**
	 * Known languages
	 */
	private static final String[] LANGUAGES = { "de", "en", "es", "fr", "it",
			"pt", "eng", "ger", "fre", "ita" };

	private ArrayList mFiles;

	private String mEncoding;

	private String mLanguage;

	/**
	 * Current file number
	 */
	private int currentIndex = 0;
	private int currentArchive = 0;
	private String archiveUrl = "";
	/**
	 * List of all files with abstracts XML
	 */
	private ConcurrentHashMap<String, ARCFileMetadata> filesMetadata;
	private List<String> files = new ArrayList<String>();
	private List<File> archives;

	private boolean withStatistics = false;
	// private boolean withStatistics = true;

	private static final Pattern PAT_CONTENTTYPE = Pattern
			.compile("Content-Type: text/html(?:;[ ]*[cC]harset=(.*))?");
	private static final Pattern PAT_HEADER = Pattern
			.compile(".*subject-uri=([^,]*),.*creation-date=([^,]*),.*");
	private static ExtractorBase extractor = ArticleExtractor.getInstance();

	/**
	 * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
	 */
	public void initialize() throws ResourceInitializationException {
		logger = getUimaContext().getLogger();
		logger.log(Level.INFO, "initialize() - Initializing ARC Reader...");

		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();
		// try{
		// builder = factory.newDocumentBuilder();
		// }catch(ParserConfigurationException e){
		// logger.log(Level.SEVERE, "initialize() "+e.getMessage());
		// throw new ResourceInitializationException(e);
		// }

		try {
			archives = getArchivesFromInput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,
					"get archives - archive input could not be read");
			e.printStackTrace();
		}

	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS cas) throws IOException, CollectionException {

		JCas jcas;
		try {
			jcas = cas.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}
		int filesInArchive = 0;
		// System.out.println(files.size()+" "+currentIndex);
		if (currentIndex == 0 || files.size() == currentIndex) {
			while (filesInArchive == 0 && archives.size() > currentArchive - 1) {
				System.out.println("new archive");
				// new archive
				File archive = archives.get(currentArchive++);
				archiveUrl = archive.getName();
				// System.out.println(archiveUrl);
				// need to extract all the archive metadata
				try {
					filesInArchive = extractFilesFromArchives(archive);
					System.out.println("Files in archive: " + filesInArchive);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		int thiscount = currentIndex;
		if (files.size() == 0 && filesInArchive == 0)
			return;
		//this is the last document in the archive
		boolean isLastFile = false;
		if(currentIndex == files.size()-1){
			isLastFile = true;
		}
		String file = files.get(currentIndex++);

		// if(currentIndex % 10000 == 0)
		// logger.log(Level.INFO, "getNext(CAS) - Reading file "+thiscount+" : "
		// + file);

		// InputStream fis = new FileInputStream(file);

		// while(!filesMetadata.containsKey(file)){

		// }
		ARCFileMetadata tfm = filesMetadata.get(file);
		String documentText = tfm.getText();
		String titleText = tfm.getTitle();
		String dateText = tfm.getDate();
		String archivename = archiveUrl;
		String crawlText = currentArchive + "_" + archivename;

		filesMetadata.remove(file);
		/*
		 * if(documentText != null && !documentText.contains("and") &&
		 * !documentText.contains("or") && !documentText.contains("the")){
		 * //document not in english //System.out.println(documentText);
		 * documentText = ""; }
		 */
		if (documentText != null ) {

			// System.out.println("new cas for: "+titleText);
			jcas.setDocumentText(titleText + "\n" + crawlText + "\n" + dateText
					+ "\n" + documentText);
			addArticleTitle(jcas, titleText);
			addCrawlInfo(jcas, currentArchive, archivename,isLastFile,titleText.length() + 1);
			addPubDate(jcas, dateText, titleText.length() + crawlText.length()
					+ 2);
			addArticleText(jcas, documentText, titleText.length()
					+ dateText.length() + 3); // +1 for

			// the new
			// line
			SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(
					jcas);
			// String file
			// srcDocInfo.setUri("file:///"+year+"/"+file);
			srcDocInfo.setOffsetInSource(0);
			srcDocInfo.setDocumentSize((int) documentText.length());
			srcDocInfo.setLastSegment(currentIndex == files.size());
			srcDocInfo.addToIndexes();
		}
		/*
		 * else if (titleText != null) {
		 * 
		 * logger .log(Level.FINE,
		 * "getNext(CAS) docuementText is null - setting only title as documentText"
		 * ); jcas.setDocumentText(titleText); addArticleTitle(jcas, titleText);
		 * addCrawlInfo(jcas, currentArchive, archivename, titleText.length() +
		 * 1); addPubDate(jcas, dateText, titleText.length() +
		 * crawlText.length() + 2);
		 * 
		 * }
		 */else {
			logger
					.log(Level.FINE,
							"getNext(CAS) docuementText is null - setting NO documentText");
		}

		// addHeader(jcas, doc);
		// addManualDesriptor(jcas, doc);

		// Also store location of source document in CAS. This information is
		// critical
		// if CAS Consumers will need to know where the original document
		// contents are located.
		// For example, the Semantic Search CAS Indexer writes this information
		// into the
		// search index that it creates, which allows applications that use the
		// search index to
		// locate the documents that satisfy their semantic queries.
		

		// fis.close();
	}

	public int extractFilesFromArchives(File archive) throws SQLException {
		InputStream input;

		int count = 0;
		// int year = Integer.parseInt(archive.getName().substring(0,
		// archive.getName().indexOf(".")));
		// Set<String> titles = LiwaDatabase.getArticlesForYear(year);
		// not care about db
		// titles = new HashSet<String>();

		System.out.println("reading archive " + archive.getName());
		int allcount = 0;
		List<TimesDocumentStatistics> statistics = new ArrayList<TimesDocumentStatistics>();

		if (filesMetadata == null) {
			filesMetadata = new ConcurrentHashMap<String, ARCFileMetadata>();
		}
		try {

			ARCReader cwreader = ARCReaderFactory.get(archive);
			Iterator it = cwreader.iterator();

			while (it.hasNext()) {
				allcount++;
			//	 if(count > 10){
				// break;
				// }
				boolean hasTitle = false;
				boolean hasDate = false;
				boolean hasText = false;
				String dateText = "";
				String titleText = "";
				String documentText = "";

				ARCRecord arrec = (ARCRecord) it.next();
				ArchiveRecordHeader header = arrec.getHeader();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						arrec), 1);
				String l;
				boolean ok = false;

				String encoding = "Cp1252";
				// String entryuri = null;
				// String crawldate = null;
				// String sofar = "";
				while ((l = br.readLine()) != null) {

					// sofar += l+"\n";
					l = l.trim();
					Matcher m = PAT_CONTENTTYPE.matcher(l);
					if (m.matches()) {

						ok = true;

						// System.out.println(header.toString());
						Matcher mheader = PAT_HEADER.matcher(header.toString());

						// System.out.println(sofar);
						String e = m.group(1);
						if (e != null) {
							encoding = e.trim();
							// System.out.println("encoding: "+encoding);
						}
						if (mheader.matches()) {
							String suri = mheader.group(1);
							String cdate = mheader.group(2);
							if (suri != null) {
								titleText = suri.trim();
								hasTitle = true;
							}
							if (cdate != null) {
								dateText = cdate.trim();
								hasDate = true;
							}
						}

					}

					if (l.length() == 0) {
						break;
					}
				}

				if (!ok) {

					continue;

				}
				Charset cs;
				try {
					cs = Charset.forName(encoding);
				} catch (UnsupportedCharsetException e) {
					cs = Charset.forName("UTF-8");
				}
				InputSource is = new InputSource(new InputStreamReader(arrec,
						cs));
				String text;
				try {
					documentText = extractor.getText(is);
					if (documentText != null) {
						hasText = true;
					}
					// System.out.println(text);

				} catch (BoilerpipeProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
					// break;
				}

				if (documentText.length() > 0
						&& !filesMetadata.containsKey(titleText) && (documentText.contains("and") || documentText.contains("or")  || documentText.contains("the"))) {
					// System.out.println(fileName+" "+titleText);
					filesMetadata.put(titleText, new ARCFileMetadata(titleText,
							dateText, documentText));
					// System.out.println("title: "+titleText+" nwords: "+java.util.regex.Pattern.compile("[\\S]+").split(documentText.trim()).length);

					// System.out.println("title: "+titleText);
					// System.out.println(documentText);

					// TimesFileMetadata tfm = new
					// TimesFileMetadata(titleText,dateText,documentText);

					files.add(titleText);

					count++;
					/*
					 * if(withStatistics){ statistics.add(new
					 * TimesDocumentStatistics
					 * (year,titleText,documentText.length
					 * (),java.util.regex.Pattern
					 * .compile("[\\S]+").split(documentText.trim()).length));
					 * 
					 * if(count%5000 == 0){ if(statistics.size()>0){
					 * LiwaDatabase.insertStatisticsArchive(statistics);
					 * statistics = new ArrayList<TimesDocumentStatistics>(); }
					 * } } // System.out.println("added: "+fileName);
					 */
				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(allcount);
		return count;
	}

	/*
	 * public void getNext(CAS cas) throws IOException, CollectionException {
	 * 
	 * JCas jcas; try { jcas = cas.getJCas(); } catch (CASException e) { throw
	 * new CollectionException(e); } File file = files.get(currentIndex++);
	 * 
	 * logger.log(Level.INFO, "getNext(CAS) - Reading file " + file.getName());
	 * 
	 * InputStream fis = new FileInputStream(file);
	 * 
	 * try {
	 * 
	 * Document doc = builder.parse(file);
	 * 
	 * String documentText = getElementContent(doc, ELEMENT_ARTICLE_TEXT);
	 * logger.log(Level.FINE, "getNext(CAS) - documentText=" + documentText);
	 * 
	 * String titleText = getElementContent(doc, ELEMENT_ARTICLE_TITLE);
	 * logger.log(Level.FINE, "getNext(CAS) - titleText=" + titleText);
	 * 
	 * String dateText = getElementContent(doc, ELEMENT_ISSUE_DATE);
	 * logger.log(Level.FINE, "getNext(CAS) - titleDate=" + dateText);
	 * 
	 * if (documentText != null) {
	 * 
	 * jcas.setDocumentText(titleText + "\n" + dateText+"\n"+documentText);
	 * addArticleTitle(jcas, titleText);
	 * addPubDate(jcas,dateText,titleText.length() + 1); addArticleText(jcas,
	 * documentText,titleText.length() + dateText.length()+2); // +1 for
	 * 
	 * // the new // line } else if (titleText != null) {
	 * 
	 * logger.log(Level.FINE,
	 * "getNext(CAS) docuementText is null - setting only title as documentText"
	 * ); jcas.setDocumentText(titleText); addArticleTitle(jcas, titleText);
	 * addPubDate(jcas,dateText,titleText.length() + 1);
	 * 
	 * } else { logger.log(Level.FINE,
	 * "getNext(CAS) docuementText and titleText are null - setting NO documentText"
	 * ); }
	 * 
	 * 
	 * 
	 * // addHeader(jcas, doc); // addManualDesriptor(jcas, doc);
	 * 
	 * // Also store location of source document in CAS. This information is
	 * critical // if CAS Consumers will need to know where the original
	 * document contents are located. // For example, the Semantic Search CAS
	 * Indexer writes this information into the // search index that it creates,
	 * which allows applications that use the search index to // locate the
	 * documents that satisfy their semantic queries. SourceDocumentInformation
	 * srcDocInfo = new SourceDocumentInformation(jcas);
	 * srcDocInfo.setUri(file.getAbsoluteFile().toURL().toString());
	 * srcDocInfo.setOffsetInSource(0); srcDocInfo.setDocumentSize((int)
	 * file.length()); srcDocInfo.setLastSegment(currentIndex == files.size());
	 * srcDocInfo.addToIndexes(); } catch (SAXException e) {
	 * logger.log(Level.SEVERE, "getNext(CAS)" + e.getMessage()); throw new
	 * CollectionException(e); } fis.close(); }
	 */
	/**
	 * Add the AbstractText Type to the JCAS
	 * 
	 * @param jcas
	 * @param documentText
	 */
	private void addArticleText(JCas jcas, String documentText, int offset) {
		ArticleText abstractText = new ArticleText(jcas);
		abstractText.setBegin(offset);
		abstractText.setEnd(offset + documentText.length());
		abstractText.addToIndexes(jcas);
	}

	/**
	 * Add the AbstractTitle Type to the JCas
	 * 
	 * @param jcas
	 *            The jcas
	 * @param titleText
	 *            Text that represents the title of the abstract
	 */
	private void addArticleTitle(JCas jcas, String abstractText) {
		Title title = new Title(jcas);
		title.setBegin(0);
		title.setEnd(abstractText.length());
		title.addToIndexes(jcas);
	}

	/**
	 * Get files from directory that is specified in the configuration parameter
	 * PARAM_INPUTDIR of the collection reader descriptor.
	 * 
	 * @throws ResourceInitializationException
	 *             thrown if there is a problem with a configuration parameter
	 */
	private List<File> getFilesFromInputDirectory()
			throws ResourceInitializationException {

		currentIndex = 0;
		if (isSingleProcessing()) {
			return getSingleFile();
		}

		String directoryName = (String) getConfigParameterValue(PARAM_INPUT_DIR);
		int maxArchives = (Integer)getConfigParameterValue(PARAM_MAX_ARCHIVES);
		logger.log(Level.INFO, PARAM_INPUT_DIR + "=" + directoryName+ " "+PARAM_MAX_ARCHIVES+"="+maxArchives);
		if (directoryName == null) {
			throw new ResourceInitializationException(new Exception() {

				public String getMessage() {
					return "Value of configuration parameter "
							+ PARAM_INPUT_DIR + " was not found";
				}
			});
		}

		File inputDirectory = new File(directoryName.trim());
		if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
			// TODO delete logging message when it is assured that exception
			// handling in clients is done properly
			logger.log(Level.WARNING, "getFilesFromInputDirectory() "
					+ inputDirectory
					+ " does not exist. Invalid configuration parameter '"
					+ PARAM_INPUT_DIR + "'.");
			throw new ResourceInitializationException(
					new FileNotFoundException(
							"Value of configuration parameter "
									+ PARAM_INPUT_DIR
									+ " is not existing or not a directory: "
									+ directoryName));
		}

		return getAllFilesFromDirectory(inputDirectory,maxArchives);
	}

	private List<File> getArchivesFromInput()
			throws ResourceInitializationException, IOException {

		currentIndex = 0;
		currentArchive = 0;

		String inputName = (String) getConfigParameterValue(PARAM_INPUT_DIR);
		int maxArchives = (Integer)getConfigParameterValue(PARAM_MAX_ARCHIVES);
		logger.log(Level.INFO, PARAM_INPUT_DIR + "=" + inputName+ " "+PARAM_MAX_ARCHIVES+"="+maxArchives);
		if (inputName == null) {
			throw new ResourceInitializationException(new Exception() {

				public String getMessage() {
					return "Value of configuration parameter "
							+ PARAM_INPUT_DIR + " was not found";
				}
			});
		}

		File inputDirectory = new File(inputName.trim());
		if (inputDirectory.exists() && inputDirectory.isDirectory()) {
			System.out.println(inputName + " is a directory");
			// TODO delete logging message when it is assured that exception
			// handling in clients is done properly
			logger.log(Level.INFO, "getFilesFromInput() " + inputDirectory
					+ " is a directory. ");
			// throw new ResourceInitializationException(new
			// FileNotFoundException("Value of configuration parameter "+
			// PARAM_INPUT_DIR + " is not existing or not a directory: " +
			// directoryName));
			return getAllFilesFromDirectory(inputDirectory,maxArchives);
		} else {
			if (!existsURL(inputName)) {
				logger.log(Level.WARNING, "getFilesFromInput() "
						+ inputDirectory
						+ " is not a directory nor a valid URL. ");
				throw new ResourceInitializationException(
						new FileNotFoundException(
								"Value of configuration parameter "
										+ PARAM_INPUT_DIR
										+ " is not existing or not a directory, or not an URL: "
										+ inputName));

			}
			return getAllFilesFromURL(inputName);
		}

	}

	private List<File> getArchivesFromInputDirectory()
			throws ResourceInitializationException {

		currentIndex = 0;
		currentArchive = 0;

		String directoryName = (String) getConfigParameterValue(PARAM_INPUT_DIR);
		int maxArchives = (Integer)getConfigParameterValue(PARAM_MAX_ARCHIVES);
		logger.log(Level.INFO, PARAM_INPUT_DIR + "=" + directoryName+ " "+PARAM_MAX_ARCHIVES+"="+maxArchives);
		
		if (directoryName == null) {
			throw new ResourceInitializationException(new Exception() {

				public String getMessage() {
					return "Value of configuration parameter "
							+ PARAM_INPUT_DIR + " was not found";
				}
			});
		}

		File inputDirectory = new File(directoryName.trim());
		if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
			// TODO delete logging message when it is assured that exception
			// handling in clients is done properly
			logger.log(Level.WARNING, "getFilesFromInputDirectory() "
					+ inputDirectory
					+ " does not exist. Invalid configuration parameter '"
					+ PARAM_INPUT_DIR + "'.");
			throw new ResourceInitializationException(
					new FileNotFoundException(
							"Value of configuration parameter "
									+ PARAM_INPUT_DIR
									+ " is not existing or not a directory: "
									+ directoryName));
		}

		return getAllFilesFromDirectory(inputDirectory,maxArchives);
	}

	/**
	 * All files in the directory a asumed to be wanted documents (artifacts)
	 * 
	 * @param directory
	 *            the directory that contains only wanted artifacts
	 * @return a {@link List} of all {@link File}s in the direcotry
	 */
	private List<File> getAllFilesFromDirectory(File directory,int maxArchives) {

		// TODO delete method and use directory.listFiles() instead OR use this
		// method to exclude (sub)directories
		List<File> fileList = new ArrayList<File>();
		File[] dirFiles = directory.listFiles();
		int count = 0;
		for (int i = 0; i < dirFiles.length; i++) {
			if (!dirFiles[i].isDirectory()
					&& (dirFiles[i].getName().endsWith(".gz")) && count < maxArchives) {
				
				String fileName = dirFiles[i].getName();
			
				int id=LiwaDatabase.existsCrawl(fileName);
				
				if(LiwaDatabase.existsCrawl(fileName)>0){
					System.out.println("archive has been processed already: "+fileName);
				}else{
					
					fileList.add(dirFiles[i]);
					count ++;
				}
			}
		}
		logger.log(Level.INFO, "ARC Reader found " + fileList.size()
				+ " archives in folder " + directory + ".");
		System.out.println("ARC Reader found " + fileList.size()
				+ " archives in folder " + directory + ".");
		return fileList;
	}

	/**
	 * All files in the directory a asumed to be wanted documents (artifacts)
	 * 
	 * @param url
	 *            the directory that contains only wanted artifacts
	 * @return a {@link List} of all {@link File}s in the direcotry
	 * @throws IOException
	 */
	private List<File> getAllFilesFromURL(String urlname) throws IOException {

		// TODO delete method and use directory.listFiles() instead OR use this
		// method to exclude (sub)directories
		List<File> fileList = new ArrayList<File>();
		URL url = new URL(urlname);

		BufferedReader in = new BufferedReader(new InputStreamReader(url
				.openStream()));
		String filename;

		while ((filename = in.readLine()) != null) {
			File file = new File(filename);
			if (!file.isDirectory() && filename.endsWith("arc.gz")) {
				String fileName = file.getName();
				if(LiwaDatabase.existsCrawl(fileName)>0){
					System.out.println("archive has been processed: "+fileName);
				}else{
					fileList.add(file);
				}
				
			}
		}

		in.close();

		logger.log(Level.INFO, "ARC Reader found " + fileList.size()
				+ " archives at the url " + urlname + ".");
		System.out.println("ARC Reader found " + fileList.size()
				+ " archives at the url " + urlname + ".");
		return fileList;
	}

	/**
	 * Get a list of the single file defined in the descriptor
	 * 
	 * @return
	 * @throws ResourceInitializationException
	 */
	private List<File> getSingleFile() throws ResourceInitializationException {

		logger.log(Level.INFO,
				"getSingleFile() - MedlineReader is used in SINGLE FILE mode.");
		String singleFile = (String) getConfigParameterValue(PARAM_INPUT_FILE);

		if (singleFile == null) {
			return null;
		}
		File file = new File(singleFile.trim());
		if (!file.exists() || file.isDirectory()) {
			String message = file
					+ " does not exist or is a directory. Invalid configuration parameter '"
					+ PARAM_INPUT_FILE + "'.";
			logger.log(Level.WARNING, "getSingleFile() " + message);
			throw new ResourceInitializationException(new Exception() {

				public String getMessage() {
					return "Value of configuration parameter "
							+ PARAM_INPUT_FILE
							+ " is not existing or is a directory.";
				}
			});
		}
		List<File> fileList = new ArrayList<File>();
		fileList.add(file);
		return fileList;
	}

	/**
	 * Determines form the descriptor if this CollectionReader should only
	 * process a single file. . <b>The parameter must not have a value! It is
	 * sufficient to be defined to return <code>true</code></b>
	 * 
	 * @return <code>true</code> if there is a parameter defined called like the
	 *         value of PARAM_INPUT_FILE
	 */
	private boolean isSingleProcessing() {

		ConfigurationParameterDeclarations declarations = this
				.getProcessingResourceMetaData()
				.getConfigurationParameterDeclarations();
		ConfigurationParameter param = declarations.getConfigurationParameter(
				null, PARAM_INPUT_FILE);
		if (param == null) {
			return false;
		}
		return true;
	}

	/**
	 * Creates a Date object and fills it with data from the XML Document doc
	 * 
	 * @param jcas
	 *            The associated JCas
	 * @param doc
	 *            XML Document
	 * @return Date, or null if ELEMENT_PUB_DATE does not exist
	 */
	private Date addPubDate(JCas jcas, String dateText, int offset) {

		Date date = new Date(jcas);

		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
		java.util.Date d;
		try {
			// System.out.println(dateText.substring(0, 8));
			d = sdf.parse(dateText.substring(0, 8));

			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			// System.out.println(cal.get(Calendar.MONTH)+1);
			date.setDay(cal.get(Calendar.DAY_OF_MONTH));
			date.setMonth(cal.get(Calendar.MONTH) + 1);
			date.setYear(cal.get(Calendar.YEAR));

			date.setBegin(offset);
			date.setEnd(offset + dateText.length() + 1);

			// System.out.println(date.toString());
			date.addToIndexes(jcas);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Creates a Crawl object and fills it with data from the XML Document doc
	 * 
	 * @param jcas
	 *            The associated JCas
	 * @param doc
	 *            XML Document
	 * @return boolean, or null if ELEMENT_PUB_DATE does not exist
	 */
	private Crawl addCrawlInfo(JCas jcas, int crawlId, String crawlFilename,boolean isLastFile,
			int offset) {

		Crawl crawl = new Crawl(jcas);

		crawl.setCrawlId(crawlId);
		crawl.setFilename(crawlFilename);
		crawl.setLastDocument(isLastFile);
		crawl.setBegin(offset);
		String crawlText = crawlId + "_" + crawlFilename;
		crawl.setEnd(offset + crawlText.length() + 1);
		crawl.addToIndexes(jcas);

		return crawl;
	}

	/**
	 * Adds a ManualDescriptor Type extracted from XML document doc object to
	 * the JCas jcas
	 * 
	 * @param jcas
	 *            The CAS the ManualDescriptor will be contained in
	 * @param doc
	 *            The XML document
	 */
	/*
	 * private void addManualDesriptor(JCas jcas, Document doc) {
	 * ManualDescriptor manualDescriptor = new ManualDescriptor(jcas); FSArray
	 * meSHArray = createMeshListArray(jcas, doc); if (meSHArray != null) {
	 * manualDescriptor.setMeSHList(meSHArray); meSHArray.addToIndexes(); }
	 * FSArray chemicalsArray = createChemicalListArray(jcas, doc); if
	 * (chemicalsArray != null) {
	 * manualDescriptor.setChemicalList(chemicalsArray);
	 * chemicalsArray.addToIndexes(); } FSArray keywordArray =
	 * createKeywordListArray(jcas, doc); if (keywordArray != null) {
	 * manualDescriptor.setKeywordList(keywordArray);
	 * keywordArray.addToIndexes(); } FSArray dbInfoArray =
	 * createDbInfoListArray(jcas, doc); if (dbInfoArray != null) {
	 * manualDescriptor.setDBInfoList(dbInfoArray); dbInfoArray.addToIndexes();
	 * } StringArray geneSymbolArray = createGeneSymbolListArray(jcas, doc); if
	 * (geneSymbolArray != null) {
	 * manualDescriptor.setGeneSymbolList(geneSymbolArray); // Caution: it is
	 * not necessacry or even harmful to add the StringArray to // the indexes
	 * !? // geneSymbolArray.addToIndexes(); }
	 * 
	 * manualDescriptor.addToIndexes(); }
	 */
	/**
	 * Adds a Header type extracted from XML document doc object to the JCas
	 * jcas
	 * 
	 * @param jcas
	 *            The CAS the Header will be contained in
	 * @param doc
	 *            The XML document
	 */
	/*
	 * private void addHeader(JCas jcas, Document doc) {
	 * 
	 * Header header = new Header(jcas);
	 * 
	 * FSArray authorsArray = createAuthorsArray(jcas, doc); if (authorsArray !=
	 * null) { header.setAuthors(authorsArray); authorsArray.addToIndexes(); }
	 * 
	 * FSArray pubTypeArray = createPubTypeArray(jcas, doc); if (pubTypeArray !=
	 * null) { header.setPubTypeList(pubTypeArray); pubTypeArray.addToIndexes();
	 * }
	 * 
	 * String status = getUniqueElementAttribute(doc, ELEMENT_MEDLINE_CITATION,
	 * ATTRIBUTE_STATUS); if (status != null) {
	 * header.setCitationStatus(status); } String language =
	 * getElementContent(doc, ELEMENT_LANGUAGE); if (language != null) { if
	 * (isOtherLanguage(language)) { header.setLanguage(LANGUAGE_OTHER); } else
	 * { header.setLanguage(language); } } String pmid = getElementContent(doc,
	 * ELEMENT_PMID); if (pmid != null) { header.setDocId(pmid); }
	 * 
	 * header.setSource(TEXT_SOURCE); header.addToIndexes(); }
	 */
	/**
	 * Determines if the languange is not known
	 * 
	 * @param language
	 * @return
	 */
	private boolean isOtherLanguage(String language) {
		for (int i = 0; i < LANGUAGES.length; i++) {
			if (language.equals(LANGUAGES[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Fills an array with the names of gene symbols found in the XML document
	 * <code>doc</code>
	 * 
	 * @param jcas
	 *            The CAS the array will be contained in
	 * @param doc
	 *            The XML document
	 * @return An array with gene symbol names, or null is element does not
	 *         exist
	 */
	/*
	 * private StringArray createGeneSymbolListArray(JCas jcas, Document doc) {
	 * 
	 * StringArray array = null;
	 * 
	 * NodeList geneSymbols = getUniqueElementChilds(doc,
	 * ELEMENT_GENE_SYMBOL_LIST);
	 * 
	 * if (geneSymbols != null) {
	 * 
	 * ArrayList<String> geneSymbolList = new ArrayList<String>(); int
	 * geneSymbolCount = 0; for (int i = 0; i < geneSymbols.getLength(); i++) {
	 * 
	 * Node geneSymbolNode = geneSymbols.item(i);
	 * 
	 * if (geneSymbolNode.getNodeType() == Node.ELEMENT_NODE) {
	 * geneSymbolList.add(geneSymbolNode.getTextContent()); geneSymbolCount++; }
	 * } array = new StringArray(jcas, geneSymbolCount);
	 * copyToStringArray(geneSymbolList, array); } return array; }
	 */
	/**
	 * Fills an FSArray with DBInfo objects from the XML document
	 * <code>doc</code>
	 * 
	 * @param jcas
	 *            The CAS the FSArray will be contained in
	 * @param doc
	 *            The XML document
	 * @return An array with DBInfo objects, or null if element
	 *         ELEMENT_DATA_BANK_LIST does not exist
	 */
	/*
	 * private FSArray createDbInfoListArray(JCas jcas, Document doc) {
	 * 
	 * FSArray array = null;
	 * 
	 * NodeList dbs = getUniqueElementChilds(doc, ELEMENT_DATA_BANK_LIST);
	 * 
	 * if (dbs != null) {
	 * 
	 * int dbCount = 0; ArrayList<DBInfo> dbList = new ArrayList<DBInfo>();
	 * 
	 * for (int i = 0; i < dbs.getLength(); i++) {
	 * 
	 * Node dbNode = dbs.item(i);
	 * 
	 * if (dbNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * NodeList dbItems = dbNode.getChildNodes();
	 * 
	 * DBInfo dbInfo = new DBInfo(jcas); dbCount++;
	 * 
	 * for (int j = 0; j < dbItems.getLength(); j++) { Node dbItem =
	 * dbItems.item(j);
	 * 
	 * if (dbItem.getNodeName().equals(ELEMENT_DATA_BANK_NAME)) {
	 * dbInfo.setName(dbItem.getTextContent()); }
	 * 
	 * if (dbItem.getNodeName().equals(ELEMENT_ACCESSION_NUMBER_LIST)) {
	 * NodeList accessionNumbers = dbItem.getChildNodes();
	 * 
	 * ArrayList<String> accessionNumberList = new ArrayList<String>(); int
	 * accessionNumberCount = 0; for (int k = 0; k <
	 * accessionNumbers.getLength(); k++) {
	 * 
	 * Node accessionNumberNode = accessionNumbers.item(k);
	 * 
	 * if (accessionNumberNode.getNodeType() == Node.ELEMENT_NODE) {
	 * accessionNumberCount++;
	 * accessionNumberList.add(accessionNumberNode.getTextContent()); } }
	 * StringArray accessionNumberArray = new StringArray(jcas,
	 * accessionNumberCount); copyToStringArray(accessionNumberList,
	 * accessionNumberArray); dbInfo.setAcList(accessionNumberArray); } }
	 * 
	 * dbInfo.addToIndexes(); dbList.add(dbInfo); } } array = new FSArray(jcas,
	 * dbCount); copyToFSArray(dbList, array); } return array; }
	 */
	/**
	 * Creates a FSArray with PubTypes found in the XML document <code>doc</doc>
	 * 
	 * @param jcas
	 *            The CAS the FSArray will be contained in
	 * @param doc
	 *            The XML document
	 * @return An array with PubTypes, or null if ELEMENT_PUBLICATION_TYPE_LIST
	 *         does not exist
	 */
	/*
	 * private FSArray createPubTypeArray(JCas jcas, Document doc) {
	 * 
	 * FSArray array = null;
	 * 
	 * NodeList pubTypes = getUniqueElementChilds(doc,
	 * ELEMENT_PUBLICATION_TYPE_LIST);
	 * 
	 * if (pubTypes != null) {
	 * 
	 * array = new FSArray(jcas, pubTypes.getLength());
	 * 
	 * for (int i = 0; i < pubTypes.getLength(); i++) {
	 * 
	 * Node pubTypeNode = pubTypes.item(i);
	 * 
	 * if (pubTypeNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * String pubTypeString = pubTypeNode.getTextContent();
	 * 
	 * if (pubTypeString != null && pubTypeString.length() >
	 * TEXT_JOURNAL.length() && pubTypeString.substring(0,
	 * TEXT_JOURNAL.length()).equals(TEXT_JOURNAL)) {
	 * 
	 * String issn = getElementContent(doc, ELEMENT_ISSN); String volume =
	 * getElementContent(doc, ELEMENT_VOLUME); String title =
	 * getElementContent(doc, ELEMENT_TITLE); String shortTitle =
	 * getElementContent(doc, ELEMENT_SHORT_TITLE); String issue =
	 * getElementContent(doc, ELEMENT_ISSUE); String pages =
	 * getElementContent(doc, ELEMENT_PAGES); Date pubDate = createPubDate(jcas,
	 * doc);
	 * 
	 * Journal journal = new Journal(jcas); if (issn != null) {
	 * journal.setISSN(issn); } if (volume != null) { journal.setVolume(volume);
	 * } if (title != null) { journal.setTitle(title); } if (shortTitle != null)
	 * { journal.setShortTitle(shortTitle); } if (pubDate != null) {
	 * journal.setPubDate(pubDate); } if (pubTypeString != null) {
	 * journal.setName(pubTypeString); } if (issue != null) {
	 * journal.setIssue(issue); } if (pages != null) { journal.setPages(pages);
	 * }
	 * 
	 * journal.addToIndexes(); array.set(i, journal);
	 * 
	 * } else { OtherPub otherPub = new OtherPub(jcas);
	 * otherPub.setName(pubTypeString);
	 * 
	 * otherPub.addToIndexes();
	 * 
	 * array.set(i, otherPub); } } } } return array; }
	 */
	/**
	 * Fills an FSArray with AuthorInfo objects
	 * 
	 * @param jcas
	 *            The CAS the FSArray will be contained in
	 * @param doc
	 *            The XML document
	 * @return array with AuthorInfo objects, or null if ELEMENT_AUTHOR_LIST
	 *         does not exist
	 */
	/*
	 * private FSArray createAuthorsArray(JCas jcas, Document doc) {
	 * 
	 * FSArray array = null;
	 * 
	 * NodeList authorNodeList = getUniqueElementChilds(doc,
	 * ELEMENT_AUTHOR_LIST);
	 * 
	 * if (authorNodeList != null) {
	 * 
	 * ArrayList<FeatureStructure> authorList = new
	 * ArrayList<FeatureStructure>(); int authorCount = 0;
	 * 
	 * for (int i = 0; i < authorNodeList.getLength(); i++) { Node authorNode =
	 * authorNodeList.item(i);
	 * 
	 * if (authorNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * authorCount++; NodeList authorChildNodes = authorNode.getChildNodes();
	 * 
	 * AuthorInfo authorInfo = new AuthorInfo(jcas); for (int j = 0; j <
	 * authorChildNodes.getLength(); j++) {
	 * 
	 * Node authorChild = authorChildNodes.item(j);
	 * 
	 * if (authorChild.getNodeName().equals(ELEMENT_FORE_NAME)) {
	 * authorInfo.setForeName(authorChild.getTextContent()); } else if
	 * (authorChild.getNodeName().equals(ELEMENT_LAST_NAME)) {
	 * authorInfo.setLastName(authorChild.getTextContent()); } else if
	 * (authorChild.getNodeName().equals(ELEMENT_INITIALS)) {
	 * authorInfo.setInitials(authorChild.getTextContent()); } } // Set
	 * affiliation only to the first author found (this could be //
	 * non-deterministic // due to specific XML parser implementation !!!) if
	 * (authorCount == 1) { String affiliation = getElementContent(doc,
	 * ELEMENT_AFFILIATION); if (affiliation != null) {
	 * authorInfo.setAffiliation(affiliation); } }
	 * 
	 * authorInfo.addToIndexes(); authorList.add(authorInfo); } array = new
	 * FSArray(jcas, authorList.size()); copyToFSArray(authorList, array); } }
	 * return array; }
	 */
	/**
	 * Copy an ArrayList of FeatrueStructure objects to a FSArray
	 * 
	 * @param list
	 *            List to be copied to
	 * @param array
	 *            Array to be copied from
	 */
	/*
	 * private void copyToFSArray(ArrayList list, FSArray array) {
	 * 
	 * for (int i = 0; i < list.size(); i++) { array.set(i, (FeatureStructure)
	 * list.get(i)); } }
	 */
	/**
	 * Copy an ArrayList of FeatrueStructure objects to a StringArray
	 * 
	 * @param stringList
	 *            List to be copied from
	 * @param stringArray
	 *            Array to be copied to
	 */
	private void copyToStringArray(ArrayList<String> stringList,
			StringArray stringArray) {

		for (int i = 0; i < stringList.size(); i++) {
			stringArray.set(i, (String) stringList.get(i));
		}
	}

	/**
	 * Create an FSArray filled with Keyword objects extracted from the XML
	 * Document doc
	 * 
	 * @param jcas
	 *            The CAS the FSArray will be contained in
	 * @param doc
	 *            The XML document
	 * @return An array with KeyWord objects, or null if element
	 *         ELEMENT_KEYWORD_LIST does not exist
	 */
	/*
	 * private FSArray createKeywordListArray(JCas jcas, Document doc) {
	 * 
	 * FSArray array = null;
	 * 
	 * NodeList keywords = getUniqueElementChilds(doc, ELEMENT_KEYWORD_LIST);
	 * 
	 * if (keywords != null) {
	 * 
	 * ArrayList<Keyword> keywordList = new ArrayList<Keyword>();
	 * 
	 * int keywordCount = 0; for (int i = 0; i < keywords.getLength(); i++) {
	 * 
	 * Node keywordNode = keywords.item(i);
	 * 
	 * if (keywordNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * Keyword keyword = new Keyword(jcas);
	 * keyword.setName(keywordNode.getTextContent());
	 * 
	 * keyword.addToIndexes(); keywordList.add(keyword); keywordCount++; } }
	 * array = new FSArray(jcas, keywordCount); copyToFSArray(keywordList,
	 * array); } return array; }
	 */
	/**
	 * Create an FSArray filled with MeshHeading objects extracted from the XML
	 * Document doc
	 * 
	 * @param jcas
	 *            The CAS that will contain the FSArray
	 * @param doc
	 *            The XML document
	 * @return An array with MeshHeading objects, or null if element
	 *         ELEMENT_MESH_HEADING_LIST does not exist
	 */
	/*
	 * private FSArray createMeshListArray(JCas jcas, Document doc) {
	 * 
	 * FSArray array = null;
	 * 
	 * NodeList meshs = getUniqueElementChilds(doc, ELEMENT_MESH_HEADING_LIST);
	 * 
	 * if (meshs != null) {
	 * 
	 * array = new FSArray(jcas, meshs.getLength());
	 * 
	 * for (int i = 0; i < meshs.getLength(); i++) {
	 * 
	 * Node meshNode = meshs.item(i);
	 * 
	 * if (meshNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * NodeList meshValues = meshNode.getChildNodes();
	 * 
	 * MeshHeading meshHeading = new MeshHeading(jcas); for (int j = 0; j <
	 * meshValues.getLength(); j++) {
	 * 
	 * Node node = meshValues.item(j); String nodeName = node.getNodeName();
	 * boolean isMayorTopic; if (nodeName.equals(ELEMENT_DESCRIPTOR_NAME)) {
	 * isMayorTopic = getIsMajorTopic(node);
	 * meshHeading.setDescriptorNameMajorTopic(isMayorTopic);
	 * meshHeading.setDescriptorName(node.getTextContent()); } if
	 * (nodeName.equals(ELEMENT_QUALIFIER_NAME)) {
	 * 
	 * isMayorTopic = getIsMajorTopic(node);
	 * meshHeading.setQualifierNameMajorTopic(isMayorTopic);
	 * meshHeading.setQualifierName(node.getTextContent()); } }
	 * 
	 * meshHeading.addToIndexes(); array.set(i, meshHeading); } } } return
	 * array; }
	 */
	/**
	 * Determines if the XML element node
	 * <code>node</node> is a mayor topic according to the value of the 
		 * 	attribute ATTRIBUTE_MAYOR_TOPIC
	 * 
	 * @param node
	 *            The explored node
	 * @return true, if the attribute ATTRIBUTE_MAYOR_TOPIC has the content "Y"
	 */
	/*
	 * private boolean getIsMajorTopic(Node node) {
	 * 
	 * NamedNodeMap attributesList = node.getAttributes();
	 * 
	 * boolean isMayorTopic = false; for (int k = 0; k <
	 * attributesList.getLength(); k++) {
	 * 
	 * Node attribute = attributesList.item(k);
	 * 
	 * if (attribute.getNodeName().equals(ATTRIBUTE_MAJOR_TOPIC_YN)) {
	 * 
	 * String yesNo = attribute.getTextContent(); if (yesNo.equals("Y")) {
	 * isMayorTopic = true; } } } return isMayorTopic; }
	 */
	/**
	 * Create an FSArray filled with Chemical objects extracted from the XML
	 * Document doc
	 * 
	 * @param jcas
	 *            Assiciated JCas
	 * @param doc
	 *            XML Document
	 * @return Array with Chemical objects, or null if element
	 *         ELEMENT_CHEMICAL_LIST does not exist
	 */
	/*
	 * private FSArray createChemicalListArray(JCas jcas, Document doc) {
	 * 
	 * FSArray array = null;
	 * 
	 * NodeList chemicals = getUniqueElementChilds(doc, ELEMENT_CHEMICAL_LIST);
	 * 
	 * if (chemicals != null) {
	 * 
	 * array = new FSArray(jcas, chemicals.getLength());
	 * 
	 * for (int i = 0; i < chemicals.getLength(); i++) {
	 * 
	 * Node chemicalNode = chemicals.item(i);
	 * 
	 * if (chemicalNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * NodeList nodes = chemicalNode.getChildNodes();
	 * 
	 * Chemical chemical = new Chemical(jcas); for (int j = 0; j <
	 * nodes.getLength(); j++) {
	 * 
	 * Node node = nodes.item(j);
	 * 
	 * String nodeName = node.getNodeName(); if
	 * (nodeName.equals(ELEMENT_REGISTRY_NUMBER)) { String regNr =
	 * node.getTextContent(); chemical.setRegistryNumber(regNr); } if
	 * (nodeName.equals(ELEMENT_NAME_OF_SUBSTANCE)) { String nameOfSubstance =
	 * node.getTextContent(); chemical.setNameOfSubstance(nameOfSubstance); } }
	 * 
	 * chemical.addToIndexes(); array.set(i, chemical); } } } return array; }
	 */
	/**
	 * Creates a Date object and fills it with data from the XML Document doc
	 * 
	 * @param jcas
	 *            The associated JCas
	 * @param doc
	 *            XML Document
	 * @return Date, or null if ELEMENT_PUB_DATE does not exist
	 */
	/*
	 * private Date createPubDate(JCas jcas, Document doc) {
	 * 
	 * Date date = null;
	 * 
	 * NodeList pubDateValues = getUniqueElementChilds(doc, ELEMENT_PUB_DATE);
	 * 
	 * if (pubDateValues != null) {
	 * 
	 * date = new Date(jcas);
	 * 
	 * for (int i = 0; i < pubDateValues.getLength(); i++) {
	 * 
	 * Node pubDateValue = pubDateValues.item(i); String pubDateContent =
	 * pubDateValue.getTextContent();
	 * 
	 * if (pubDateValue.getNodeName().equals(ELEMENT_DAY)) {
	 * date.setDay(Integer.parseInt(pubDateContent)); }
	 * 
	 * if (pubDateValue.getNodeName().equals(ELEMENT_MONTH)) {
	 * date.setMonth(parseMonthFormString(pubDateContent)); }
	 * 
	 * if (pubDateValue.getNodeName().equals(ELEMENT_YEAR)) {
	 * date.setYear(Integer.parseInt(pubDateContent)); } if
	 * (pubDateValue.getNodeName().equals(ELEMENT_MEDLINE_DATE)) {
	 * putMedlineDate(date, pubDateContent); break; } }
	 * 
	 * date.addToIndexes(); } return date; }
	 */
	/**
	 * Converts an 3-character abbreviated month to an int
	 * 
	 * @param abbreviation
	 *            The abbreviation to be parsed
	 * @return A number corresponding to the 3-character abbriviation of the
	 *         month (1-based, that means, 'Jan' corresponds to 1)
	 */
	/*
	 * private int parseMonthFormString(String abbreviation) {
	 * 
	 * for (int i = 0; i < MONTHS.length; i++) { if
	 * (MONTHS[i].equals(abbreviation)) { return i + 1; } } return 0; }
	 */
	/**
	 * Puts the content of the ELEMENT_DELINE_DATE tag into a
	 * de.julielab.jules.types.Date
	 * 
	 * @param date
	 *            The date to be filled
	 * @param content
	 *            Text from what the date is extracted
	 */
	/*
	 * private void putMedlineDate(Date date, String content) {
	 * 
	 * ArrayList<Integer> years = new ArrayList<Integer>(); ArrayList<Integer>
	 * days = new ArrayList<Integer>(); ArrayList<String> months = new
	 * ArrayList<String>();
	 * 
	 * Pattern yearPattern = Pattern.compile(PATTERN_YEAR); Pattern
	 * monthContextPattern = Pattern.compile(PATTERN_MONTH_CONTEXT); Pattern
	 * dayPattern = Pattern.compile(PATTERN_DAY);
	 * 
	 * Matcher yearMatcher = yearPattern.matcher(content); Matcher dayMatcher =
	 * dayPattern.matcher(content);
	 * 
	 * int maxYear = 0; while (yearMatcher.find()) {
	 * 
	 * years.add(Integer.parseInt(yearMatcher.group())); maxYear =
	 * getMax(years); } if (maxYear > 0) { date.setYear(maxYear); } // check if
	 * there is a minus if (years.size() == 2) { // check what side of the minus
	 * is relevant if (hasExactlyOneMinus(content)) { monthContextPattern =
	 * getMonthContextPattern(content, monthContextPattern, maxYear); } }
	 * Matcher monthMatcher = monthContextPattern.matcher(content); while
	 * (monthMatcher.find()) { Pattern monthPattern =
	 * Pattern.compile(PATTERN_MONTH); Matcher monMatcher =
	 * monthPattern.matcher(monthMatcher.group());
	 * 
	 * if (monMatcher.find()) { months.add(monMatcher.group()); } } if
	 * (months.size() == 1) {
	 * date.setMonth(parseMonthFormString(months.get(0))); } while
	 * (dayMatcher.find()) { days.add(Integer.parseInt(dayMatcher.group())); }
	 * if (days.size() == 1) { date.setDay(days.get(0)); } }
	 */
	/**
	 * Gets the Pattern monthContextPattern used to determin the month with some
	 * context, depending on which side of the minus the highest year was found
	 * 
	 * @param content
	 *            the whole date string
	 * @param monthContextPattern
	 *            the context pattern
	 * @param maxYear
	 *            the higherst year found
	 * @return the Pattern for extracting the month with some context (contains
	 *         exactly one candidate for a month)
	 */
	/*
	 * private Pattern getMonthContextPattern(String content, Pattern
	 * monthContextPattern, int maxYear) { Matcher leftYearMatcher =
	 * Pattern.compile(PATTERN_YEAR + ".*-").matcher(content);
	 * 
	 * if (leftYearMatcher.find()) {
	 * 
	 * String leftYearContext = leftYearMatcher.group(); Matcher matcher =
	 * Pattern.compile(PATTERN_YEAR).matcher(leftYearContext);
	 * 
	 * if (matcher.find()) {
	 * 
	 * String leftYearStr = matcher.group(); int leftYear =
	 * Integer.parseInt(leftYearStr);
	 * 
	 * // get month from the left side of the minus if (leftYear == maxYear) {
	 * monthContextPattern = Pattern.compile(PATTERN_MONTH + ".*-"); // get
	 * month from the right side of the minus } else { monthContextPattern =
	 * Pattern.compile("-.*" + PATTERN_MONTH); } } } return monthContextPattern;
	 * }
	 */
	/**
	 * Checks if content contains exactly one minus
	 * 
	 * @param content
	 *            String to be explored
	 * @return true, if there is exactly one minus
	 */
	/*
	 * private boolean hasExactlyOneMinus(String content) {
	 * 
	 * Matcher minusMatcher = Pattern.compile("-").matcher(content);
	 * 
	 * int countMinus = 0; while (minusMatcher.find()) { countMinus++; } if
	 * (countMinus == 1) { return true; } return false; }
	 */
	/**
	 * Gets the maximum value of the ArrayList of Integers
	 * 
	 * @param values
	 *            The array to be explored
	 * @return Maximum of all Integer values as an int
	 */
	private int getMax(ArrayList<Integer> values) {

		int max = Integer.MIN_VALUE;

		for (int i = 0; i < values.size(); i++) {

			if (values.get(i) > max) {
				max = values.get(i);
			}
		}
		return max;
	}

	/**
	 * Creates a list with childs of an XML element given by its element name
	 * 
	 * @param doc
	 *            The XML document
	 * @param element
	 *            The unique element
	 * @return A NodeList with the childs of the element
	 */
	private NodeList getUniqueElementChilds(Document doc, String element) {

		NodeList list = null;
		Node node = doc.getElementsByTagName(element).item(0);
		if (node != null) {
			list = node.getChildNodes();
		}
		return list;
	}

	/**
	 * Gets the textcontent of an attribute of an element that is unique (the
	 * element has single apperance in doc)
	 * 
	 * @param doc
	 *            XML Document
	 * @param element
	 *            The element name that contains the unique attribute
	 * @param attribute
	 *            The unique attribute's name
	 * @return A String with the attribue content, or null if element does not
	 *         exist.
	 */
	private String getUniqueElementAttribute(Document doc, String element,
			String attribute) {
		String status = null;
		Node node = doc.getElementsByTagName(element).item(0);

		if (node != null) {
			NamedNodeMap namedNodeMap = node.getAttributes();
			Node statusNode = namedNodeMap.getNamedItem(attribute);
			status = statusNode.getTextContent();
		}
		return status;
	}

	/**
	 * Gets the text content of an XML element
	 * 
	 * @param doc
	 *            The XML document
	 * @param element
	 *            The element
	 * @return Text content of the XML element <code>element</code>
	 */
	private String getElementContent(Document doc, String element) {

		String text = null;
		NodeList nodeList = doc.getElementsByTagName(element);

		if (nodeList.item(0) != null) {
			text = nodeList.item(0).getTextContent();
		}
		return text;
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#hasNext()
	 */
	public boolean hasNext() throws IOException, CollectionException {
		// System.out.println(currentIndex +" "+ files.size());
		// return (currentIndex < files.size()) || (currentArchive <
		// archives.size());

		if (currentIndex < files.size() || currentArchive < archives.size())
			return true;
		else if (currentIndex == files.size()
				&& currentArchive < archives.size())
			return true;
		else {
			/*
			 * try { LiwaDatabase.endConnection();
			 * 
			 * } catch (Throwable e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			System.out.println(currentIndex + " " + currentArchive + " "
					+ archives.size());
			return false;
		}

	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(currentIndex, archives.size(),
				Progress.ENTITIES) };
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
	 */
	public void close() throws IOException {

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

	public static boolean existsURL(String URLName) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName)
					.openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
