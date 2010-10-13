package org.liwa.coherence.sitemap;

import java.sql.Timestamp;

import org.liwa.coherence.schedule.ChangeRateProvider;
import org.liwa.coherence.schedule.Dataset;
import org.liwa.coherence.schedule.DatasetProvider;
import org.liwa.coherence.schedule.SchedulablePage;
import org.liwa.coherence.schedule.TimestampMapper;

public class SitemapDatasetAdapter implements DatasetProvider {

	private SitemapLoader sitemaps;

	private ChangeRateProvider changeRateProvider;

	private double politenessDelay;

	public double getPolitenessDelay() {
		return politenessDelay;
	}

	public void setPolitenessDelay(double politenessDelay) {
		this.politenessDelay = politenessDelay;
	}

	public ChangeRateProvider getChangeRateProvider() {
		return changeRateProvider;
	}

	public void setChangeRateProvider(ChangeRateProvider changeRateProvider) {
		this.changeRateProvider = changeRateProvider;
	}

	public SitemapLoader getSitemaps() {
		return sitemaps;
	}

	public void setSitemaps(SitemapLoader sitemaps) {
		this.sitemaps = sitemaps;
	}

	public Dataset getDataset() {
		Dataset d = new Dataset();
		for (PublishedUrl url : sitemaps.getPublishedUrls()) {
			SchedulablePage page = new SchedulablePage();
			page.setChangeRate(changeRateProvider.getChangeRate(url
					.getLocation()));
			page.setUrl(url.getLocation());
			page.setPriority(url.getPriority());
			d.addPage(page);

		}
		return d;
	}

	public TimestampMapper getTimestampMapper() {
		return new TimestampMapper(
				((double) System.currentTimeMillis()) / 1000, sitemaps
						.getPublishedUrls().size(), politenessDelay);
	}
}
