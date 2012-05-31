package org.uci.luci.interchange.Factory;

import org.uci.luci.interchange.OSM.*;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.UI.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.*;

public class SimulationFactory {
	public static void runSimulation(String mapFile, int simulationSpeed,
			String intersectionType) throws Exception {
		OsmFileReader osmFileReader = new OsmFileReader(mapFile);
		osmFileReader.parseStructure();
		OpenStreetMap openStreetMap = osmFileReader.osmHandler.openStreetMap;

		Global.openStreetMap = openStreetMap;
		Global.openStreetMap.purgeUnconnectedNodes();
		Global.openStreetMap.removeDisconnectedGraphs();
		Global.openStreetMap.precomputeNeighborDistances();
		Global.openStreetMap.projectUsingProjection(new MercatorProjection());

		if (intersectionType.equals("Bidding"))
			IntersectionRegistry.generateBiddingIntersections();
		else
			IntersectionRegistry.generateTraditionalIntersections();

		AppWindow appWindow = new AppWindow();

		Simulator simulator = new Simulator();
		simulator.setSpeed(simulationSpeed);
		Global.simulator = simulator;
		simulator.start();
	}
}