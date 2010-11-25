package org.liwa.coherence.sitemap;

import org.liwa.coherence.schedule.ChangeRateProvider;
import org.liwa.coherence.schedule.Dataset;
import org.liwa.coherence.schedule.DatasetProvider;
import org.liwa.coherence.schedule.SchedulablePage;
import org.liwa.coherence.schedule.TimestampMapper;

public class SitemapDatasetAdapter implements DatasetProvider {

	private SitemapLoader sitemaps;

	private ChangeRateProvider changeRateProvider;

	private double politenessDelay;

	private Dataset d;

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
		if (d == null) {
			d = new Dataset();
			for (CompressedUrl url : sitemaps.getCompressedUrls()) {
				SchedulablePage page = new SchedulablePage();
				page.setChangeRate(changeRateProvider
						.provideChangeRate(url.getId()));
				page.setPublishedUrlId(url.getId());
				page.setPriority(url.getPriority());
				page.setChangeRate(url.getChangeRate());
				page.setDelta(getPolitenessDelay());
				d.addPage(page);

			}
		}
		return d;
	}

	public TimestampMapper getTimestampMapper() {
		return new TimestampMapper(
				((double) System.currentTimeMillis()) / 1000, sitemaps
						.getCompressedUrls().size(), politenessDelay);
	}
}
