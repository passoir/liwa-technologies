package org.liwa.coherence.schedule;

import org.liwa.coherence.sitemap.SitemapLoader;

public interface ChangeRateProvider {
	double getChangeRate(String url);

	void setSitemaps(SitemapLoader sitemaps);

	void afterSitemapSet();
}
