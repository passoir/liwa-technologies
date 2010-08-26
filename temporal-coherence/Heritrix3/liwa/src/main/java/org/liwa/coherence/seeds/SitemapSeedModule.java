package org.liwa.coherence.seeds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archive.modules.CrawlURI;
import org.archive.modules.seeds.TextSeedModule;
import org.liwa.coherence.sitemap.PublishedUrl;
import org.liwa.coherence.sitemap.Sitemap;
import org.liwa.coherence.sitemap.SitemapLoader;

public class SitemapSeedModule extends TextSeedModule {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6778012905234226685L;

	@Override
	protected void seedLine(String uri) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		System.out.println("loading sitemaps");
		List<Sitemap> sitemaps = SitemapLoader.loadSitemap(uri);
		
		for(Sitemap s: sitemaps){
			List<PublishedUrl> urls = s.getUrlList();
			for(PublishedUrl url: urls){
				System.out.println(url);
				super.seedLine(url.getLocation());
				Integer integer = map.get(url.getChangeRate());
				if(integer==null){
					map.put(url.getChangeRate(), 1);
				}else{
					map.put(url.getChangeRate(), integer+1);
				}
			}
		}
		System.out.println(map);
	}
	
	@Override
	public void addSeed(CrawlURI curi) {
		// TODO Auto-generated method stub

	}

}
