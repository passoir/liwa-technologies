package org.liwa.coherence.schedule;

public interface DatasetProvider {
	Dataset getDataset();
	TimestampMapper getTimestampMapper();
}
