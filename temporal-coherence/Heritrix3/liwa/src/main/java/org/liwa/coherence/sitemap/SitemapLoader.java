package org.liwa.coherence.sitemap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
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

public class SitemapLoader extends DefaultHandler implements InitializingBean {

	private List<String> sitemapUrls = null;

	private boolean sitemapsInitialized = false;

	/**
	 * Text from which to extract seeds
	 */
	protected ReadSource textSource = null;

	private List<CompressedUrl> compressedUrls = new ArrayList<CompressedUrl>();

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

	public void setCompressedUrls(List<CompressedUrl> urls) {
		compressedUrls.addAll(urls);
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

	public List<CompressedUrl> getCompressedUrls() {
		return compressedUrls;
	}

	private static class GeneralSitemapHandler extends DefaultHandler {
		String host;

		SitemapHandler callback;

		UrlHandler urlHandler;

		SitemapSetHandler sitemapSetHandler;

		DefaultHandler defaultHandler;

		List<String> sitemaps;

		List<CompressedUrl> compressedUrls;

		public GeneralSitemapHandler(SitemapHandler callback,
				List<CompressedUrl> compressedUrls) {
			this.callback = callback;
			urlHandler = new UrlHandler(callback);
			sitemapSetHandler = new SitemapSetHandler();
			this.compressedUrls = compressedUrls;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			defaultHandler.characters(ch, start, length);
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
					urlHandler.host = host;
					urlHandler.compressedUrls = compressedUrls;
				} else {
					defaultHandler = sitemapSetHandler;
					sitemapSetHandler.host = host;
				}
			}
			defaultHandler.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endDocument() throws SAXException {
			if (defaultHandler == sitemapSetHandler) {
				sitemaps = sitemapSetHandler.sitemaps;
			}
		}
	}

	private static class SitemapSetHandler extends DefaultHandler {

		List<String> sitemaps = new ArrayList<String>();

		static final int NONE_STATE = 1;

		static final int LOC_STATE = 3;

		static final int LASTMOD_STATE = 4;

		int localState = NONE_STATE;

		String host;

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
				if (getDomain(location).equalsIgnoreCase(host)) {
					sitemaps.add(location);
				}
				location = "";
			}
			super.endElement(uri, localName, qName);
		}
	}

	private static class UrlHandler extends DefaultHandler {

		List<CompressedUrl> compressedUrls = null;

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

		String host;

		SitemapHandler callback;

		public UrlHandler(SitemapHandler callback) {
			this.callback = callback;
		}

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
				CompressedUrl url = new CompressedUrl();

				if (changeFreq.trim().length() > 0) {
					// System.out.println(changeFreq);
					url.setChangeRate(SitemapChangeRateProvider.CHANGE_RATE_MAP
							.get(changeFreq.trim()));

				} else {
					url.setChangeRate(SitemapChangeRateProvider.YEARLY);
				}
				// if (priority.trim().length() > 0) {
				// url.setPriority(priority);
				// }
				double dPriority = -1;
				if (priority != null && priority.length() > 0) {
					dPriority = Double.parseDouble(priority);
				}
				url.setPriority(dPriority);
				if (changeFreq.trim().length() > 0
						&& getDomain(location).equalsIgnoreCase(host)) {
					int id = callback.saveUrl(location, changeFreq, dPriority,
							null);
					if (id > 0) {
						compressedUrls.add(url);
						url.setId(id);
					}
				}

				location = "";
				changeFreq = "";
				priority = "";
				lastMod = "";
			}
			super.endElement(uri, localName, qName);
		}
	}

	public static List<CompressedUrl> loadCompressedUrls(
			List<String> sitemapUrls, SitemapHandler callback) {
		List<CompressedUrl> urls = new ArrayList<CompressedUrl>();
		for (String url : sitemapUrls) {
			loadSitemap(url, callback, urls);
		}
		return urls;
	}

	private static void loadCompressedUrls(List<String> sitemapUrls,
			SitemapHandler callback, List<CompressedUrl> urls) {
		for (String url : sitemapUrls) {
			loadSitemap(url, callback, urls);
		}
	}

	private static void loadSitemap(String sitemapUrl, SitemapHandler callback,
			List<CompressedUrl> urls) {
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
			GeneralSitemapHandler handler = new GeneralSitemapHandler(callback,
					urls);
			handler.host = getDomain(sitemapUrl);
			saxParser.parse(inputSource, handler);
			if (handler.sitemaps != null) {
				loadCompressedUrls(handler.sitemaps, callback, urls);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private static String getDomain(String url) {
		try {
			URL urlObject = new URL(url);
			return urlObject.getHost();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
