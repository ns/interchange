package org.uci.luci.interchange;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import org.uci.luci.interchange.Intersections.*;
import org.uci.luci.interchange.OSM.*;
import org.uci.luci.interchange.Driver.*;
import org.uci.luci.interchange.Exception.*;
import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Util.StatisticsLogger.ConfidenceInterval;
import org.uci.luci.interchange.Util.StatisticsLogger.VehicleSample;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Factory.*;

public class Simulator extends Thread {
	private boolean debug = false;
	private boolean paused;
	ArrayList<String> spawnPoints = new ArrayList<String>();
	public int lastSimulatorStepTotalVehicles;
	long lastSimulatorStepTotalTime;
	int delay;
	int simulatorTicksSinceCheck;
	long simulatorTimeSinceCheck;
	double tickLength = 1.0 / 5.0; // 1/60th of a sec
	double simulatorTime = 0;
	// 60 for rushed tests, 120 for max
	int spawnRate = 5; // 120;
	int tick = 0;
	int percentOfRushedDrivers = 0;
	int driverRushingLevel = 100;

	// "important" paths where drivers will be rushed
	String impPath1Origin = "122969631";
	String impPath1Destination = "331384664";
	String impPath2Origin = "122788471";
	String impPath2Destination = "122880644";

	public void setSpawnRate(int sr) {
		spawnRate = sr;
	}

	public int getSpawnRate() {
		return spawnRate;
	}

	public double simulatorTime() {
		return simulatorTime;
	}

	public Simulator() throws InterruptedException {
		delay = 10;
		lastSimulatorStepTotalVehicles = 0;
		lastSimulatorStepTotalTime = 0;
		simulatorTicksSinceCheck = 0;
		simulatorTimeSinceCheck = 0;
		paused = false;

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("Simulator performance");

				System.out.println("\tFree memory: "
						+ Utils.humanReadableByteCount(Runtime.getRuntime()
								.freeMemory(), false));
				System.out
						.println("\tMaximum memory: "
								+ (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit"
										: Utils.humanReadableByteCount(Runtime
												.getRuntime().maxMemory(),
												false)));
				System.out.println("\t" + lastSimulatorStepTotalVehicles
						+ " vehicles in simulator.");
				System.out.println("\t" + lastSimulatorStepTotalTime
						+ " ns per simulator step.");

				double tps = (simulatorTicksSinceCheck / ((System.nanoTime() - simulatorTimeSinceCheck) / 1000000000.0));
				System.out.println("\t" + tps + " ticks per sec");
				simulatorTicksSinceCheck = 0;
				simulatorTimeSinceCheck = System.nanoTime();

				double nsPerVehicle = 0;
				if (lastSimulatorStepTotalVehicles != 0)
					nsPerVehicle = (lastSimulatorStepTotalTime / lastSimulatorStepTotalVehicles);
				double vps = 1.0 / (nsPerVehicle / 1000000000);

				DecimalFormat df = new DecimalFormat();
				System.out.println("\taround " + df.format(vps)
						+ " vehicle ticks per sec.");
			}
		};
		new Timer(10000, taskPerformer).start();
	}

	public void initPhase() {
		Oracle.generateRoutes(300);

		try {
			Oracle.generatePath(Global.openStreetMap.getNode(impPath1Origin),
					Global.openStreetMap.getNode(impPath1Destination));
			Oracle.generatePath(Global.openStreetMap.getNode(impPath2Origin),
					Global.openStreetMap.getNode(impPath2Destination));
		} catch (NoPathToDestinationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SpawningThread spawnThread = new SpawningThread();
		// spawnThread.start();
	}

	public void generateVehiclesPhase(int tick) {
		// if (tick % 50 == 0) {
		// Vehicle v = null;
		// VehicleDriver d = null;
		// try {
		// v = VehicleFactory.createVehicleAtNode(Global.openStreetMap
		// .getNode(impPath1Origin));
		// if (v == null)
		// return;
		// v.spawnedAtSpawnRate = spawnRate;
		// d = VehicleDriverFactory.createVehicleDriver(v);
		// d.setDestinationAndGo(impPath1Destination);
		//
		// d.setRushedness(driverRushingLevel);
		// d.driverGroup = 1;
		// d.spawnedAtPercentRushedness = percentOfRushedDrivers;
		//
		// v.isBeingCreated = false;
		// } catch (NoPathToDestinationException e) {
		// if (d != null)
		// VehicleDriverFactory.destroyVehicleDriver(d);
		// if (v != null)
		// VehicleFactory.destroyVehicle(v);
		// }
		// }
		//
		// if (tick % 50 == 25) {
		// Vehicle v = null;
		// VehicleDriver d = null;
		// try {
		// v = VehicleFactory.createVehicleAtNode(Global.openStreetMap
		// .getNode(impPath2Origin));
		// if (v == null)
		// return;
		// v.spawnedAtSpawnRate = spawnRate;
		// d = VehicleDriverFactory.createVehicleDriver(v);
		// d.setDestinationAndGo(impPath2Destination);
		//
		// d.setRushedness(driverRushingLevel);
		// d.driverGroup = 2;
		// d.spawnedAtPercentRushedness = percentOfRushedDrivers;
		//
		// v.isBeingCreated = false;
		// } catch (NoPathToDestinationException e) {
		// if (d != null)
		// VehicleDriverFactory.destroyVehicleDriver(d);
		// if (v != null)
		// VehicleFactory.destroyVehicle(v);
		// }
		// }

		if (tick % spawnRate == 0) {
			Vehicle v = null;
			VehicleDriver d = null;
			try {
				List<String> route = Oracle.randomRoute();

				v = VehicleFactory.createVehicleAt(route.get(0), route.get(1));
				if (v == null)
					return;
				// v = VehicleFactory.createVehicleAtRandomPoint();
				v.spawnedAtSpawnRate = spawnRate;
				d = VehicleDriverFactory.createVehicleDriver(v);
				// d.pickRandomDestinationAndGo();
				d.setDestinationAndGo(route.get(route.size() - 1));

				if (Utils.randomNumberGenerator().nextInt(100) < percentOfRushedDrivers && false) {
					d.setRushedness(driverRushingLevel);
					d.driverGroup = 1;

				} else {
					d.setRushedness(1);
					d.driverGroup = 3;
				}
				d.spawnedAtPercentRushedness = percentOfRushedDrivers;

				// if (Utils.randomNumberGenerator().nextInt(4) ==
				// 0) {
				// d.setRushedness(100);
				// // d.spawnedAtPercentRushedness =
				// } else {
				// d.setRushedness(0);
				// }

				v.isBeingCreated = false;
			} catch (NoPathToDestinationException e) {
				if (d != null)
					VehicleDriverFactory.destroyVehicleDriver(d);
				if (v != null)
					VehicleFactory.destroyVehicle(v);
			}
		}
	}

	public void vehicleDriversTickPhase(int tick) {
		for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
			if (d.vehicle.paused() || d.vehicle.isBeingCreated)
				continue;

			try {
				d.tick(simulatorTime, tickLength, tick);
			} catch (Exception e) {
				for (VehicleDriver dd : VehicleDriverRegistry
						.allLicensedDrivers()) {
				}
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void intersectionTickPhase(int tick) {
		for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
			i.tick(simulatorTime, tickLength, tick);
		}
	}

	public void commitPhase(int tick) {
		for (Vehicle v : VehicleRegistry.allRegisteredVehicles()) {
			if (v.isBeingCreated)
				continue;
			v.commit(simulatorTime, tickLength);
		}
	}

	public void purgePhase(int tick) {
		for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
			if (d.vehicle.flagForRemoval) {
				Vehicle vv = d.vehicle;

				// if (vv.spawnedAtSpawnRate == spawnRate) {
				// StatisticsLogger.addSample(vv.spawnedAtSpawnRate + "",
				// new VehicleSample(vv.vin, simulatorTime,
				// vv.vehicleTotalTraveledDistance,
				// vv.vehicleTotalWaitTime, d.rushedness()));
				//
				// StatisticsLogger
				// .log("vehicle.distTraveled2DelayTime-all",
				// simulatorTime + "," + vv.vin + ","
				// + vv.vehicleTotalTraveledDistance
				// + "," + vv.vehicleTotalWaitTime
				// + "," + vv.spawnedAtSpawnRate + ","
				// + spawnRate + "," + d.rushedness()
				// + ","
				// + d.spawnedAtPercentRushedness
				// + "," + vv.leftTurnsMade + ","
				// + vv.rightTurnsMade + ","
				// + vv.throughsMade);
				// }

				// if (vv.throughsMade >= 10 && vv.throughsMade <= 15
				// && vv.leftTurnsMade >= 1 && vv.leftTurnsMade <= 5) {
				if (d.driverGroup == 1) {
					StatisticsLogger.addSample("g1-"
							+ d.spawnedAtPercentRushedness,// vv.spawnedAtSpawnRate
							// + "",//
							// spawnRate
							new VehicleSample(vv.vin, simulatorTime,
									vv.vehicleTotalTraveledDistance,
									vv.vehicleTotalWaitTime, d.rushedness(),
									vv.throughsMade));

					StatisticsLogger.log("vehicle.distTraveled2DelayTime-g1",
							simulatorTime + "," + vv.vin + ","
									+ vv.vehicleTotalTraveledDistance + ","
									+ vv.vehicleTotalWaitTime + ","
									+ vv.spawnedAtSpawnRate + "," + spawnRate
									+ "," + d.rushedness() + ","
									+ d.spawnedAtPercentRushedness + ","
									+ vv.leftTurnsMade + ","
									+ vv.rightTurnsMade + "," + vv.throughsMade
									+ "," + driverRushingLevel + ","
									+ vv.vehicleTotalStoppedTimeAtLeft + ","
									+ vv.vehicleTotalStoppedTimeAtRight + ","
									+ vv.vehicleTotalStoppedTimeAtThrough);
				}
				if (d.driverGroup == 2) {
					StatisticsLogger.addSample("g2-"
							+ d.spawnedAtPercentRushedness,// vv.spawnedAtSpawnRate
							// + "",//
							// spawnRate
							new VehicleSample(vv.vin, simulatorTime,
									vv.vehicleTotalTraveledDistance,
									vv.vehicleTotalWaitTime, d.rushedness(),
									vv.throughsMade));
					StatisticsLogger.log("vehicle.distTraveled2DelayTime-g2",
							simulatorTime + "," + vv.vin + ","
									+ vv.vehicleTotalTraveledDistance + ","
									+ vv.vehicleTotalWaitTime + ","
									+ vv.spawnedAtSpawnRate + "," + spawnRate
									+ "," + d.rushedness() + ","
									+ d.spawnedAtPercentRushedness + ","
									+ vv.leftTurnsMade + ","
									+ vv.rightTurnsMade + "," + vv.throughsMade
									+ "," + driverRushingLevel + ","
									+ vv.vehicleTotalStoppedTimeAtLeft + ","
									+ vv.vehicleTotalStoppedTimeAtRight + ","
									+ vv.vehicleTotalStoppedTimeAtThrough);
				} else if (d.driverGroup == 3) {
					StatisticsLogger.addSample("g3-"
							+ d.spawnedAtPercentRushedness + "",//
							// vv.spawnedAtSpawnRate
							// + "",//
							// spawnRate
							new VehicleSample(vv.vin, simulatorTime,
									vv.vehicleTotalTraveledDistance,
									vv.vehicleTotalWaitTime, d.rushedness(),
									vv.throughsMade));

					StatisticsLogger.log("vehicle.distTraveled2DelayTime-g3",
							simulatorTime + "," + vv.vin + ","
									+ vv.vehicleTotalTraveledDistance + ","
									+ vv.vehicleTotalWaitTime + ","
									+ vv.spawnedAtSpawnRate + "," + spawnRate
									+ "," + d.rushedness() + ","
									+ d.spawnedAtPercentRushedness + ","
									+ vv.leftTurnsMade + ","
									+ vv.rightTurnsMade + "," + vv.throughsMade
									+ "," + driverRushingLevel + ","
									+ vv.vehicleTotalStoppedTimeAtLeft + ","
									+ vv.vehicleTotalStoppedTimeAtRight + ","
									+ vv.vehicleTotalStoppedTimeAtThrough);
				}
				// }

				// if (d.rushedness() <= 50
				// && d.spawnedAtPercentRushedness == percentOfRushedDrivers) {
				// StatisticsLogger.addSample(d.spawnedAtPercentRushedness
				// + "-l",// vv.spawnedAtSpawnRate
				// // + "",//
				// // spawnRate
				// new VehicleSample(vv.vin, simulatorTime,
				// vv.vehicleTotalTraveledDistance,
				// vv.vehicleTotalWaitTime, d.rushedness()));
				// StatisticsLogger.log("vehicle.distTraveled2DelayTime-low",
				// simulatorTime + "," + vv.vin + ","
				// + vv.vehicleTotalTraveledDistance + ","
				// + vv.vehicleTotalWaitTime + ","
				// + vv.spawnedAtSpawnRate + "," + spawnRate
				// + "," + d.rushedness() + ","
				// + d.spawnedAtPercentRushedness);
				// } else if (d.spawnedAtPercentRushedness ==
				// percentOfRushedDrivers) {
				// StatisticsLogger.addSample(d.spawnedAtPercentRushedness
				// + "-h",// vv.spawnedAtSpawnRate
				// // + "",//
				// // spawnRate
				// new VehicleSample(vv.vin, simulatorTime,
				// vv.vehicleTotalTraveledDistance,
				// vv.vehicleTotalWaitTime, d.rushedness()));
				//
				// StatisticsLogger.log("vehicle.distTraveled2DelayTime-high",
				// simulatorTime + "," + vv.vin + ","
				// + vv.vehicleTotalTraveledDistance + ","
				// + vv.vehicleTotalWaitTime + ","
				// + vv.spawnedAtSpawnRate + "," + spawnRate
				// + "," + d.rushedness() + ","
				// + d.spawnedAtPercentRushedness);
				// }

				VehicleDriverFactory.destroyVehicleDriver(d);
				VehicleFactory.destroyVehicle(d.vehicle);
			}
		}
	}

	private void statsForSpawnRate(int tick) {
		ConfidenceInterval ci = StatisticsLogger
				.calculateConfidenceIntervalForSample(spawnRate + "");

		if (ci != null)
			System.out.println("ci = " + ci + " samples = " + ci.samples
					+ " range = " + ci.range());
		if (ci != null && ci.samples > 200) {
			StatisticsLogger.purgeAllSampleData();

			// increasing spawn rate
			spawnRate -= 1;
			if (spawnRate < 5) {
				System.out.println("Done!");
				System.exit(0);
			}
		}
	}

	private void statsForConstantEverything(int tick) {
		ConfidenceInterval ci = StatisticsLogger
				.calculateConfidenceIntervalForSample(spawnRate + "");

		if (ci != null)
			System.out.println("ci = " + ci + " samples = " + ci.samples
					+ " range = " + ci.range());
		if (ci != null && ci.samples > 10000 && ci.range() < 10) {
			StatisticsLogger.purgeAllSampleData();
			System.out.println("Done!");
			System.exit(0);
		}
	}

	private void statsForSpawnRateWithTwoRushingRates(int tick) {
		ConfidenceInterval ciH = StatisticsLogger
				.calculateConfidenceIntervalForSample("g1-"
						+ percentOfRushedDrivers);
		ConfidenceInterval ciL = StatisticsLogger
				.calculateConfidenceIntervalForSample("g3-"
						+ percentOfRushedDrivers);
		if (ciL != null && ciH != null) {
			System.out.println("ci = " + ciL + " samples = " + ciL.samples
					+ " range = " + ciL.range());
			System.out.println("ci = " + ciH + " samples = " + ciH.samples
					+ " range = " + ciH.range());
		} else {
			System.out.println("..null");
		}

		if (ciL != null && ciL.samples > 200 && ciH != null
				&& ciH.samples > 200) {
			StatisticsLogger.purgeAllSampleData();

			percentOfRushedDrivers += 5;
			// spawnRate -= 1;
			// if (spawnRate < 5) {
			if (percentOfRushedDrivers > 95) {
				System.out.println("Done!");
				System.exit(0);
			}
		}
	}

	private void statsForConstantSpawnRateWithRushingRateIncreasing(int tick) {
		ConfidenceInterval ciH = StatisticsLogger
				.calculateConfidenceIntervalForSample(percentOfRushedDrivers
						+ "-h");
		ConfidenceInterval ciL = StatisticsLogger
				.calculateConfidenceIntervalForSample(percentOfRushedDrivers
						+ "-l");
		if (ciL != null && ciH != null) {
			System.out.println("ci = " + ciL + " samples = " + ciL.samples
					+ " range = " + ciL.range());
			System.out.println("ci = " + ciH + " samples = " + ciH.samples
					+ " range = " + ciH.range());
		} else {
			System.out.println("..null");
		}

		if (ciL != null && ciL.samples > 50 && ciL.sd() < 15 && ciH != null
				&& ciH.samples > 50 && ciH.sd() < 15) {
			StatisticsLogger.purgeAllSampleData();

			percentOfRushedDrivers += 10;
			if (percentOfRushedDrivers > 90) {
				System.out.println("Done!");
				System.exit(0);
			}
		}
	}

	private void statsForConstantSpawnRateWithRushingLevelIncreasing(int tick) {
		ConfidenceInterval ciH = StatisticsLogger
				.calculateConfidenceIntervalForSample("g1-"
						+ driverRushingLevel);
		ConfidenceInterval ciL = StatisticsLogger
				.calculateConfidenceIntervalForSample("g3-"
						+ driverRushingLevel);
		if (ciL != null && ciH != null) {
			System.out.println("ci = " + ciL + " samples = " + ciL.samples
					+ " range = " + ciL.range());
			System.out.println("ci = " + ciH + " samples = " + ciH.samples
					+ " range = " + ciH.range());
		} else {
			System.out.println("..null");
		}

		// if (ciL != null && ciL.samples > 50 && ciL.sd() < 20 && ciH != null
		// && ciH.samples > 50 && ciH.sd() < 20) {
		// StatisticsLogger.purgeAllSampleData();
		if (ciL != null && ciL.samples > 200 && ciH != null
				&& ciH.samples > 200) {
			driverRushingLevel += 5;
			if (driverRushingLevel > 100) {
				System.out.println("Done!");
				System.exit(0);
			}
		}
	}

	public void statsPhase(int tick) {
		// if ((tick%(tickLength/60)*60)!=0)
		if (tick % (60 * 30) != 0)
			return;

		// statsForSpawnRate(tick);
		// statsForConstantEverything(tick);
		statsForSpawnRateWithTwoRushingRates(tick);
		// statsForConstantSpawnRateWithRushingRateIncreasing(tick);
		// statsForConstantSpawnRateWithRushingLevelIncreasing(tick);

		// ConfidenceInterval ciH = StatisticsLogger
		// .calculateConfidenceIntervalForSample(percentOfRushedDrivers
		// + "-h");
		// ConfidenceInterval ciL = StatisticsLogger
		// .calculateConfidenceIntervalForSample(percentOfRushedDrivers
		// + "-l");

		// if (ciL != null && ciH != null) {
		// System.out.println("ci = " + ciL + " samples = " + ciL.samples
		// + " range = " + ciL.range());
		// System.out.println("ci = " + ciH + " samples = " + ciH.samples
		// + " range = " + ciH.range());
		// } else {
		// System.out.println("..null");
		// }

		// if (ciL != null && ciL.samples > 200 && ciL.range() < 10 && ciH !=
		// null
		// && ciH.samples > 200 && ciH.range() < 10) {
		// StatisticsLogger.purgeAllSampleData();
		//
		// spawnRate -= 10;
		// if (spawnRate < 10) {
		// System.out.println("Done!");
		// System.exit(0);
		// }
		// }

		// if (ciL != null && ciL.samples > 800 && ciL.range() < 5 && ciH !=
		// null
		// && ciH.samples > 800 && ciH.range() < 5) {
		// StatisticsLogger.purgeAllSampleData();
		//
		// // increasing % rushed drivers
		// percentOfRushedDrivers += 10;
		// if (percentOfRushedDrivers > 90) {
		// System.out.println("Done!");
		// System.exit(0);
		// }
		// }
	}

	public void collisionTestPhase(int tick) {
		// log("\t// collision test");
		// List<Vehicle> collisions =
		// VehicleCollisionChecker.checkCollisions(VehicleRegistry.allRegisteredVehicles());
		// for (Vehicle v : collisions)
		// v.pause();
	}

	public void run() {
		try {
			tick = 0;
			simulatorTimeSinceCheck = System.nanoTime();

			initPhase();

			while (true) {
				if (paused) {
					Thread.sleep(500);
					continue;
				}

				tick++;
				simulatorTicksSinceCheck++;

				long startTime = System.nanoTime();
				long endTime;

				generateVehiclesPhase(tick);
				vehicleDriversTickPhase(tick);
				intersectionTickPhase(tick);
				commitPhase(tick);
				collisionTestPhase(tick);
				purgePhase(tick);
				statsPhase(tick);

				endTime = System.nanoTime();
				long duration = endTime - startTime;
				lastSimulatorStepTotalVehicles = VehicleRegistry
						.allRegisteredVehicles().size();
				lastSimulatorStepTotalTime = duration;

				simulatorTime += tickLength;

				if (delay >= 1)
					Thread.sleep(delay);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		paused = true;
	}

	public void unpause() {
		paused = false;
	}

	public void changeSpeed(int delta) {
		if (delay + delta < 0)
			delay = 0;
		else
			delay += delta;
	}

	public void setSpeed(int speed) {
		delay = speed;
	}

	private void log(String str) {
		if (!debug)
			return;
		System.out.println(str);
	}
}