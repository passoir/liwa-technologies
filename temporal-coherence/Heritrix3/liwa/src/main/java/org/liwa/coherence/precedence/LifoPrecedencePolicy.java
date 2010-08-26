package org.liwa.coherence.precedence;

import org.archive.crawler.frontier.precedence.BaseUriPrecedencePolicy;
import org.archive.modules.CrawlURI;

public class LifoPrecedencePolicy extends BaseUriPrecedencePolicy {

	private int maxPrecedence = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4469484993651586368L;
	
	private int currentPrecedence = Integer.MAX_VALUE;

	@Override
	protected int calculatePrecedence(CrawlURI curi) {
		// TODO Auto-generated method stub
		currentPrecedence--;
		return currentPrecedence;
	}

	public int getMaxPrecedence() {
		return maxPrecedence;
	}

	public void setMaxPrecedence(int maxPrecedence) {
		this.maxPrecedence = maxPrecedence;
		currentPrecedence = maxPrecedence;
	}
	
	

	
}
