package org.liwa.coherence.schedule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Selective {

	int size;

	List<Double> changes = new ArrayList<Double>();

	List<Double> weights = new ArrayList<Double>();

	List<SchedulablePage> myPages = null;

	protected Selective() {
		// TODO Auto-generated constructor stub
	}

	public Selective(List<Double> changes, List<Double> weights) {
		this.changes = changes;
		this.weights = weights;
	}

	public Selective(List<SchedulablePage> pages) {
		int i = 0;

		for (i = 0; i < pages.size(); i++) {
			SchedulablePage p = pages.get(i);
			changes.add(p.getChangeRate());
			weights.add(p.getPriority());
		}
		myPages = pages;
	}

	/*
	 * Checking out if changes[from] is a hopeless page
	 */
	boolean isHopeless(int from, long shortestInterval, double delta) {
		int i, j;

		double noDead = 0.0;
		for (i = from, j = 0; i < changes.size(); i++, j++) {
			noDead += probSharp(weights.get(i), changes.get(i),
					shortestInterval + 2 * j, delta);
		}

		long longestInterval = shortestInterval + 2 * j - 2;

		double dead = 0.0;
		for (i = from, j = 0; i < changes.size() - 1; i++, j++) {
			dead += probSharp(weights.get(i + 1), changes.get(i + 1),
					shortestInterval + 2 * j, delta);
		}
		dead += probSharp(weights.get(from), changes.get(from),
				longestInterval, delta);

		// System.out.println("ishopeless: " + noDead + " " + dead);
		if (noDead - dead >= 0)
			return false;
		else
			return true;
	}

	/**
	 * 
	 * @param d
	 * @param Hopeful
	 *            should be empty; method puts hopeful pages into the list
	 * @param Hopeless
	 *            should be empty; method puts hopeless pages into the list
	 * @return
	 */
	public double selective(List<SchedulablePage> hopeful,
			List<SchedulablePage> hopeless, double delta) {
		boolean print = false;
		int shortestIndex, longestIndex;

		double expectedNNPages = weights.get(0); // [0,0] page is always
		// sharp

		List<Integer> bi = new ArrayList<Integer>(); // bad guys
		List<Integer> gi = new ArrayList<Integer>(); // good guys

		if (myPages != null)
			hopeful.add(myPages.get(0));
		
		gi.add(0);

		shortestIndex = 1;
		longestIndex = changes.size() - 1;
		for (int i = 1; i < changes.size(); i++) {
			if (isHopeless(i, 2 * shortestIndex, delta)) {
				// System.out.println(changes.get(i) + " is hopeless");
				// We've got a hopeless page
				expectedNNPages += probSharp(weights.get(i), changes.get(i),
						2 * longestIndex, delta);
				longestIndex--;
				bi.add(i);
				if (myPages != null)
					hopeless.add(myPages.get(i));
				if (print == true) {
					System.out.println(changes.get(i) + " is hopeless");
					
				}
			} else {
				// System.out.println(changes.get(i) + " is ok");
				expectedNNPages += probSharp(weights.get(i), changes.get(i),
						2 * shortestIndex, delta);
				shortestIndex++;
				gi.add(i);
				if (myPages != null)
					hopeful.add(myPages.get(i));
				if (print == true) {
					System.out.println(changes.get(i) + " is hopeful");
				}
			}
		}

		List<Double> c = new ArrayList<Double>();
		List<Double> w = new ArrayList<Double>();
		for(int i = 0; i < gi.size(); i++){
			c.add(changes.get(gi.get(i)));
			w.add(weights.get(gi.get(i)));
		}
		for(int i = bi.size()-1; i >= 0; i--){
			c.add(changes.get(bi.get(i)));
			w.add(weights.get(bi.get(i)));
		}
		changes = c;
		weights = w;
		if (print == true) {
			System.out.println("hopeful indexes: " + gi);
			System.out.println("hopeless indexes: " + bi);
		}
		return expectedNNPages;
	}

	double probSharp(double priority, double changeRate, double length,
			double delta) {
		return priority * Math.exp(-changeRate * length * delta);
	}

	public static double expectedNNSharp(List<Double> changes,
			List<Double> weights, List<Integer> schedule) {
		double res = 0.0;

		for (int i = 0; i < changes.size(); i++) {
			res += weights.get(i) * Math.exp(-changes.get(i) * schedule.get(i));
		}

		return res;

	}

	private void swapChangeRates(List<Double> schedule, List<Double> weights,
			int i, int j) {
		double temp = schedule.get(i);
		schedule.set(i, schedule.get(j));
		schedule.set(j, temp);

		temp = weights.get(i);
		weights.set(i, weights.get(j));
		weights.set(j, temp);
	}

	public double exhaustive() {
		boolean print = false;
		double delta = 1;
		size = changes.size();
		double NNSharp = 0.0;
		double bestNNSharp = NNSharp;
		for (int j = 0; j < changes.size(); j++) {
			// Computing the E##sharp
			boolean swap = false;
			NNSharp = 0.0;
			for (int i = 0; i < changes.size(); i++) {
				NNSharp += probSharp(weights.get(i), changes.get(i), 2 * i,
						delta);
			}
			bestNNSharp = NNSharp;

			if (print == true) {
				DecimalFormat d = new DecimalFormat("##.####");
				for (int k = 0; k < changes.size(); k++)
					System.out.print(d.format(changes.get(k)) + ' ');
				System.out.println(" " + NNSharp + ", best: " + bestNNSharp);
			}

			// Changing things pair wise
			int from, to;

			for (int i = 1; i < changes.size() - 1; i++) {
				from = i;
				to = i + 1;

				NNSharp = NNSharp
						- probSharp(weights.get(from), changes.get(from),
								2 * from, delta)
						- probSharp(weights.get(to), changes.get(to), 2 * to,
								delta)
						+ probSharp(weights.get(from), changes.get(from),
								2 * to, delta)
						+ probSharp(weights.get(to), changes.get(to), 2 * from,
								delta);
				swapChangeRates(changes, weights, from, to);

				if (print == true) {
					DecimalFormat d = new DecimalFormat("##.##############");
					for (int k = 0; k < changes.size(); k++)
						System.out.print(d.format(changes.get(k)) + ' ');

				}
				// System.out.println("current: " + NNSharp + ", best: " +
				// bestNNSharp);

				// keeping the better schedule
				if (NNSharp > bestNNSharp) {
					bestNNSharp = NNSharp;
					swap = true;
				} else {
					swapChangeRates(changes, weights, from, to);
					break;
				}
			}
			if(swap){
				j--;
			}
		}
		// System.out.print("current: ");
		// DecimalFormat d = new DecimalFormat("##.##############");
		// for (int k = 0; k < bestOrder.size(); k++)
		// System.out.print(d.format(bestOrder.get(k)) + ' ');
		// System.out.println(" " + NNSharp + ", best: " + bestNNSharp);
		return bestNNSharp;
	}
	
	public List<Double> getChangeRates(){
		return changes;
	}
	
	public List<Double> getWeights(){
		return weights;
	}
}
