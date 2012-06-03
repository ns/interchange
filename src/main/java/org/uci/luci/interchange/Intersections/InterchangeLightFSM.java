package org.uci.luci.interchange.Intersections;

import java.util.HashMap;
import java.util.Random;

import org.uci.luci.interchange.Intersections.LightFSM.LIGHT;
import org.uci.luci.interchange.Util.Utils;

public class InterchangeLightFSM {
	private double deadTimeDur = 5;
	private double minThroughsGreenDur;
	private double throughsGreenDur, throughsYellowDur;
	private double leftsGreenDur, leftsYellowDur;

	private double lastStateChangeAt;
	private String state;

	HashMap<String, Integer> g1Bids, g1BidsLeft, g2Bids, g2BidsLeft;

	public InterchangeLightFSM(double minThroughsGreenDur,
			double throughsGreenDur, double throughsYellowDur,
			double leftsGreenDur, double leftsYellowDur) {
		g1Bids = new HashMap<String, Integer>();
		g1BidsLeft = new HashMap<String, Integer>();
		g2Bids = new HashMap<String, Integer>();
		g2BidsLeft = new HashMap<String, Integer>();

		this.minThroughsGreenDur = minThroughsGreenDur;
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
			state = "lefts_green1";
		else if (randInt == 2)
			state = "lefts_yellow1";
		else if (randInt == 3)
			state = "throughs_green1";
		else if (randInt == 4)
			state = "throughs_yellow1";
		else if (randInt == 5)
			state = "lefts_green2";
		else if (randInt == 6)
			state = "lefts_yellow2";
		else if (randInt == 7)
			state = "throughs_green2";
		else if (randInt == 8)
			state = "throughs_yellow2";
		else
			state = "all_red";
	}

	public String getState() {
		return state + " (g1 " + group1Bids() + " g2 " + group2Bids() + ")";
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

	public void acceptBidGroup1Left(String vin, int value) {
		synchronized (this) {
			g1BidsLeft.put(vin, value);
		}
	}

	public void acceptBidGroup1Right(String vin, int value) {
		// ignore
	}

	public void acceptBidGroup1Through(String vin, int value) {
		synchronized (this) {
			g1Bids.put(vin, value);
		}
	}

	public void acceptBidGroup2Left(String vin, int value) {
		synchronized (this) {
			g2BidsLeft.put(vin, value);
		}
	}

	public void acceptBidGroup2Right(String vin, int value) {
		// ignore
	}

	public void acceptBidGroup2Through(String vin, int value) {
		synchronized (this) {
			g2Bids.put(vin, value);
		}
	}

	public void clearBidForVehicle(String vin) {
		synchronized (this) {
			g1Bids.remove(vin);
			g1BidsLeft.remove(vin);
			g2Bids.remove(vin);
			g2BidsLeft.remove(vin);
		}
	}

	private int group1Bids() {
		int sum = 0;
		synchronized (this) {
			for (int i : g1Bids.values())
				sum += i;
			for (int i : g1BidsLeft.values())
				sum += i;
		}
		return sum;
	}

	private int group2Bids() {
		int sum = 0;
		synchronized (this) {
			for (int i : g2Bids.values())
				sum += i;
			for (int i : g2BidsLeft.values())
				sum += i;
		}
		return sum;
	}

	private int numGroup1Bids() {
		int sum = 0;
		synchronized (this) {
			sum += g1Bids.values().size();
			sum += g1BidsLeft.values().size();
		}
		return sum;
	}

	private int numGroup2Bids() {
		int sum = 0;
		synchronized (this) {
			sum += g2Bids.values().size();
			sum += g2BidsLeft.values().size();
		}
		return sum;
	}

	public void tick(double simTime, double tickLength, int tick) {
		double sinceLastStateChange = simTime - lastStateChangeAt;

		// if (state.equals("all_red") && sinceLastStateChange > deadTimeDur) {
		// state = "lefts_green1";
		// lastStateChangeAt = simTime;
		// } else if (state.equals("lefts_green1")
		// && sinceLastStateChange > leftsGreenDur) {
		// state = "lefts_yellow1";
		// lastStateChangeAt = simTime;
		// } else if (state.equals("lefts_yellow1")
		// && sinceLastStateChange > leftsYellowDur) {
		// state = "throughs_green1";
		// lastStateChangeAt = simTime;
		// } else if (state.equals("throughs_green1")) {
		// if ((group1Bids() < group2Bids() && sinceLastStateChange >
		// minThroughsGreenDur)
		// || (sinceLastStateChange > throughsGreenDur)) {
		// state = "throughs_yellow1";
		// lastStateChangeAt = simTime;
		// }
		// } else if (state.equals("throughs_yellow1")
		// && sinceLastStateChange > throughsYellowDur) {
		// state = "lefts_green2";
		// lastStateChangeAt = simTime;
		// } else if (state.equals("lefts_green2")
		// && sinceLastStateChange > leftsGreenDur) {
		// state = "lefts_yellow2";
		// lastStateChangeAt = simTime;
		// } else if (state.equals("lefts_yellow2")
		// && sinceLastStateChange > leftsYellowDur) {
		// state = "throughs_green2";
		// lastStateChangeAt = simTime;
		// } else if (state.equals("throughs_green2")) {
		// if ((group1Bids() > group2Bids() && sinceLastStateChange >
		// minThroughsGreenDur)
		// || (sinceLastStateChange > throughsGreenDur)) {
		// state = "throughs_yellow2";
		// lastStateChangeAt = simTime;
		// }
		// } else if (state.equals("throughs_yellow2")
		// && sinceLastStateChange > throughsYellowDur) {
		// state = "all_red";
		// lastStateChangeAt = simTime;
		// }

		if (state.equals("all_red") && sinceLastStateChange > deadTimeDur) {
			if (group1Bids() >= group2Bids()) {
				if (g1BidsLeft.size() > 0)
					state = "lefts_green1";
				else
					state = "throughs_green1";
			} else {
				if (g2BidsLeft.size() > 0)
					state = "lefts_green2";
				else
					state = "throughs_green2";
			}
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
			if ((group1Bids() < group2Bids() && sinceLastStateChange > minThroughsGreenDur)
					|| (numGroup2Bids() > 0 && sinceLastStateChange > throughsGreenDur)) {
				state = "throughs_yellow1";
				lastStateChangeAt = simTime;
			} else if (g1BidsLeft.size() > 0
					&& sinceLastStateChange > throughsGreenDur) {
				state = "lefts_green1";
				lastStateChangeAt = simTime;
			}
		} else if (state.equals("throughs_yellow1")
				&& sinceLastStateChange > throughsYellowDur) {
			if (g2BidsLeft.size() > 0)
				state = "lefts_green2";
			else
				state = "throughs_green2";
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
			if ((group1Bids() > group2Bids() && sinceLastStateChange > minThroughsGreenDur)
					|| (numGroup1Bids() > 0 && sinceLastStateChange > throughsGreenDur)) {
				state = "throughs_yellow2";
				lastStateChangeAt = simTime;
			} else if (g2BidsLeft.size() > 0
					&& sinceLastStateChange > throughsGreenDur) {
				state = "lefts_green2";
				lastStateChangeAt = simTime;
			}
		} else if (state.equals("throughs_yellow2")
				&& sinceLastStateChange > throughsYellowDur) {
			state = "all_red";
			lastStateChangeAt = simTime;
		}
	}
}
