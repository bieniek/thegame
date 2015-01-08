package com.epam.search.thegame;

public class Point {
	private double x;

	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(String[] solrCords) {
		this.x = Double.parseDouble(solrCords[0]);
		this.y = Double.parseDouble(solrCords[1]);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return x + "," + y;
	}
}
