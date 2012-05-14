package org.uci.luci.interchange;

public class Global {
  public static OpenStreetMap openStreetMap;
  public static Simulator simulator;
  
  public static Projection projection = new MercatorProjection();
  public static double maxLat;
  public static double minLat;
  public static double maxLon;
  public static double minLon;
  //public static boolean runSim = true;
}