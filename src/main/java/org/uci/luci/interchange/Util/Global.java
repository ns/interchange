package org.uci.luci.interchange.Util;

import org.uci.luci.interchange.OSM.*;
import org.uci.luci.interchange.UI.AppWindow;
import org.uci.luci.interchange.Simulator;

public class Global {
	public static OpenStreetMap openStreetMap;
	public static Simulator simulator;
	public static AppWindow window;

	public static Projection projection = new MercatorProjection();
	public static double maxLat;
	public static double minLat;
	public static double maxLon;
	public static double minLon;
	// public static boolean runSim = true;
}