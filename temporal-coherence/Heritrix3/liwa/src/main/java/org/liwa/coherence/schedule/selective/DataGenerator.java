package org.liwa.coherence.schedule.selective;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataGenerator {
	public static int generateWeightsTheSame(List<Double> weights, int size) {
		double w = 1.0;

		for (int i = 0; i < size; i++) {
			weights.add(w);
		}

		return weights.size();
	}

	public static int generateExtreme(List<Double> weights, int size) {
		double high = 0.9;
		double low = 0.1;

		for (int i = 0; i < size / 2; i++) {
			weights.add(high);
		}

		for (int i = size / 2; i < size && weights.size() < size; i++) {
			weights.add(low);
		}

		return weights.size();
	}

	public static int generateWeights2BigLinearDecreaseTheRest(
			List<Double> weights, int size) {
		double bigWeightRate = 1.0;
		weights.add(bigWeightRate);
		weights.add(bigWeightRate);

		double first = 0.5;
		double last = 0.1;
		double step = (first - last) / (size - 3);
		double weightRate = first;

		for (int i = 0; i < size - 2; i++) {
			weights.add(weightRate);
			weightRate -= step;
		}

		return weights.size();
	}

	public static int generateChanges2BigRestTheSame(List<Double> changes,
			int size) {
		double bigChangeRate = 0.9;
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);

		double theRest = 0.01;
		for (int i = 0; i < size - 3; i++) {
			changes.add(theRest);
		}

		return changes.size();
	}

	public static void generateFromDbExamples(List<Double> changes,
			List<Double> weights, List<Integer> schedule) {
		for (int i = 0; i < 21; i++) {
			changes.add(0.000277777778);
			if (i == 0) {
				weights.add(0.8);
			} else {
				weights.add(0.7);
			}
		}
		for (int i = 0; i < 1; i++) {
			changes.add(1.15740741e-005);
			weights.add(0.7);
		}

		for (int i = 0; i < 4; i++) {
			changes.add(1.65343915e-006);
			if (i == 0) {
				weights.add(0.7);
			} else {
				weights.add(0.6);
			}
		}

		for (int i = 0; i < 7; i++) {
			changes.add(3.85802469e-007);
			if (i < 6) {
				weights.add(0.7);
			} else {
				weights.add(0.6);
			}
		}

		for (int i = 0; i < 12; i++) {
			changes.add(3.2511444e-008);
			if (i < 8) {
				weights.add(0.7);
			} else {
				weights.add(0.6);
			}
		}
		for (int i = 0; i < changes.size(); i++) {
			schedule.add(2 * i);
		}
		return;
	}

	public static int generateChangesSome(List<Double> changes, int size) {
		double bigChangeRate = 1.0;
		changes.add(bigChangeRate);
		changes.add(bigChangeRate);

		double first = 0.5;
		double last = 0.1;
		double step = (first - last) / (size / 2);
		double changeRate = first;

		for (int i = 0; i < size / 2 - 2 && changes.size() < size; i++) {
			changes.add(changeRate);
			changeRate -= step;
		}

		first = 0.5 / 10;
		last = 0.1 / 10;
		step = (first - last) / (size / 2);
		changeRate = first;
		for (int i = size / 2 - 2; i < size && changes.size() < size; i++) {
			changes.add(changeRate);
			changeRate -= step;
		}

		DecimalFormat d = new DecimalFormat("##.####");
		// for (int i = 0; i < changes.size(); i++)
		// System.out.print(d.format(changes.get(i)) + ' ');
		// System.out.println();

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
		for (int i = 3; i < size; i++) {
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

	public static List<String> getWeightLabels() {
		List<String> labels = new ArrayList<String>();
		labels.add("2BigRestSame");
		labels.add("2BigRestLinearDecrease");
		labels.add("1x2");
		labels.add("Some");
		labels.add("Extreme");
		labels.add("2BigRestSameReverse");
		labels.add("2BigRestLinearDecreaseReverse");
		labels.add("1x2Reverse");
		labels.add("SomeReverse");
		labels.add("ExtremeReverse");
		return labels;
	}

	public static List<String> getChangeRateLabels() {
		List<String> labels = new ArrayList<String>();
		// labels.add("2BigRestSame");
		// labels.add("2BigRestLinearDecrease");
		// labels.add("1x2");
		// labels.add("Some");
		labels.add("Extreme");
		return labels;
	}

	public static List<List<Double>> getWeights(int size) {
		List<List<Double>> weightLists = new ArrayList<List<Double>>();
		List<Double> list2Big = new ArrayList<Double>();
		generateChanges2BigRestTheSame(list2Big, size);
		weightLists.add(list2Big);
		List<Double> list2BigLinear = new ArrayList<Double>();
		generateChanges2BigLinearDecreaseTheRest(list2BigLinear, size);
		weightLists.add(list2BigLinear);
		List<Double> list1x2 = new ArrayList<Double>();
		generateChanges1x2(list1x2, size);
		weightLists.add(list1x2);
		List<Double> some = new ArrayList<Double>();
		generateChangesSome(some, size);
		weightLists.add(some);
		List<Double> extreme = new ArrayList<Double>();
		generateExtreme(extreme, size);
		weightLists.add(extreme);
		List<Double> list2BigReverse = new ArrayList<Double>();
		generateChanges2BigRestTheSame(list2BigReverse, size);
		Collections.reverse(list2BigReverse);
		weightLists.add(list2BigReverse);
		List<Double> list2BigLinearReverse = new ArrayList<Double>();
		generateChanges2BigLinearDecreaseTheRest(list2BigLinearReverse, size);
		Collections.reverse(list2BigLinearReverse);
		weightLists.add(list2BigLinearReverse);
		List<Double> list1x2Reverse = new ArrayList<Double>();
		generateChanges1x2(list1x2Reverse, size);
		Collections.reverse(list1x2Reverse);
		weightLists.add(list1x2Reverse);
		List<Double> someReverse = new ArrayList<Double>();
		generateChangesSome(someReverse, size);
		Collections.reverse(someReverse);
		weightLists.add(someReverse);
		List<Double> extremeReverse = new ArrayList<Double>();
		generateExtreme(extremeReverse, size);
		Collections.reverse(extremeReverse);
		weightLists.add(extremeReverse);
		return weightLists;
	}

	public static List<List<Double>> getChangeRates(int size) {
		List<List<Double>> changeRatesLists = new ArrayList<List<Double>>();
		// List<Double> list2Big = new ArrayList<Double>();
		// generateChanges2BigRestTheSame(list2Big, size);
		// changeRatesLists.add(list2Big);
		// List<Double> list2BigLinear = new ArrayList<Double>();
		// generateChanges2BigLinearDecreaseTheRest(list2BigLinear, size);
		// changeRatesLists.add(list2BigLinear);
		// List<Double> list1x2 = new ArrayList<Double>();
		// generateChanges1x2(list1x2, size);
		// changeRatesLists.add(list1x2);
		// List<Double> some = new ArrayList<Double>();
		// generateChangesSome(some, size);
		// changeRatesLists.add(some);
		List<Double> extreme = new ArrayList<Double>();
		generateExtreme(extreme, size);
		changeRatesLists.add(extreme);
		return changeRatesLists;
	}

	public static List<Double> generateLinear(int size) {
		List<Double> list = new ArrayList<Double>();
		double start = 0.9;
		double step = 0.1;
		for (int i = 0; i < size; i++) {
			list.add(start);
			if (start - step < step) {
				step /= 10;
			}
			start -= step;
		}
		return list;
	}

}
