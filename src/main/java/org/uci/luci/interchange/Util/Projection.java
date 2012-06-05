package org.uci.luci.interchange.Util;

public interface Projection {
	double earthRadiusKm = 6378.1;
	double earthRadiusMi = 3958.75;

	abstract double convertLongToX(double lng);

	abstract double convertLatToY(double lat);

}