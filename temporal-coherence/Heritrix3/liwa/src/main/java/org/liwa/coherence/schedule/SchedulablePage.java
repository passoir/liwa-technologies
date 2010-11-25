package org.liwa.coherence.schedule;

import java.util.Comparator;

public class SchedulablePage {
	private int publishedUrlId;

	private double changeRate;

	private double priority;

	private String frequency;

	private int visit;

	private int revisit;

	private double delta;

	public SchedulablePage() {
		// TODO Auto-generated constructor stub
	}

	public SchedulablePage(SchedulablePage p) {
		publishedUrlId = p.publishedUrlId;
		changeRate = p.changeRate;
		priority = p.priority;
		frequency = p.frequency;
		visit = p.visit;
		revisit = p.revisit;
		delta = p.delta;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public int getRevisit() {
		return revisit;
	}

	public void setRevisit(int revisit) {
		this.revisit = revisit;
	}

	public int getVisit() {
		return visit;
	}

	public void setVisit(int visit) {
		this.visit = visit;
	}

	public double getExpectedCoherence() {
		double expCoherence = Math.exp(-changeRate * (revisit + visit) * delta);
		System.out.println(publishedUrlId + " " + visit + " " + revisit + " " + delta
				+ " " + expCoherence);
		return expCoherence;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public double getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(double changeRate) {
		this.changeRate = changeRate;
	}

	public int getPublishedUrlId() {
		return publishedUrlId;
	}

	public void setPublishedUrlId(int publishedUrlId) {
		this.publishedUrlId = publishedUrlId;
	}

	public long getIntLength(double tau) {
		return getIntLength(changeRate, tau);
	}

	public static long getIntLength(double lambda, double tau) {
		return (long) (1.0 / lambda * Math.log(1 / tau));
	}

	public static double getChangeRateFromIntLength(long length, double tau) {
		return Math.log(1 / tau) / length;
	}

	public static class ChangeRateReverseComparator implements
			Comparator<SchedulablePage> {
		public int compare(SchedulablePage o1, SchedulablePage o2) {
			int d = (int)Math.signum(-o1.changeRate + o2.changeRate);
			if(d == 0){
				return (int)Math.signum(-o1.priority + o2.priority);
			}
			return d;
		}
	}

	public static class ChangeRatePriorityReverseComparator implements
			Comparator<SchedulablePage> {
		public int compare(SchedulablePage o1, SchedulablePage o2) {
			return (int) Math.signum(-o1.priority
					* Math.pow(Math.E, o1.changeRate) + o2.priority
					* Math.pow(Math.E, o2.changeRate));
		}
	}

	public static class PriorityReverseComparator implements
			Comparator<SchedulablePage> {
		public int compare(SchedulablePage o1, SchedulablePage o2) {
			int d = (int) Math.signum(-o1.priority + o2.priority);
			if(d == 0){
				return (int) Math.signum(-o1.changeRate + o2.changeRate);
			}
			return d;
		}
	}

	public static final ChangeRateReverseComparator CHANGE_RATE_COMPARATOR = new ChangeRateReverseComparator();

	public static final PriorityReverseComparator PRIORITY_COMPARATOR = new PriorityReverseComparator();

	public static final ChangeRatePriorityReverseComparator CHANGE_RATE_PRIORITY_COMPARATOR = new ChangeRatePriorityReverseComparator();

}
