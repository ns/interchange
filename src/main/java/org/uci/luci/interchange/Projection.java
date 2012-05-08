package org.uci.luci.interchange;

public interface Projection {

	double earthRadiusKm = 6378.1;
	double earthRadiusMi = 3958.75;
		
//	abstract double[] convert(double lat, double lng, double radius);
//	abstract double[] convert(double lat, double lng);


	abstract double convertLongToX(double lng);
	abstract double convertLatToY(double lat);
	
	
}