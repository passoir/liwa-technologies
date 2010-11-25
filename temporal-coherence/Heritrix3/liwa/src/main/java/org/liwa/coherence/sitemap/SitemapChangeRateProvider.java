package org.liwa.coherence.sitemap;

import java.util.HashMap;
import java.util.Map;

import org.liwa.coherence.schedule.ChangeRateProvider;
import org.springframework.beans.factory.InitializingBean;

public class SitemapChangeRateProvider implements ChangeRateProvider,
		InitializingBean {

	public static final double ALWAYS = 0.0166666667;

	public static final double HOURLY = 0.000277777778;

	public static final double DAILY = 1.15740741E-5;

	public static final double WEEKLY = 1.65343915E-6;

	public static final double MONTHLY = 3.85802469E-7;

	public static final double YEARLY = 3.2511444E-8;

	public static final Map<String, Double> CHANGE_RATE_MAP = new HashMap<String, Double>();
	static {
		CHANGE_RATE_MAP.put(ChangeRate.ALWAYS, ALWAYS);
		CHANGE_RATE_MAP.put(ChangeRate.HOURLY, HOURLY);
		CHANGE_RATE_MAP.put(ChangeRate.DAILY, DAILY);
		CHANGE_RATE_MAP.put(ChangeRate.WEEKLY, WEEKLY);
		CHANGE_RATE_MAP.put(ChangeRate.MONTHLY, MONTHLY);
		CHANGE_RATE_MAP.put(ChangeRate.YEARLY, YEARLY);
		CHANGE_RATE_MAP.put(ChangeRate.NEVER, Double.MIN_NORMAL);
	}

	private SitemapLoader sitemaps;

	private Map<Integer, Double> urlChangeRateMap = new HashMap<Integer, Double>();

	public SitemapLoader getSitemaps() {
		return sitemaps;
	}

	public void setSitemaps(SitemapLoader sitemaps) {
		this.sitemaps = sitemaps;
	}

	public double provideChangeRate(int id) {
		Double changeRate = urlChangeRateMap.get(id);
		// System.out.println(changeRateKey);
		if (changeRate == null) {
			changeRate = YEARLY;
		}
		return changeRate;
	}
	
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void afterSitemapSet(){
		for (CompressedUrl url : sitemaps.getCompressedUrls()) {
				urlChangeRateMap.put(url.getId(), url.getChangeRate());
		}
		//System.out.println(map);
		// System.out.println(urlChangeRateMap);
	}
	
	
}
