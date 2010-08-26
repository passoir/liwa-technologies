package org.liwa.coherence.pojo;

public class Link {
	private long fromPageId;
	private long crawlId;
	private long fromUrlId;
	private long toUrlId;
	private long fromSiteId;
	private long toSiteId;
	private String type;
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getFromPageId() {
		return fromPageId;
	}
	public void setFromPageId(long fromPageId) {
		this.fromPageId = fromPageId;
	}
	public long getCrawlId() {
		return crawlId;
	}
	public void setCrawlId(long crawlId) {
		this.crawlId = crawlId;
	}
	public long getFromUrlId() {
		return fromUrlId;
	}
	public void setFromUrlId(long fromUrlId) {
		this.fromUrlId = fromUrlId;
	}
	public long getToUrlId() {
		return toUrlId;
	}
	public void setToUrlId(long toUrlId) {
		this.toUrlId = toUrlId;
	}
	public long getFromSiteId() {
		return fromSiteId;
	}
	public void setFromSiteId(long fromSiteId) {
		this.fromSiteId = fromSiteId;
	}
	public long getToSiteId() {
		return toSiteId;
	}
	public void setToSiteId(long toSiteId) {
		this.toSiteId = toSiteId;
	}

}
