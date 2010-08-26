package org.liwa.coherence.pojo;

import java.io.IOException;
import java.sql.Timestamp;

import org.archive.io.ReplayInputStream;

public class Page {

	private long id;
	private long crawlId;
	private long urlId;
	private String url;
	private long siteId;
	private String etag;
	private int size;
	private String type;
	private long parentPageId;
	private Timestamp visitedTimestamp;
	private ReplayInputStream content;
	private String checksum;
	private Timestamp lastModified;
	private long vsPageId;
	private int statusCode;
	private long downloadTime;

	public long getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(long downloadTime) {
		this.downloadTime = downloadTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCrawlId() {
		return crawlId;
	}

	public void setCrawlId(long crawlId) {
		this.crawlId = crawlId;
	}

	public long getUrlId() {
		return urlId;
	}

	public void setUrlId(long urlId) {
		this.urlId = urlId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getParentPageId() {
		return parentPageId;
	}

	public void setParentPageId(long parentPageId) {
		this.parentPageId = parentPageId;
	}

	public Timestamp getVisitedTimestamp() {
		return visitedTimestamp;
	}

	public void setVisitedTimestamp(Timestamp visitedTimestamp) {
		this.visitedTimestamp = visitedTimestamp;
	}

	public ReplayInputStream getContent() {
		return content;
	}

	public void setContent(ReplayInputStream content) {
		this.content = content;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public Timestamp getLastModified() {
		return lastModified;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public long getVsPageId() {
		return vsPageId;
	}

	public void setVsPageId(long vsPageId) {
		this.vsPageId = vsPageId;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeLong(id);
		out.writeObject(etag);
		out.writeInt(size);
		out.writeObject(type);
		out.writeObject(checksum);
		out.writeObject(lastModified);
		out.writeLong(urlId);
		out.writeLong(siteId);
		out.writeObject(url);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		id = in.readLong();
		etag = (String)in.readObject();
		size = in.readInt();
		type = (String)in.readObject();
		checksum = (String)in.readObject();
		lastModified = (Timestamp)in.readObject();
		urlId = in.readLong();
		siteId = in.readLong();
		url = (String)in.readObject();
	}

}
