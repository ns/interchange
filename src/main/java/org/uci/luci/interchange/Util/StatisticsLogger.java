package org.uci.luci.interchange.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class StatisticsLogger {
	private static FileWriter writer;
	private static Hashtable<String, FileWriter> logs;
	private static boolean loggingEnabled = true;

	public static void log(String log, String str) {
		if (!loggingEnabled)
			return;

		if (logs == null) {
			logs = new Hashtable<String, FileWriter>();
		}

		try {
			if (logs.get(log) == null) {
				logs.put(log, new FileWriter("C:\\Users\\Nitin\\Desktop\\log\\"
						+ log + ".csv"));
			}

			logs.get(log).append(str);
			logs.get(log).append('\n');
			logs.get(log).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Hashtable<String, ArrayList<VehicleSample>> sampleData = new Hashtable<String, ArrayList<VehicleSample>>();

	public static class VehicleSample {
		int vin;
		double vehicleGaugeTime;
		double vehicleTotalTraveledDistance;
		double vehicleTotalWaitTime;

		public VehicleSample(int vin, double vehicleGaugeTime,
				double vehicleTotalTraveledDistance, double vehicleTotalWaitTime) {
			this.vin = vin;
			this.vehicleGaugeTime = vehicleGaugeTime;
			this.vehicleTotalTraveledDistance = vehicleTotalTraveledDistance;
			this.vehicleTotalWaitTime = vehicleTotalWaitTime;
		}
	}

	public static class ConfidenceInterval {
		public double mean;
		public int samples;
		public double lowEndpoint;
		public double highEndpoint;
		public double confidence;
		public double lowDelta;
		public double highDelta;

		public ConfidenceInterval(double mean, int samples, double lowDelta,
				double highDelta, double lowEndpoint, double highEndpoint,
				double confidence) {
			this.mean = mean;
			this.samples = samples;
			this.lowDelta = lowDelta;
			this.highDelta = highDelta;
			this.lowEndpoint = lowEndpoint;
			this.highEndpoint = highEndpoint;
			this.confidence = confidence;
		}

		public double range() {
			return highEndpoint - lowEndpoint;
		}
	}

	public static void addSample(String category, VehicleSample vs) {
		if (!sampleData.containsKey(category))
			sampleData.put(category, new ArrayList<VehicleSample>());
		sampleData.get(category).add(vs);
	}

	private static double calculateStandardDeviation(List<VehicleSample> samples) {
		double mean = calculateMean(samples);
		double total = 0;
		for (VehicleSample vs : samples) {
			total += Math.pow(
					(vs.vehicleTotalWaitTime / vs.vehicleTotalTraveledDistance)
							- mean, 2);
		}
		return Math.sqrt(total / samples.size());
	}

	private static double calculateMean(List<VehicleSample> samples) {
		double total = 0;
		for (VehicleSample vs : samples) {
			total += (vs.vehicleTotalWaitTime / vs.vehicleTotalTraveledDistance);
		}
		return total / samples.size();
	}

	public static ConfidenceInterval calculateConfidenceIntervalForSample(
			String category) {
		if (!sampleData.containsKey(category)) {
			return null;
		}

		List<VehicleSample> samples = sampleData.get(category);

		double sd = calculateStandardDeviation(samples);

		double sde = sd / Math.sqrt(samples.size());

		double alpha = 0.05;
		double phi = 0.975;
		double z = 1.96;

		// XBAR - z*sde <= u
		// XBAR + z*sde >= u

		double mean = calculateMean(samples);

		double lowerEndpoint = mean - z * sde;
		double upperEndpoint = mean + z * sde;

		DecimalFormat df = new DecimalFormat("#.###");
		System.out.println(category + "\t\tSD=" + df.format(sd) + "\t\tD="
				+ df.format(upperEndpoint - lowerEndpoint) + " ("
				+ df.format(lowerEndpoint) + "-" + df.format(upperEndpoint)
				+ ")");

		return new ConfidenceInterval(mean, samples.size(), z * sde, z * sde,
				lowerEndpoint, upperEndpoint, (1 - alpha));
	}

	public static void purgeAllSampleData() {
		sampleData.clear();
	}

}
