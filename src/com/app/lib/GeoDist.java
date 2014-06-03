package com.app.lib;

import java.lang.Math;

public class GeoDist {
	
	private class CartesianCoordinate {

	    private double x; private double y; private double z;
		
	    public CartesianCoordinate(double x, double y, double z){
	    	this.x = x; this.y = y; this.z = z;
	    }	    
		public double getX() { return x; }
		public double getY() { return y; }
		public double getZ() { return z; }
	}
	
	private static final double R = 6367000; 
	
	
	private static final double METER2MILE = 0.0006214;
	
	public GeoDist() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Calculates the surface distance between 2 GPS coorindates
	 * @param line_dist
	 * @return 
	 */
	public double getSurfaceDistance(GpsPoint p1, GpsPoint p2){
		
		CartesianCoordinate c1 = getCartCoord(p1);
		CartesianCoordinate c2 = getCartCoord(p2);
		double line_dist = getLineDistance(c1, c2);
		
		
		double result = (2*R * (Math.asin(line_dist/(2*R))));
		
		return meterToMile(result);
	}
	
	public double roundDist(double num_to_round, int num_decimals){
		double result = num_to_round;
		double factor =  Math.pow(10, num_decimals);
		result = result * factor;
		result = Math.round(result);
		result = result/factor;				
		return result;
	}
	
	/**
	 * Checks whether the 2 GPS Coordinates are within a certain radius
	 * of each other.
	 * @param p1 gps point 1
	 * @param p2 gps point 2
	 * @param radius radius (in miles) within which to check
	 * @return true if the 2 points are within radius, false otherwise
	 */
	public boolean inRange(GpsPoint p1, GpsPoint p2, double radius){
		
		return (getSurfaceDistance(p1,p2) <= radius);
	}

	/**
	 * Converts distance from meter to miles
	 * @param meters
	 * @return miles
	 */
	private double meterToMile(double meters){
		return meters*METER2MILE;
	}
	
	private CartesianCoordinate getCartCoord(GpsPoint p){
		
		// get angles in degrees from lat/lon
		double phi = getPhi(p.getLat());
		double theta = getTheta(p.getLon());
		
		// convert angles to radian measures
		phi = getRadians(phi);
		theta = getRadians(theta);
		
		// get Cartesian coordinates
		double x = R * Math.cos(theta) * Math.sin(phi);
		double y = R * Math.sin(theta) * Math.sin(phi);
		double z = R * Math.cos(phi);
		CartesianCoordinate c = new CartesianCoordinate(x,y,z);
		
				
		//System.out.println("Cart Coord: ");
		
		//c.print();
		
		return c;
		
	}
	
	/**
	 * calculates the Euclidean distance between 2 points in the 3d cartesian coordinate
	 * system
	 * @param c1
	 * @param c2
	 * @return
	 */
	private double getLineDistance(CartesianCoordinate c1, CartesianCoordinate c2){
		
		double delta_x = c1.getX() - c2.getX();
		double delta_y = c1.getY() - c2.getY();
		double delta_z = c1.getZ() - c2.getZ();
				
		double line_dist =  Math.sqrt( 
								Math.pow(delta_x, 2) + 
								Math.pow(delta_y, 2) + 
								Math.pow(delta_z, 2) );
		
		//System.out.println("line distance: "+line_dist);
		
		return line_dist;
	}
	
	
	/**
	 * Converts lat in degrees into radians
	 * @param lat
	 * @return
	 */
	private double getPhi(double lat){
		
		double phi = 0;
		
		if(lat > 0){
			phi = 90 - lat;
		}else{
			phi = 90 + lat;
		}
		
		
		return phi;
	}
	
	/**
	 * Converts longitude in degrees into radian
	 * @param lon
	 * @return
	 */
	private double getTheta(double lon){
		
		
		return lon;	
	}
	
	private double getRadians(double angle_in_degrees){
		
		double angle_in_radians = (angle_in_degrees * 2 * Math.PI)/360;
		
		
		return angle_in_radians;
		
	}
	
}
