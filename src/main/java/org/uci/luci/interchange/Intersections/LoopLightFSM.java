package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Intersections.LightFSM.LIGHT;
import org.uci.luci.interchange.Util.*;

import java.util.ArrayList;
import java.util.Random;

public class LoopLightFSM {
	private double deadTimeDur = 0;
	private double throughsGreenDur, throughsYellowDur;
	private double leftsGreenDur, leftsYellowDur;

	private double lastStateChangeAt;
	private String state;

	private ArrayList<String> vehiclesOnLeftGroup1 = new ArrayList<String>();
	private ArrayList<String> vehiclesOnThroughGroup1 = new ArrayList<String>();
	private ArrayList<String> vehiclesOnLeftGroup2 = new ArrayList<String>();
	private ArrayList<String> vehiclesOnThroughGroup2 = new ArrayList<String>();

	public LoopLightFSM(double throughsGreenDur, double throughsYellowDur,
			double leftsGreenDur, double leftsYellowDur) {
		this.throughsGreenDur = throughsGreenDur;
		this.throughsYellowDur = throughsYellowDur;
		this.leftsGreenDur = leftsGreenDur;
		this.leftsYellowDur = leftsYellowDur;

		Random randomGenerator = Utils.randomNumberGenerator();
		lastStateChangeAt = 0;

		int randInt = randomGenerator.nextInt(9);

		if (randInt == 0)
			state = "all_red";
		else if (randInt == 1)
			state = "all_red";
		else if (randInt == 2)
			state = "lefts_green1";
		else if (randInt == 3)
			state = "lefts_yellow1";
		else if (randInt == 4)
			state = "throughs_green1";
		else if (randInt == 5)
			state = "throughs_yellow1";
		else if (randInt == 6)
			state = "lefts_green2";
		else if (randInt == 7)
			state = "lefts_yellow2";
		else if (randInt == 8)
			state = "throughs_green2";
		else if (randInt == 9)
			state = "throughs_yellow2";
		else
			state = "all_red";
	}

	public void detectVehicleOnLeftGroup1(String vin) {
		vehiclesOnLeftGroup1.add(vin);
	}

	public void detectVehicleOnThroughGroup1(String vin) {
		vehiclesOnThroughGroup1.add(vin);
	}

	public void detectVehicleOnLeftGroup2(String vin) {
		vehiclesOnLeftGroup2.add(vin);
	}

	public void detectVehicleOnThroughGroup2(String vin) {
		vehiclesOnThroughGroup2.add(vin);
	}

	public void undetectVehicle(String vin) {
		vehiclesOnLeftGroup1.add(vin);
		vehiclesOnThroughGroup1.remove(vin);
		vehiclesOnLeftGroup2.remove(vin);
		vehiclesOnThroughGroup2.remove(vin);
	}

	public String getState() {
		return state;
	}

	public LIGHT getLightForRights1() {
		return LIGHT.GREEN;
	}

	public LIGHT getLightForRights2() {
		return LIGHT.GREEN;
	}

	public LIGHT getLightForThrough1() {
		if (state.equals("throughs_green1"))
			return LIGHT.GREEN;
		else if (state.equals("throughs_yellow1"))
			return LIGHT.YELLOW;
		return LIGHT.RED;
	}

	public LIGHT getLightForLefts1() {
		if (state.equals("lefts_green1"))
			return LIGHT.GREEN;
		else if (state.equals("lefts_yellow1"))
			return LIGHT.YELLOW;
		return LIGHT.RED;
	}

	public LIGHT getLightForThrough2() {
		if (state.equals("throughs_green2"))
			return LIGHT.GREEN;
		else if (state.equals("throughs_yellow2"))
			return LIGHT.YELLOW;
		return LIGHT.RED;
	}

	public LIGHT getLightForLefts2() {
		if (state.equals("lefts_green2"))
			return LIGHT.GREEN;
		else if (state.equals("lefts_yellow2"))
			return LIGHT.YELLOW;
		return LIGHT.RED;
	}

	public void tick(double simTime, double tickLength, int tick) {
		double sinceLastStateChange = simTime - lastStateChangeAt;

		if (state.equals("all_red") && sinceLastStateChange > deadTimeDur) {
			if (vehiclesOnLeftGroup1.size() > 0)
				state = "lefts_green1";
			else if (vehiclesOnThroughGroup1.size() > 0)
				state = "throughs_green1";
			else if (vehiclesOnLeftGroup2.size() > 0)
				state = "lefts_green2";
			else if (vehiclesOnThroughGroup2.size() > 0)
				state = "throughs_green2";
			else
				state = "lefts_green1";
			lastStateChangeAt = simTime;
		} else if (state.equals("lefts_green1")
				&& sinceLastStateChange > leftsGreenDur) {
			state = "lefts_yellow1";
			lastStateChangeAt = simTime;
		} else if (state.equals("lefts_yellow1")
				&& sinceLastStateChange > leftsYellowDur) {
			state = "throughs_green1";
			lastStateChangeAt = simTime;
		} else if (state.equals("throughs_green1")) {
			if (sinceLastStateChange > throughsGreenDur
					|| vehiclesOnThroughGroup1.size() == 0
					&& (vehiclesOnLeftGroup2.size() > 0 || vehiclesOnThroughGroup2
							.size() > 0)) {
				state = "throughs_yellow1";
				lastStateChangeAt = simTime;
			}
		} else if (state.equals("throughs_yellow1")
				&& sinceLastStateChange > throughsYellowDur) {
			state = "lefts_green2";
			lastStateChangeAt = simTime;
		} else if (state.equals("lefts_green2")
				&& sinceLastStateChange > leftsGreenDur) {
			state = "lefts_yellow2";
			lastStateChangeAt = simTime;
		} else if (state.equals("lefts_yellow2")
				&& sinceLastStateChange > leftsYellowDur) {
			state = "throughs_green2";
			lastStateChangeAt = simTime;
		} else if (state.equals("throughs_green2")) {
			if (sinceLastStateChange > throughsGreenDur
					|| vehiclesOnThroughGroup2.size() == 0
					&& (vehiclesOnLeftGroup1.size() > 0 || vehiclesOnThroughGroup1
							.size() > 0)) {
				state = "throughs_yellow2";
				lastStateChangeAt = simTime;
			}
		} else if (state.equals("throughs_yellow2")
				&& sinceLastStateChange > throughsYellowDur) {
			state = "all_red";
			lastStateChangeAt = simTime;
		}
	}
}