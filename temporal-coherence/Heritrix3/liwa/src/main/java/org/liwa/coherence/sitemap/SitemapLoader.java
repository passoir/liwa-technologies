package org.liwa.coherence.sitemap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.archive.io.ReadSource;
import org.archive.util.iterator.LineReadingIterator;
import org.archive.util.iterator.RegexLineIterator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.syndication.io.impl.DateParser;

public class SitemapLoader extends DefaultHandler implements InitializingBean {

	private List<Sitemap> sitemapList = new ArrayList<Sitemap>();

	private List<String> sitemapUrls = null;

	private boolean sitemapsInitialized = false;

	/**
	 * Text from which to extract seeds
	 */
	protected ReadSource textSource = null;

	public ReadSource getTextSource() {
		return textSource;
	}

	private void initializeSitemapUrls() {
		if (sitemapUrls == null) {
			sitemapUrls = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(textSource
					.obtainReader());
			try {
				initSitemapsFromReader(reader);
				sitemapsInitialized = true;
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	
	public void setSitemaps(List<Sitemap> sitemaps){
		this.sitemapList = new ArrayList<Sitemap>();
		this.sitemapList.addAll(sitemaps);
		this.sitemapsInitialized = true;
	}

	public List<String> getSitemapUrls() {
		if (!sitemapsInitialized) {
			initializeSitemapUrls();
		}
		return sitemapUrls;
	}

	@Required
	public void setTextSource(ReadSource seedsSource) {
		this.textSource = seedsSource;
	}

	public void initSitemaps() {
		if (!sitemapsInitialized) {
			initializeSitemapUrls();
			for (String sitemap : sitemapUrls) {
				sitemapList.addAll(SitemapLoader.loadSitemap(sitemap));
			}
			sitemapsInitialized = true;
		}
	}

	private void initSitemapsFromReader(BufferedReader reader) {
		String s;
		Iterator<String> iter = new RegexLineIterator(new LineReadingIterator(
				reader), RegexLineIterator.COMMENT_LINE,
				RegexLineIterator.NONWHITESPACE_ENTRY_TRAILING_COMMENT,
				RegexLineIterator.ENTRY);

		while (iter.hasNext()) {
			s = (String) iter.next();
			if (Character.isLetterOrDigit(s.charAt(0))) {
				// consider a likely URI
				sitemapLine(s);
			} else {
				// report just in case it's a useful directive
				nonSitemapLine(s);
			}
		}

	}

	private void nonSitemapLine(String s) {
		// TODO Auto-generated method stub

	}

	private void sitemapLine(String s) {
		sitemapUrls.add(s);
	}

	public List<Sitemap> getSitemaps() {
		if (!sitemapsInitialized) {
			initSitemaps();
		}
		return sitemapList;
	}

	public List<PublishedUrl> getPublishedUrls() {
		List<PublishedUrl> publishedUrls = new ArrayList<PublishedUrl>();
		if (!sitemapsInitialized) {
			initSitemaps();
		}
		for (Sitemap s : sitemapList) {
			publishedUrls.addAll(s.getUrlList());
		}
		return publishedUrls;
	}

	private static class SitemapHandler extends DefaultHandler {
		UrlHandler urlHandler = new UrlHandler();

		SitemapSetHandler sitemapSetHandler = new SitemapSetHandler();

		DefaultHandler defaultHandler;

		List<PublishedUrl> publishedUrls;

		List<String> sitemaps;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			defaultHandler.characters(ch, start, length);
		}

		@Override
		public void endDocument() throws SAXException {
			if (defaultHandler == urlHandler) {
				publishedUrls = urlHandler.publshedUrls;
			} else if (defaultHandler == sitemapSetHandler) {
				sitemaps = sitemapSetHandler.sitemaps;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			defaultHandler.endElement(uri, localName, qName);
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (defaultHandler == null) {
				if (qName == "urlset") {
					defaultHandler = urlHandler;
				} else {
					defaultHandler = sitemapSetHandler;
				}
			}
			defaultHandler.startElement(uri, localName, qName, attributes);
		}
	}

	private static class SitemapSetHandler extends DefaultHandler {

		List<String> sitemaps = new ArrayList<String>();

		static final int NONE_STATE = 1;

		static final int LOC_STATE = 3;

		static final int LASTMOD_STATE = 4;

		int localState = NONE_STATE;

		String location = "";

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("loc")) {
				localState = LOC_STATE;
			} else {
				localState = NONE_STATE;
			}

			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			char[] chars = new char[length];
			System.arraycopy(ch, start, chars, 0, length);
			String s = new String(chars);
			if (s.trim().length() > 0) {
				switch (localState) {
				case LOC_STATE:
					location += s;
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			localState = NONE_STATE;
			if (qName.equals("sitemap")) {
				sitemaps.add(location);
				location = "";
			}
			super.endElement(uri, localName, qName);
		}
	}

	private static class UrlHandler extends DefaultHandler {

		List<PublishedUrl> publshedUrls = new ArrayList<PublishedUrl>();

		static final int NONE_STATE = 1;

		static final int PRIORITY_STATE = 2;

		static final int LOC_STATE = 3;

		static final int LASTMOD_STATE = 4;

		static final int CHANGEFREQ_STATE = 5;

		int localState = NONE_STATE;

		String location = "";

		String changeFreq = "";

		String lastMod = "";

		String priority = "";

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("priority")) {
				localState = PRIORITY_STATE;
			} else if (qName.equals("loc")) {
				localState = LOC_STATE;
			} else if (qName.equals("lastmod")) {
				localState = LASTMOD_STATE;
			} else if (qName.equals("changefreq")) {
				localState = CHANGEFREQ_STATE;
			} else if (qName.equals("priority")) {
				localState = PRIORITY_STATE;
			} else {
				localState = NONE_STATE;
			}

			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			char[] chars = new char[length];
			System.arraycopy(ch, start, chars, 0, length);
			String s = new String(chars);
			if (s.trim().length() > 0) {
				switch (localState) {
				case LOC_STATE:
					location += s.trim();
					break;
				case CHANGEFREQ_STATE:
					changeFreq += s.trim();
					break;
				case LASTMOD_STATE:
					lastMod += s.trim();
					break;
				case PRIORITY_STATE:
					priority += s.trim();
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			localState = NONE_STATE;
			if (qName.equals("url")) {
				PublishedUrl url = new PublishedUrl(location, null);
				if (changeFreq.trim().length() > 0) {
					url.setChangeRate(changeFreq);
				} else {
					url.setChangeRate(ChangeRate.NEVER);
				}
				// if (priority.trim().length() > 0) {
				// url.setPriority(priority);
				// }
				if (lastMod != null && lastMod.length() > 0) {
					url.setLastModified(DateParser.parseW3CDateTime(lastMod));
				}
				// System.out.println(url);
				publshedUrls.add(url);

				location = "";
				changeFreq = "";
				priority = "";
				lastMod = "";
			}
			super.endElement(uri, localName, qName);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadSitemap("http://spiderbites.nytimes.com/sitemaps/www.nytimes.com/sections_00000.xml.gz");
	}

	public static List<Sitemap> loadSitemaps(List<String> sitemapUrls) {
		List<Sitemap> sitemaps = new ArrayList<Sitemap>();
		for (String url : sitemapUrls) {
			sitemaps.addAll(loadSitemap(url));
		}
		return sitemaps;
	}

	public static List<Sitemap> loadSitemap(String sitemapUrl) {
		List<Sitemap> array = new ArrayList<Sitemap>();

		try {
			URL url = new URL(sitemapUrl);

			InputStream urlStream = url.openStream();
			InputSource inputSource = null;
			if (sitemapUrl.endsWith(".gz")) {
				urlStream = new GZIPInputStream(urlStream);
			}
			inputSource = new InputSource(urlStream);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			SitemapHandler handler = new SitemapHandler();
			saxParser.parse(inputSource, handler);
			if (handler.publishedUrls != null) {
				Sitemap s = new Sitemap(sitemapUrl, null);
				s.setUrlList(handler.publishedUrls);
				array.add(s);
			} else if (handler.sitemaps != null) {
				array.addAll(loadSitemaps(handler.sitemaps));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}

	public static String xmlToString(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void afterPropertiesSet() throws Exception {
		// initSitemaps();
	}
}
