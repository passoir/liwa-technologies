package org.liwa.coherence.db;

import java.io.IOException;
import java.util.Properties;

public class Queries {
	private static final String SEQ_START = "select.seq.start";

	private static final String SEQ_END = "select.seq.end";
	
	private static final String REVISIT_LIFO = "select.revisit.lifo";

	private static final String INSERT_CRAWL = "insert into t_crawls"
			+ " (crawl_id, title) values(?,?)";
	
	private static final String INSERT_RECRAWL = "insert into t_crawls"
		+ " (crawl_id, title, recrawled_id) values(?,?,?)";

	private static final String INSERT_PAGE = "insert into t_pages (page_id, crawl_id,"
			+ " url_id, url, site_id, etag, page_size, page_type, parent_page_id, visited_timestamp, "
			+ "content, checksum, last_modified, vs_page_id, status_code, download_time, priority) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";

	private static final String INSERT_COMPACT_PAGE = 
		"insert into t_pages (page_id, crawl_id,"
		+ " url_id, url, site_id, visited_timestamp, "
		+ " checksum, status_code,  priority, sig0,sig1,sig2,sig3,sig4,sig5,sig6," +
				"sig7,sig8,sig9) "
		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String GET_PAGE_ID_QUERY = "select page_id from t_pages "
			+ "where crawl_id = ? and url = ?";

	private static final String GET_PROTOTYPE_QUERY = "select page_id, etag, page_size, "
			+ "page_type, checksum, last_modified, url_id, site_id, vs_page_id "
			+ "from (select page_id, etag, page_size, "
			+ "page_type, checksum, last_modified, url_id, site_id, vs_page_id"
			+ " from t_pages where url = ?" + " order by page_id desc) t";

	private static final String INSERT_LINKS_QUERY = "insert into t_links "
			+ "(from_page_id, crawl_id, from_url_id, from_site_id, to_site_id,"
			+ "to_url_id, link_type) values(?,?,?,?,?,?,?)";

	private static final String GET_LINKS_QUERY = "select url, link_type "
			+ "from t_links l, t_urls u "
			+ "where from_page_id = ?  and l.to_url_id = u.url_id";

	private static final String URL_SELECT_QUERY = "select url_id from t_urls "
			+ "where url = ?";

	private static final String URL_INSERT_QUERY = "insert into t_urls "
			+ " (url_id, url) values(?, ?)";

	private static final String SITE_SELECT_QUERY = "select site_id from t_sites "
			+ "where site = ?";

	private static final String SITE_INSERT_QUERY = "insert into t_sites "
			+ " (site_id, site) values(?, ?)";

	private String queriesLocation;

	private Properties props = new Properties();

	public String getQueriesLocation() {
		return queriesLocation;
	}

	public void setQueriesLocation(String queriesLocation) {
		this.queriesLocation = queriesLocation;
		try {
			props.load(ClassLoader.getSystemResourceAsStream(queriesLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getRevisitsQuery() {
		return props.getProperty(REVISIT_LIFO);
	}

	
	public String getSequenceStart() {
		return props.getProperty(SEQ_START);
	}

	public String getSequenceEnd() {
		return props.getProperty(SEQ_END);
	}

	public String getInsertCrawlQuery() {
		return INSERT_CRAWL;
	}
	
	public String getInsertRecrawlQuery() {
		return INSERT_RECRAWL;
	}
	public String getInsertPageQuery() {
		return INSERT_PAGE;
	}
	
	public String getInsertCompactPageQuery() {
		return INSERT_COMPACT_PAGE;
	}

	public String getPageIdByUrlQuery() {
		// TODO Auto-generated method stub
		return GET_PAGE_ID_QUERY;
	}

	public String getPrototypeQuery() {
		return GET_PROTOTYPE_QUERY;
	}

	public String getLinksQuery() {
		return GET_LINKS_QUERY;
	}

	public String getInsertLinksQuery() {
		return INSERT_LINKS_QUERY;
	}

	public String getUrlQuery() {
		return URL_SELECT_QUERY;
	}

	public String getInsertUrlQuery() {
		return URL_INSERT_QUERY;
	}
	
	public String getSiteQueries(){
		return SITE_SELECT_QUERY;
	}
	
	public String getInsertSiteQueries(){
		return SITE_INSERT_QUERY;
	}
}
