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

public class Simulator {
	ArrayList<String> spawnPoints = new ArrayList<String>();

	int lastSimulatorStepTotalVehicles;
	long lastSimulatorStepTotalTime;
	int tick = 0;
	boolean runSim = true;
	boolean reset = false;
	Timer timer;

	public Simulator() throws InterruptedException {
		lastSimulatorStepTotalVehicles = 0;
		lastSimulatorStepTotalTime = 0;

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("Simulator performance");

				System.out.println("\tFree memory: " + humanReadableByteCount(Runtime.getRuntime().freeMemory(), false));
				System.out.println("\tMaximum memory: " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : humanReadableByteCount(Runtime.getRuntime().maxMemory(), false)));
				System.out.println("\t" + lastSimulatorStepTotalVehicles + " vehicles in simulators.");
				System.out.println("\t" + lastSimulatorStepTotalTime + " ns per simulator step.");

				double nsPerVehicle = (lastSimulatorStepTotalTime/lastSimulatorStepTotalVehicles);
				double vps = 1.0/(nsPerVehicle/1000000000);

				DecimalFormat df = new DecimalFormat();
				// DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				// dfs.setGroupingSeparator('.');
				// df.setDecimalFormatSymbols(dfs);
				// System.out.println(df.format((int)num));
				System.out.println("\taround " + df.format(vps) + " vehicle ticks per sec.");
			}
		};
		timer = new Timer(2000, taskPerformer);
		//timer.start();
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public void simulate() throws InterruptedException {
		//timer.start();
		while(true) {
			tickSimulator();
			do
			{
				Thread.sleep(5);
			}while(!runSim);
			if(reset)
			{
				reset = false;
				tick = 0;
				VehicleRegistry.reset();
				Oracle.resetVehicles();
				VehicleDriverRegistry.reset();
			}
		}
	}

	public void resetSimulator() throws InterruptedException
	{
		stopSimulating();
		reset = true;
		
		startSimulating();
	}

	public void stopSimulating() throws InterruptedException
	{
		//timer.stop();
		runSim = false;
	}

	public void startSimulating()
	{
		runSim = true;
	}

	public void tickSimulator()
	{
		tick++;
		if (tick%30 == 1) {
			Vehicle v = VehicleFactory.createVehicleAtNode(Global.openStreetMap.getNode("122633613"));
			// Vehicle v = VehicleFactory.createVehicleAtRandomPoint();
			VehicleDriver d = VehicleDriverFactory.createVehicleDriver(v);
		}

		long startTime = System.nanoTime();
		long endTime;

		//System.out.println("vehicles: tick");
		for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
			d.tick(tick);
		}

		for (Vehicle v : VehicleRegistry.allRegisteredVehicles()) {
			// each vehicle's velocity vector has been determined by now
			// we simply calculate exactly where the vehicle should be for
			// the next timestep

			if (v.velocity == null)
				continue;

			Node lastNode = v.getOriginNode();
			Node nextNode = v.getDestinationNode();

			double newLat = v.lat + v.velocity.x;
			double newLon = v.lon + v.velocity.y;

			v.lat = (double)newLat;
			v.lon = (double)newLon;
		}

		for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
			i.tick(tick);
		}

		endTime = System.nanoTime();
		long duration = endTime - startTime;
		lastSimulatorStepTotalVehicles = VehicleRegistry.allRegisteredVehicles().size();
		lastSimulatorStepTotalTime = duration;
	}

}