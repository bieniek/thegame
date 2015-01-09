package com.epam.search.thegame;

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
		double x = GameUtils.moveX(getPosition(), distance, direction);
		double y = GameUtils.moveY(getPosition(), distance, direction);

		if (x >= Game.MAX_X - 2 || y >= Game.MAX_Y - 2 || x < 0 || y < 0) {
			direction += GameUtils.randDirection();
		} else {
			setPosition(new Point(x, y));
		}
	}

	public void escape(int distance) {
		direction = GameUtils.oppositeDirection(direction);
		move(distance);
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}



}
