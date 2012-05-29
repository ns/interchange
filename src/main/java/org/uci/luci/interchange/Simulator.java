package org.uci.luci.interchange;

import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import org.uci.luci.interchange.Intersections.*;
import org.uci.luci.interchange.Driver.*;
import org.uci.luci.interchange.Exception.*;
import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Util.*;
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
	double tickLength = 1.0 / 60.0; // 1/60th of a sec
	double simulatorTime = 0;

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
				// DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				// dfs.setGroupingSeparator('.');
				// df.setDecimalFormatSymbols(dfs);
				// log(df.format((int)num));
				System.out.println("\taround " + df.format(vps)
						+ " vehicle ticks per sec.");
			}
		};
		new Timer(2000, taskPerformer).start();
	}

	public void initPhase() {
    // int numVehiclesToGenerate = 10000;
    int numVehiclesToGenerate = 2000;
		for (int i = 0; i < numVehiclesToGenerate; i++) {
			log("\t// generating vehicle");
			Vehicle v = null;
			VehicleDriver d = null;
			try {
				// v = VehicleFactory.createVehicleAtNode(Global.openStreetMap.getNode("122733227"));
				v = VehicleFactory.createVehicleAtRandomPoint();
				d = VehicleDriverFactory.createVehicleDriver(v);
				d.pickRandomDestinationAndGo();
				// d.setDestinationAndGo("249586091");
			} catch (NoPathToDestinationException e) {
				if (d != null)
					VehicleDriverFactory.destroyVehicleDriver(d);
				if (v != null)
					VehicleFactory.destroyVehicle(v);
			}
			System.out.println("Generated vehicle " + i + " of " + numVehiclesToGenerate);
		}
	}

	public void generateVehiclesPhase(int tick) {
		// if (tick % 500 == 1) {
		// log("\t// generating vehicle");
		// Vehicle v = null;
		// VehicleDriver d = null;
		// try {
		// v =
		// VehicleFactory.createVehicleAtNode(Global.openStreetMap.getNode("123068182"));
		// // v = VehicleFactory.createVehicleAtRandomPoint();
		//
		// d = VehicleDriverFactory.createVehicleDriver(v);
		// d.setDestinationAndGo("122959098");
		// // d.pickRandomDestinationAndGo();
		// } catch (NoPathToDestinationException e) {
		// if (d != null)
		// VehicleDriverFactory.destroyVehicleDriver(d);
		// if (v != null)
		// VehicleFactory.destroyVehicle(v);
		// }
		// }
	}

	public void vehicleDriversTickPhase(int tick) {
		log("\t// drivers.tick()");
		for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
			if (d.vehicle.paused())
				continue;

			try {
				d.tick(simulatorTime, tickLength, tick);
			} catch (Exception e) {
				log("\tCrash for v = " + d.vehicle.vin + " license = "
						+ d.licence);
				for (VehicleDriver dd : VehicleDriverRegistry
						.allLicensedDrivers()) {
					log("\t\ttick v = " + dd.vehicle.vin + " license = "
							+ dd.licence);
				}
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void intersectionTickPhase(int tick) {
		log("\t// intersections.tick()");
		for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
			i.tick(simulatorTime, tickLength, tick);
		}
	}

	public void commitPhase(int tick) {
		log("\t// moving vehicles");
		for (Vehicle v : VehicleRegistry.allRegisteredVehicles()) {
			v.commit(simulatorTime, tickLength);
		}
	}

	public void purgePhase(int tick) {
		log("\t// removing flagged vehicles");
		for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
			if (d.vehicle.flagForRemoval) {
				log("removing vehicle " + d.vehicle.vin);
				Vehicle vv = d.vehicle;

				VehicleDriverFactory.destroyVehicleDriver(d);
				VehicleFactory.destroyVehicle(d.vehicle);

				if (VehicleRegistry.allRegisteredVehicles().contains(vv)) {
					log("clearly this doesn't work");
				}

				for (VehicleDriver dx : VehicleDriverRegistry
						.allLicensedDrivers()) {
					// if
					// (VehicleDriverRegistry.allLicensedDrivers().contains(d))
					// {
					if (d.licence == dx.licence)
						log("clearly this doesn't work (d)");
				}

				log("removed vehicle " + vv.vin);
			}
		}
	}

	public void collisionTestPhase(int tick) {
		// log("\t// collision test");
		// List<Vehicle> collisions =
		// VehicleCollisionChecker.checkCollisions(VehicleRegistry.allRegisteredVehicles());
		// for (Vehicle v : collisions)
		// v.pause();
	}

	@Override
	public void run() {
		try {
			int tick = 0;
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