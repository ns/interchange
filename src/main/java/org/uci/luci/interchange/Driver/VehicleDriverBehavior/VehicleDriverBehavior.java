package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

public interface VehicleDriverBehavior {
	public void tick(double simTime, double tickLength);

	public String state();
}