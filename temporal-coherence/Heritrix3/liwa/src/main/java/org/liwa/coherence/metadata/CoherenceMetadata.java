package org.liwa.coherence.metadata;

import org.archive.modules.CrawlMetadata;

public class CoherenceMetadata extends CrawlMetadata {

	private static final long serialVersionUID = -1766315315701360776L;

	private boolean recrawling=false;
	private long crawlId;
	private long recrawledId;
	
	
	
    public long getRecrawledId() {
		return recrawledId;
	}
	public void setRecrawledId(long recrawledId) {
		this.recrawledId = recrawledId;
	}
	public void setRecrawling(boolean recrawling) {
        this.recrawling = recrawling;
    }
    public boolean isRecrawling() {
        return recrawling;
    }
    
    public long getCrawlId() {
		return crawlId;
	}
    
    public void setCrawlId(long crawlId) {
		this.crawlId = crawlId;
	}
}
