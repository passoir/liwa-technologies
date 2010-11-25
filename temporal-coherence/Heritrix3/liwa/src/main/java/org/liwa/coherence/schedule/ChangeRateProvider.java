package org.liwa.coherence.schedule;

import org.liwa.coherence.sitemap.SitemapLoader;

public interface ChangeRateProvider {
	double provideChangeRate(int urlId);

	void setSitemaps(SitemapLoader sitemaps);

	void afterSitemapSet();
}
