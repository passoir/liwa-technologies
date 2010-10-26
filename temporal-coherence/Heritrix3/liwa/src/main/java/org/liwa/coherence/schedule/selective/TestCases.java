package org.liwa.coherence.schedule.selective;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.liwa.coherence.schedule.SchedulablePage;
import org.liwa.coherence.schedule.Selective;

public class TestCases {
	private static void testExhaustive(){
		int size = 10000;
		List<Integer> schedule = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			schedule.add(2 * i);
		}
		List<List<Double>> changes = DataGenerator.getChangeRates(size);
		List<List<Double>> weights = DataGenerator.getWeights(size);
		
		List<String> changeLabels = DataGenerator.getChangeRateLabels();
		List<String> weightLabels = DataGenerator.getWeightLabels();
		
		for(int i = 0; i < changes.size(); i++){
			testChanges(changes.get(i), weights, schedule,
					changeLabels.get(i), weightLabels);
		}
		
	}
	
	private static void testChanges(List<Double> changes, List<List<Double>>
		weights, List<Integer> schedule, 
		String changeLabel, List<String> weightLabels){
		for(int i = 0; i < weights.size(); i++){
			List<Double> c = new ArrayList<Double>();
			for(int j = 0; j < changes.size(); j++){
				c.add(changes.get(i));
			}
			System.out.println("-----------------------------------");
			System.out.println(changeLabel + " : " + weightLabels.get(i));
			testOrder(changes, weights.get(i));
		}
	}

	private static void testOrder(List<Double> changes, List<Double> weights) {
		DecimalFormat dc = new DecimalFormat("##.##########");
//
//		System.out.println("Selective-exhaustive: ");
//
//		System.out.println("l    : "
//				+ dc.format(testOrderCubic(changes, weights, 
//						new ChangeRateComparator())));
//		System.out.println("w    : "
//				+ dc.format(testOrderCubic(changes, weights, 
//						new WeightComparator())));
//		System.out.println("w+l  : "
//				+ dc.format(testOrderCubic(changes, weights, 
//						new WeightChangeRateSumComparator())));
//		System.out.println("w*l  : "
//				+ dc.format(testOrderCubic(changes, weights, 
//						new WeightChangeRateProductComparator())));
//		System.out.println("w*e^l: "
//				+ dc.format(testOrderCubic(changes, weights, 
//						new WeightChangeRateExponentComparator())));

		System.out.println("Selective-fast: ");

		System.out.println("l    : "
				+ dc.format(testOrderSelective(changes, weights, 
						new ChangeRateComparator())));
		System.out.println("w    : "
				+ dc.format(testOrderSelective(changes, weights, 
						new WeightComparator())));
		System.out.println("w+l  : "
				+ dc.format(testOrderSelective(changes, weights, 
						new WeightChangeRateSumComparator())));
		System.out.println("w*l  : "
				+ dc.format(testOrderSelective(changes, weights, 
						new WeightChangeRateProductComparator())));
		System.out.println("w*e^l: "
				+ dc.format(testOrderSelective(changes, weights, 
						new WeightChangeRateExponentComparator())));
	}

	private static double testOrderCubic(List<Double> changes,
			List<Double> weights,
			Comparator<WeightedChangeRate> c) {
		resort(changes, weights, c);
		Selective s = new Selective(changes, weights);
		return s.exhaustive();
	}

	private static double testOrderSelective(List<Double> changes,
			List<Double> weights,
			Comparator<WeightedChangeRate> c) {
		resort(changes, weights, c);
		Selective s = new Selective(changes, weights);
//		System.out.println(s.getChangeRates());
		double d = s.selective(new ArrayList<SchedulablePage>(),
				new ArrayList<SchedulablePage>(), 1.0);
		// double t = 0;
		// for (int i = 0; i < changes.size(); i++) {
		// System.out.println(changes.get(i) + " " + weights.get(i)+
		// " " + (weights.get(i)*Math.exp(-changes.get(i)*2*i*0.4)));
		// t += (weights.get(i)*Math.exp(-changes.get(i)*2*i*0.4));
		// }
		// System.out.println(t);
//		System.out.println(s.getChangeRates());
		System.out.println(s.getChangeRates());
		return d;
	}
	
	
	private static void testExcel(){
		int size = 4;
		//List<Double> weights = DataGenerator.generateLinear(size);
		List<Double> changeRates = DataGenerator.generateLinear(size);
		List<Double> weights = getInterestingWeights();
//		testOrder(changeRates, weights);
//		Collections.reverse(weights);
//		testOrder(changeRates, weights);
//		testOrder(changeRates, getInterestingWeights());
//		testOrder(changeRates, getMoreInterestingWeights());
		for(int i = 0; i < 15; i++){
			Collections.shuffle(weights);
			System.out.println(weights);
			testOrder(changeRates, weights);
		}
 	}
	
	private static void testDbExample() {
		List<Double> changes = new ArrayList<Double>();
		List<Double> weights = new ArrayList<Double>();
		List<Integer> schedule = new ArrayList<Integer>();
		DataGenerator.generateFromDbExamples(changes, weights, schedule);
		// overwriteWithBig(changes);
		testOrder(changes, weights);
	}

	private static class ChangeRateComparator implements
			Comparator<WeightedChangeRate> {
		public int compare(WeightedChangeRate o1, WeightedChangeRate o2) {
			int i = (int) Math.signum(-o1.changeRate + o2.changeRate);
			if (i == 0) {
				return (int) Math.signum(-o1.weight + o2.weight);
			}
			return i;
		}
	}

	private static class WeightComparator implements
			Comparator<WeightedChangeRate> {
		public int compare(WeightedChangeRate o1, WeightedChangeRate o2) {
			int i = (int) Math.signum(-o1.weight + o2.weight);
			if (i == 0) {
				return (int) Math.signum(-o1.changeRate + o2.changeRate);
			}
			return i;
		}
	}

	private static class WeightChangeRateSumComparator implements
			Comparator<WeightedChangeRate> {
		public int compare(WeightedChangeRate o1, WeightedChangeRate o2) {
			return (int) Math.signum(-(o1.changeRate + o1.weight)
					+ (o2.changeRate + o2.weight));
		}
	}

	private static class WeightChangeRateProductComparator implements
			Comparator<WeightedChangeRate> {
		public int compare(WeightedChangeRate o1, WeightedChangeRate o2) {
			return (int) Math.signum(-(o1.changeRate * o1.weight)
					+ (o2.changeRate * o2.weight));
		}
	}

	private static class WeightChangeRateExponentComparator implements
			Comparator<WeightedChangeRate> {
		public int compare(WeightedChangeRate o1, WeightedChangeRate o2) {
			return (int) Math.signum(-(o1.weight * Math.exp(o1.changeRate))
					+ (o2.weight * Math.exp(o2.changeRate)));
		}
	}
	
	private static class WeightedChangeRate implements Comparable {
		double changeRate;

		double weight;

		public WeightedChangeRate(double c, double w) {
			changeRate = c;
			weight = w;
		}

		public int compareTo(Object o) {
			if (o instanceof WeightedChangeRate) {
				WeightedChangeRate c = (WeightedChangeRate) o;
				return (int) Math.signum(-changeRate * weight
						* Math.exp(-changeRate) + c.changeRate * c.weight
						* Math.exp(-c.changeRate));
				// return (int)Math.signum(-changeRate
				// +c.changeRate);
				// return (int)Math.signum(-weight
				// +c.weight);
				// return (int)Math.signum(-weight*changeRate
				// +c.weight*c.changeRate);

			}
			return 0;
		}

	}

	private static void resort(final List<Double> changes,
			final List<Double> weights,
			Comparator<WeightedChangeRate> comparator) {
		List<WeightedChangeRate> list = new ArrayList<WeightedChangeRate>();
		for (int i = 0; i < changes.size(); i++) {
			list.add(new WeightedChangeRate(changes.get(i), weights.get(i)));
		}
		Collections.sort(list, comparator);
		for (int i = 0; i < changes.size(); i++) {
			changes.set(i, list.get(i).changeRate);
			weights.set(i, list.get(i).weight);
		}
	}

	public static void main(String[] args) {
		testExcel();
	}


	private static List<Double> getInterestingWeights(){
		List<Double> list = new ArrayList<Double>();
		list.add(0.7);
		list.add(0.9);
		list.add(0.6);
		list.add(0.8);
		return list;
	}
	
	private static List<Double> getMoreInterestingWeights(){
		List<Double> list = new ArrayList<Double>();
		list.add(0.6);
		list.add(0.7);
		list.add(0.8);
		list.add(0.9);
		return list;
	}
}
