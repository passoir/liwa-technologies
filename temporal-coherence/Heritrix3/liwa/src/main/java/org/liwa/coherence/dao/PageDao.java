package org.liwa.coherence.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.archive.modules.CrawlURI;
import org.archive.modules.extractor.Hop;
import org.archive.modules.extractor.Link;
import org.archive.modules.extractor.LinkContext;
import org.archive.net.UURI;
import org.liwa.coherence.db.ConnectionPool;
import org.liwa.coherence.db.Queries;
import org.liwa.coherence.pojo.Page;

public class PageDao {

	private static final String PAGES_SEQ = "pages_seq";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss");

	private ConnectionPool connectionPool;

	private Queries queries;

	private LinkDao linkDao;

	private SiteDao siteDao;

	private UrlDao urlDao;

	public Queries getQueries() {
		return queries;
	}

	public void setQueries(Queries queries) {
		this.queries = queries;
	}

	public SiteDao getSiteDao() {
		return siteDao;
	}

	public void setSiteDao(SiteDao siteDao) {
		this.siteDao = siteDao;
	}

	public UrlDao getUrlDao() {
		return urlDao;
	}

	public void setUrlDao(UrlDao urlDao) {
		this.urlDao = urlDao;
	}

	public LinkDao getLinkDao() {
		return linkDao;
	}

	public void setLinkDao(LinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	public void insertPage(long crawlId, CrawlURI uri) throws SQLException {
		Page prototype = getPagePrototype(uri.getUURI().toString());
		fillPagePrototype(crawlId, uri, prototype);
		doInsertPage(prototype);
		if (prototype.getStatusCode() != HttpStatus.SC_NOT_MODIFIED) {
			insertLinks(prototype, uri.getOutLinks());
		} else {
			if (uri.getOutLinks().size() == 0) {
				this.addOutLinks(uri, prototype);
			}
		}

	}

	public List<String> getRevisitPages(long crawlId) throws SQLException {
		Connection c = connectionPool.getConnection();
		List<String> pages = new ArrayList<String>();
		try {
			PreparedStatement ps = c.prepareStatement(queries
					.getRevisitsQuery());
			ps.setLong(1, crawlId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				pages.add(rs.getString(1));
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;

		}
		return pages;
	}

	private void addOutLinks(CrawlURI uri, Page prototype) throws SQLException {

		Connection c = connectionPool.getConnection();
		try {
			if (prototype.getVsPageId() > 0) {
				ResultSet rs = linkDao.getLinks(c, prototype.getVsPageId());
				while (rs.next()) {
					String url = rs.getString(1);
					String linkType = rs.getString(2);
					Hop h = null;
					LinkContext lc = null;
					if (linkType.equals("NAVLINK")) {
						h = Hop.NAVLINK;
						lc = LinkContext.NAVLINK_MISC;
					} else if (linkType.equals("EMBED")) {
						h = Hop.EMBED;
						lc = LinkContext.EMBED_MISC;
					} else if (linkType.equals("PREREQ")) {
						h = Hop.PREREQ;
						lc = LinkContext.PREREQ_MISC;
					} else if (linkType.equals("SPECULATIVE")) {
						h = Hop.SPECULATIVE;
						lc = LinkContext.SPECULATIVE_MISC;
					} else if (linkType.equals("REFER")) {
						h = Hop.REFER;
						lc = LinkContext.SPECULATIVE_MISC;
					}
					try {
						Link.add(uri, Integer.MAX_VALUE, url, lc, h);
					} catch (URIException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				rs.close();
			}
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
		}

	}

	private void insertLinks(Page page, Collection<Link> links)
			throws SQLException {
		List<org.liwa.coherence.pojo.Link> linkPojos = new ArrayList<org.liwa.coherence.pojo.Link>();
		for (Link l : links) {
			org.liwa.coherence.pojo.Link pojo = new org.liwa.coherence.pojo.Link();
			pojo.setCrawlId(page.getCrawlId());
			pojo.setFromPageId(page.getId());
			pojo.setFromSiteId(page.getSiteId());
			pojo.setFromUrlId(page.getUrlId());
			pojo.setType(l.getHopType().name());
			String destination = l.getDestination().toString();
			pojo.setToSiteId(this.siteDao.getSiteIdForUrl(destination));
			pojo.setToUrlId(this.urlDao.getUrlId(destination));
			linkPojos.add(pojo);
		}
		this.linkDao.insertLinks(linkPojos);

	}

	private long doInsertPage(Page page) throws SQLException {

		long id = this.getNextValue(PAGES_SEQ);
		Connection c = connectionPool.getConnection();
		try {
			page.setId(id);
			PreparedStatement ps = c.prepareStatement(queries
					.getInsertPageQuery());
			ps.setLong(1, page.getId());
			ps.setLong(2, page.getCrawlId());
			ps.setLong(3, page.getUrlId());
			ps.setString(4, page.getUrl());
			ps.setLong(5, page.getSiteId());
			ps.setString(6, page.getEtag());
			ps.setInt(7, page.getSize());
			ps.setString(8, page.getType());
			if (page.getParentPageId() > 0) {
				ps.setLong(9, page.getParentPageId());
			} else {
				ps.setNull(9, Types.NUMERIC);
			}
			ps.setTimestamp(10, page.getVisitedTimestamp());
			try {
				ps.setBinaryStream(11, page.getContent());
			} catch (Throwable t) {
				// catch not implemented methods, degrade gracefully
				System.out.println(t);
				int size = 0;
				if (page.getContent() != null) {
					size = (int) page.getContent().getSize();
				}
				ps.setBinaryStream(11, page.getContent(), size);
			}

			ps.setString(12, page.getChecksum());
			ps.setTimestamp(13, page.getLastModified());

			if (page.getVsPageId() > 0) {
				ps.setLong(14, page.getVsPageId());
			} else {
				ps.setLong(14, page.getId());
			}

			ps.setInt(15, page.getStatusCode());

			if (page.getVsPageId() > 0) {
				ps.setLong(16, page.getDownloadTime());
			} else {
				ps.setNull(16, Types.NUMERIC);
			}
			ps.executeUpdate();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;

		}
		return id;

	}

	private void fillPagePrototype(long crawlId, CrawlURI uri, Page prototype)
			throws SQLException {
		if (prototype.getVsPageId() == -1) {
			this.fillUknownPage(crawlId, uri, prototype);
		} else {
			this.fillKnownPage(crawlId, uri, prototype);
		}
	}

	private void fillKnownPage(long crawlId, CrawlURI uri, Page page)
			throws SQLException {
		page.setCrawlId(crawlId);
		page.setUrl(uri.getUURI().toString());
		UURI parent = uri.getVia();
		if (parent != null) {
			page.setParentPageId(getPageIdByUrl(crawlId, parent.toString()));
		} else {
			page.setParentPageId(-1);
		}
		page.setVisitedTimestamp(new Timestamp(System.currentTimeMillis()));
		if (uri.getFetchCompletedTime() > uri.getFetchBeginTime()) {
			page.setDownloadTime(uri.getFetchCompletedTime()
					- uri.getFetchBeginTime());
		} else if (uri.getFetchCompletedTime() == uri.getFetchBeginTime()) {
			page.setDownloadTime(1);
		} else {
			page.setDownloadTime(-1);
		}

		HttpMethod method = uri.getHttpMethod();
		if (method != null) {
			page.setStatusCode(method.getStatusCode());
		}

		if (method != null
				&& method.getStatusCode() != HttpStatus.SC_NOT_MODIFIED) {
			if (!uri.getContentDigestSchemeString().equals(page.getChecksum())) {
				page.setStatusCode(method.getStatusCode());
				try {
					if (method.getResponseHeader("Last-Modified") != null) {
						page
								.setLastModified(new Timestamp(DATE_FORMAT
										.parse(
												method.getResponseHeader(
														"Last-Modified")
														.getValue().substring(
																0, 25))
										.getTime()));
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				page.setEtag(this.getHeaderValue(method, "ETag"));
				page.setChecksum(uri.getContentDigestSchemeString());
				page.setSize((int) uri.getContentSize());
				page.setType(uri.getContentType());
				page.setVsPageId(-1);
				try {
					page.setContent(uri.getRecorder().getReplayInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (method == null) {
				page.setChecksum(uri.getContentDigestSchemeString());
				page.setSize((int) uri.getContentSize());
				page.setType(uri.getContentType());
				page.setVsPageId(-1);
				try {
					page.setContent(uri.getRecorder().getReplayInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void fillUknownPage(long crawlId, CrawlURI uri, Page page)
			throws SQLException {
		page.setCrawlId(crawlId);
		page.setUrl(uri.getUURI().toString());
		page.setUrlId(this.urlDao.getUrlId(uri.getUURI().toString()));
		page.setSiteId(this.siteDao.getSiteIdForUrl(uri.getUURI().toString()));
		page.setSize((int) uri.getContentSize());
		page.setType(uri.getContentType());
		page.setChecksum(uri.getContentDigestSchemeString());
		if (uri.getFetchCompletedTime() > uri.getFetchBeginTime()) {
			page.setDownloadTime(uri.getFetchCompletedTime()
					- uri.getFetchBeginTime());
		} else if (uri.getFetchCompletedTime() == uri.getFetchBeginTime()) {
			page.setDownloadTime(1);
		} else {
			page.setDownloadTime(-1);
		}
		try {
			page.setContent(uri.getRecorder().getReplayInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UURI parent = uri.getVia();
		if (parent != null) {
			page.setParentPageId(getPageIdByUrl(crawlId, parent.toString()));
		} else {
			page.setParentPageId(-1);
		}
		page.setVisitedTimestamp(new Timestamp(System.currentTimeMillis()));
		page.setVsPageId(-1);
		HttpMethod method = uri.getHttpMethod();
		if (method != null) {
			page.setStatusCode(method.getStatusCode());
			try {
				if (method.getResponseHeader("Last-Modified") != null) {
					page.setLastModified(new Timestamp(DATE_FORMAT.parse(
							method.getResponseHeader("Last-Modified")
									.getValue().substring(0, 25)).getTime()));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			page.setEtag(this.getHeaderValue(method, "ETag"));
		}
	}

	private String getHeaderValue(HttpMethod method, String header) {
		String value = null;
		if (method != null) {
			if (method.getResponseHeader(header) != null) {
				value = method.getResponseHeader(header).getValue();
			}
		}
		return value;
	}

	private Page getPagePrototype(String url) throws SQLException {
		Page prototype = null;

		Connection c = connectionPool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(queries
					.getPrototypeQuery());
			ps.setString(1, url);
			ResultSet rs = ps.executeQuery();
			prototype = this.createPrototype(rs);
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;

		}
		return prototype;
	}

	private Page createPrototype(ResultSet rs) throws SQLException {
		Page prototype = new Page();
		prototype.setId(-1);
		prototype.setVsPageId(-1);
		if (rs.next()) {
			prototype.setVsPageId(rs.getLong(9));
			prototype.setEtag(rs.getString(2));
			prototype.setSize(rs.getInt(3));
			prototype.setType(rs.getString(4));
			prototype.setChecksum(rs.getString(5));
			prototype.setLastModified(rs.getTimestamp(6));
			prototype.setUrlId(rs.getLong(7));
			prototype.setSiteId(rs.getLong(8));
		}
		return prototype;
	}

	private long getPageIdByUrl(long crawlId, String url) throws SQLException {
		Connection c = connectionPool.getConnection();
		long id=0;
		try {
			PreparedStatement ps = c
					.prepareStatement(queries.getPageIdByUrlQuery());
			id = -1;
			ps.setLong(1, crawlId);
			ps.setString(2, url);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
			
		}
		return id;
	}

	private long getNextValue(String sequence) throws SQLException {
		Connection c = connectionPool.getConnection();
		long next = 0;
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(queries.getSequenceStart() + " "
					+ sequence + " " + queries.getSequenceEnd());
			next = -1;
			if (r.next()) {
				next = r.getLong(1);
			}
			r.close();
			s.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.close();
			throw e;
			
		}
		return next;

	}
}
