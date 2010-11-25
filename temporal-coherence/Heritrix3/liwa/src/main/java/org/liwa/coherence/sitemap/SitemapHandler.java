package org.liwa.coherence.sitemap;

import java.util.Date;

public interface SitemapHandler {
	int saveUrl(String url, String frequency, double priority,
			Date lastModified);
}
