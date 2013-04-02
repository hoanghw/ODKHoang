package org.odk.collect.android.activities;

public class GeoFence {
	double lat;
	double lon;
	int rad;
	double top;
	double bot;
	double right;
	double left;
	final static double METER_TO_DEGREE = 111000;
	
	public GeoFence(double latitude, double longitude, int radius){
		this.lat=latitude;
		this.lon=longitude;
		this.rad=radius;
		double toDegree = radius/METER_TO_DEGREE;
		this.top= latitude + toDegree;
		this.bot= latitude - toDegree;
		this.right= longitude + toDegree;
		this.left= longitude - toDegree;
	}
	
	public double getLatitude(){
		return this.lat;
	}
	public double getLongitude(){
		return this.lon;
	}
	public int getRadius(){
		return this.rad;
	}
	
	public boolean isInGeoFence(double latitude, double longitude){
		return (latitude<top)&&(latitude>bot)&&(longitude>left)&&(longitude<right);
	}
}
