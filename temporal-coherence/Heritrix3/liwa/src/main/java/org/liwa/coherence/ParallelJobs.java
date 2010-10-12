package org.liwa.coherence;

import java.util.ArrayList;
import java.util.List;

import org.archive.crawler.framework.CrawlController;
import org.liwa.coherence.processors.ProcessorListener;

public class ParallelJobs implements ProcessorListener {
	private static final int JOB_COUNT = 4;

	private List<CrawlController> controllers = new ArrayList<CrawlController>();

	private List<Boolean> ready = new ArrayList<Boolean>();

	private boolean started = false;

	private int calls = 0;

	private boolean finished = false;
	private int jobFinished = 0;
	
	public boolean isFilled(){
		return controllers.size() == JOB_COUNT;
	}

	public void addCrawlController(CrawlController cc) {
		controllers.add(cc);
		ready.add(false);
	}

	public void jobFinished(CrawlController cc) {
		finished = true;
		jobFinished++;
		synchronized (this) {
			notifyAll();
		}
	}
	
	public boolean areJobsDone(){
		return jobFinished == JOB_COUNT;
	}

	public boolean hasController(CrawlController cc) {
		for (CrawlController c : controllers) {
			if (c.equals(cc)) {
				return true;
			}
		}
		return false;
	}

	public void urlProcessed() {
		if (!finished) {
			synchronized (this) {
				if (calls == JOB_COUNT - 1) {
					calls = 0;
					this.notifyAll();
				} else {
					calls++;
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else{
			synchronized(this){
				notifyAll();
			}
		}
	}

	public void jobPaused(CrawlController cc) {
		if (!started) {
			for (int i = 0; i < controllers.size(); i++) {
				if (controllers.get(i).equals(cc)) {
					ready.set(i, true);
				}
			}
			if (isFilled()) {
				boolean allReady = true;
				for (int i = 0; i < ready.size() && allReady; i++) {
					allReady &= ready.get(i);
				}
				if (allReady) {
					for (CrawlController c : controllers) {
						c.requestCrawlResume();
					}
				}
			}
		}
	}

}
