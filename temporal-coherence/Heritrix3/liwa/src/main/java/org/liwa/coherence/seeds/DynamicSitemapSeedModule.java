package org.liwa.coherence.seeds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.archive.modules.CrawlURI;
import org.archive.modules.seeds.SeedModule;
import org.liwa.coherence.sitemap.PublishedUrl;
import org.liwa.coherence.sitemap.Sitemap;
import org.liwa.coherence.sitemap.SitemapLoader;

public class DynamicSitemapSeedModule extends SeedModule {

	private String sitemapUrl;

	public String getSitemapUrl() {
		return sitemapUrl;
	}

	public void setSitemapUrl(String sitemapUrl) {
		this.sitemapUrl = sitemapUrl;
	}

	@Override
	public void actOn(File f) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSeed(CrawlURI curi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void announceSeeds() {
		List<Sitemap> sitemaps = SitemapLoader.loadSitemap(sitemapUrl);
		List<String> seeds = new ArrayList<String>();
		for (Sitemap s : sitemaps) {
			List<PublishedUrl> urls = s.getUrlList();
			for (PublishedUrl url : urls) {
				System.out.println(url);
				seeds.add(url.getLocation());
			}
		}

	}

}
