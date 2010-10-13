package org.liwa.coherence.schedule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Selective {

	int size;

	List<Double> changes = new ArrayList<Double>();

	List<Integer> schedule = new ArrayList<Integer>();

	List<SchedulablePage> myPages = null;

	public Selective(List<Double> changes, List<Integer> schedule) {
		this.changes = changes;
		this.schedule = schedule;
	}

	public Selective(int size) {
		// for faster computation

		this.size = generateChanges1x2(this.changes, size);
		System.out.println(changes);
		generateTriangleIntervals(schedule, size);

	}

	public Selective(List<SchedulablePage> pages) {
		int i = 0;

		for (i = 0; i < pages.size(); i++) {
			SchedulablePage p = pages.get(i);
			changes.add(p.getChangeRate());
			schedule.add(2 * i);
		}
		myPages = pages;
	}

	/**
	 * e^x = 1 + x approximation of isHopeless
	 * 
	 * @param from
	 * @param to
	 * @param shortestInterval
	 * @return
	 */
	boolean isHopelessLinearApx(int from, long shortestInterval) {
		int i, j;

		double noDead = 0.0;
		for (i = from, j = 0; i < changes.size(); i++, j++) {
			noDead += changes.get(i) * (shortestInterval + 2 * j);
		}

		long longestInterval = shortestInterval + 2 * j - 2;

		double dead = 0.0;
		for (i = from, j = 0; i < changes.size() - 1; i++, j++) {
			dead += changes.get(i + 1) * (shortestInterval + 2 * j);
		}
		dead += changes.get(from) * longestInterval;

		System.out.println("isHopelessApx: " + noDead + " " + dead);
		if (noDead >= dead)
			return false;
		else
			return true;
	}

	/*
	 * Checking out if changes[from] is a hopeless page
	 */
	boolean isHopeless(int from, long shortestInterval, double delta) {
		int i, j;

		double noDead = 0.0;
		for (i = from, j = 0; i < changes.size(); i++, j++) {
			noDead += probSharp(myPages.get(i).getPriority(), changes.get(i),
					shortestInterval + 2 * j, delta);
		}

		long longestInterval = shortestInterval + 2 * j - 2;

		double dead = 0.0;
		for (i = from, j = 0; i < changes.size() - 1; i++, j++) {
			dead += probSharp(myPages.get(i+1).getPriority(),
					changes.get(i + 1), shortestInterval + 2 * j,
					delta);
		}
		dead += probSharp(myPages.get(from).getPriority(),
				changes.get(from), longestInterval, delta);

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
	public double xi(List<SchedulablePage> hopeful,
			List<SchedulablePage> hopeless, double delta) {
		boolean print = false;
		int shortestIndex, longestIndex;

		double expectedNNPages = 1.0; // [0,0] page is always sharp

		List<Integer> bi = new ArrayList<Integer>(); // bad guys
		List<Integer> gi = new ArrayList<Integer>(); // good guys

		if (myPages != null)
			hopeful.add(myPages.get(0));

		shortestIndex = 1;
		longestIndex = changes.size() - 1;
		for (int i = 1; i < changes.size(); i++) {
			if (isHopeless(i, 2 * shortestIndex, delta)) {
				// System.out.println(changes.get(i) + " is hopeless");
				// We've got a hopeless page
				expectedNNPages += probSharp(myPages.get(i).getPriority(),
						changes.get(i), 2 * longestIndex, delta);
				longestIndex--;
				if (myPages != null)
					hopeless.add(myPages.get(i));
				if (print == true) {
					System.out.println(changes.get(i) + " is hopeless");
					bi.add(i);
				}
			} else {
				// System.out.println(changes.get(i) + " is ok");
				expectedNNPages += probSharp(myPages.get(i).getPriority(),
						changes.get(i), 2 * shortestIndex, delta);
				shortestIndex++;
				if (myPages != null)
					hopeful.add(myPages.get(i));
				if (print == true) {
					System.out.println(changes.get(i) + " is hopeful");
					gi.add(i);
				}
			}
		}

		if (print == true) {
			System.out.println("hopeful indexes: " + gi);
			System.out.println("hopeless indexes: " + bi);
		}
		return expectedNNPages
				+ probSharp(myPages.get(myPages.size() - 1).getPriority(),
						changes.get(changes.size() - 1), 2 * shortestIndex,
						delta);
	}

	double probSharp(double changeRate, double length, double delta,
			double priority) {
		return priority * Math.exp(-changeRate * length * delta);
	}

	public static double expectedNNSharp(List<Double> changes,
			List<Integer> schedule) {
		double res = 0.0;

		for (int i = 0; i < changes.size(); i++) {
			res += Math.exp(-changes.get(i) * schedule.get(i));
		}

		return res;

	}

	// from http://www.freewebz.com/permute/01example.html
	public double bestPlanFromAllPermutations() {
		boolean print = false;
		List<Double> bestOrder = new ArrayList<Double>();
		for (int i = 0; i < size; i++) {
			bestOrder.add(changes.get(i));
		}

		double prevNNSharp = 0.0;
		double NNSharp = 0.0;
		for (int i = 0; i < changes.size(); i++) {
			prevNNSharp += Math.exp(-schedule.get(i) * changes.get(i));
		}
		double bestNNSharp = prevNNSharp;

		int p[] = new int[size + 1];
		int i, j; // Upper Index i; Lower Index j
		for (i = 0; i < size; i++) {
			p[i] = i;
		}
		p[size] = size; // p[N] > 0 controls iteration and the index boundary
		// for i
		i = 1; // setup first swap points to be 1 and 0 respectively (i & j)
		while (i < size) {
			p[i]--; // decrease index "weight" for i by one
			j = i % 2 * p[i]; // IF i is odd then j = p[i] otherwise j = 0

			int from = i;
			int to = j;
			NNSharp = prevNNSharp
					- Math.exp(-schedule.get(from) * changes.get(from))
					- Math.exp(-schedule.get(to) * changes.get(to))
					+ Math.exp(-schedule.get(to) * changes.get(from))
					+ Math.exp(-schedule.get(from) * changes.get(to));
			swapChangeRates(changes, from, to);
			prevNNSharp = NNSharp;

			if (print == true) {
				System.out.println(changes + " " + NNSharp + ", best: "
						+ bestNNSharp);
			}

			// keeping the better schedule
			if (NNSharp > bestNNSharp) {
				for (j = 0; j < schedule.size(); j++) {
					bestOrder.set(j, changes.get(j));
				}
				bestNNSharp = NNSharp;
			}

			i = 1; // reset index i to 1 (assumed)
			while (p[i] == 0) {
				p[i] = i; // reset p[i] zero value
				i++; // set new index value for i (increase by one)
			} // while(!p[i])
		} // while(i < N)

		System.out.println(bestOrder);
		System.out.println(expectedNNSharp(bestOrder, schedule));
		return bestNNSharp;
	}

	public static int generateChanges2BigRestTheSame(List<Double> changes,
			int size) {
		double bigChangeRate = 1.5;
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);

		double theRest = 0.01;
		for (int i = 0; i < size - 3; i++) {
			changes.add(theRest);
		}

		return changes.size();
	}

	public static int generateChangesSome(List<Double> changes, int size) {
		double bigChangeRate = 1.0;
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);

		double first = 0.5;
		double last = 0.1;
		double step = (first - last) / (size / 2);
		double changeRate = first;

		System.out.println(step);

		for (int i = 0; i < size / 2 - 2; i++) {
			changes.add(changeRate);
			changeRate -= step;
		}

		first = 0.5 / 10;
		last = 0.1 / 10;
		step = (first - last) / (size / 2);
		changeRate = first;
		for (int i = size / 2 - 2; i < size; i++) {
			changes.add(changeRate);
			changeRate -= step;
		}

		DecimalFormat d = new DecimalFormat("##.####");
		for (int i = 0; i < changes.size(); i++)
			System.out.print(d.format(changes.get(i)) + ' ');
		System.out.println();

		return changes.size();
	}

	public static int generateChanges2BigLinearDecreaseTheRest(
			List<Double> changes, int size) {
		double bigChangeRate = 1.0;
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);

		double first = 0.5;
		double last = 0.1;
		double step = (first - last) / (size - 3);
		double changeRate = first;

		for (int i = 0; i < size - 2; i++) {
			changes.add(changeRate);
			changeRate -= step;
		}

		return changes.size();
	}

	public static int generateChanges1(List<Double> changes, int size) {

		for (int i = 0; i < size; i++) {
			changes.add(0.1);
		}

		return changes.size();
	}

	public static int generateTheSameChanges(List<Double> changes,
			double changeRate, int size) {
		for (int i = 0; i < size; i++) {
			changes.add(changeRate);
		}

		return changes.size();
	}

	public static int generateChanges1x2(List<Double> changes, int size) {
		// double bigChangeRate = 0.7;
		changes.add(0.74);
		changes.add(0.73);
		changes.add(0.72);

		double first = 0.71;
		for (int i = 1; i < size; i++) {
			changes.add(1.0 * Math.round(first / i / i / i * 1000) / 1000);
		}

		return changes.size();
	}

	public static int generateTriangleIntervals(List<Integer> schedule, int size) {
		// initializing the schedule
		for (int i = 0; i < size; i++) {
			schedule.add(2 * i);
		}

		return schedule.size();
	}

	private void swapChangeRates(List<Double> schedule, int i, int j) {
		double temp = schedule.get(i);
		schedule.set(i, schedule.get(j));
		schedule.set(j, temp);
	}

	public void pushingDownwards() {
		boolean print = false;
		List<Double> bestOrder = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			bestOrder.add(changes.get(i));
		}

		// Computing the E##sharp
		double NNSharp = 0.0;
		for (int i = 0; i < changes.size(); i++) {
			NNSharp += Math.exp(-schedule.get(i) * changes.get(i));
		}
		double bestNNSharp = NNSharp;

		if (print == true) {
			DecimalFormat d = new DecimalFormat("##.####");
			for (int k = 0; k < changes.size(); k++)
				System.out.print(d.format(changes.get(k)) + ' ');
			System.out.println(" " + NNSharp + "("
					+ expectedNNSharp(changes, schedule) + ", best: "
					+ bestNNSharp);
		}

		// Changing things pair wise
		int from, to;

		for (int i = 1; i < changes.size() - 1; i++) {
			from = i;
			to = i + 1;

			NNSharp = NNSharp
					- Math.exp(-schedule.get(from) * changes.get(from))
					- Math.exp(-schedule.get(to) * changes.get(to))
					+ Math.exp(-schedule.get(to) * changes.get(from))
					+ Math.exp(-schedule.get(from) * changes.get(to));
			swapChangeRates(changes, from, to);

			if (print == true) {
				DecimalFormat d = new DecimalFormat("##.####");
				for (int k = 0; k < changes.size(); k++)
					System.out.print(d.format(changes.get(k)) + ' ');
				System.out.println(" " + NNSharp + ", best: " + bestNNSharp);
			}

			// keeping the better schedule
			if (NNSharp > bestNNSharp) {
				for (int j = 0; j < schedule.size(); j++) {
					bestOrder.set(j, changes.get(j));
				}
				bestNNSharp = NNSharp;
			}
		}

		System.out.print("best: ");
		DecimalFormat d = new DecimalFormat("##.####");
		for (int k = 0; k < bestOrder.size(); k++)
			System.out.print(d.format(bestOrder.get(k)) + ' ');
		System.out.println(" " + NNSharp + ", best: " + bestNNSharp);
	}
}
