package com.epam.search.thegame;

import java.util.Random;

import org.apache.solr.client.solrj.beans.Field;

public class UFO {
	@Field
	private String id;

	@Field
	private String name;

	@Field
	private String position;

	@Field
	private int direction;

	@Field
	private boolean collision;

	public UFO() {}

	public UFO(String id, String name, Point position, int direction) {
		this.id = id;
		this.name = name;
		this.position = position.toString();
		this.direction = direction;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getPosition() {
		return new Point(this.position.split(","));
	}

	public void setPosition(Point position) {
		this.position = position.toString();
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "UFO [id=" + id + ", name=" + name + ", position=" + position + ", direction=" + direction + "]";
	}

	public void move(int distance) {
		double x =
				Math.round(new Integer(distance).doubleValue() * Math.cos(new Integer(direction).doubleValue())) +
				getPosition().getX();
		double y =
				Math.round(new Integer(distance).doubleValue() * Math.sin(new Integer(direction).doubleValue())) +
				getPosition().getY();

		if (x >= Game.MAX_X - 2 || y >= Game.MAX_Y - 2 || x < 0 || y < 0) {
			int rad = (new Random().nextInt(361) + 1);
			direction = (direction + rad)%360;
		} else {
			Point newPosition = new Point(x, y);
			setPosition(newPosition);
		}
	}

	public void escape(int distance ) {
		direction = (direction + 180)%360;
		move(distance);
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}



}
