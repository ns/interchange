package org.uci.luci.interchange;

public class MercatorProjection implements Projection {
	public double[] convert(double lat, double lon) {
		return convert(lat, lon, earthRadiusKm);
	}

	public double[] convert(double lat, double lng, double radius) {
		double[] coords = {(lng - Global.minLon) * radius,Math.log(Math.tan(Math.PI / 4 + lat / 2)) * radius};
		return coords;
	}
	
	public double convertLongToX(double lng)
	{
		return (lng - Global.minLon) * earthRadiusKm;
	}
	
	public double convertLatToY(double lat)
	{
		return Math.log(Math.tan(Math.PI / 4 + lat / 2)) * earthRadiusKm;
	}
}
