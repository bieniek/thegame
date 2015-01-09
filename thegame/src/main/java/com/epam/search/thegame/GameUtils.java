package com.epam.search.thegame;

import java.util.Random;

public class GameUtils {
	public static int randDirection() {
		return (int)Math.round(((new Random().nextInt(361) + 1)*Math.PI)/180);
	}

	public static int oppositeDirection(int direction) {
		return (int)(direction + Math.PI);
	}

	public static double moveX(Point position, int distance, int direction) {
		return Math.round(new Integer(distance).doubleValue() * Math.cos(new Integer(direction).doubleValue())) +
				position.getX();
	}

	public static double moveY(Point position, int distance, int direction) {
		return Math.round(new Integer(distance).doubleValue() * Math.sin(new Integer(direction).doubleValue())) +
				position.getY();
	}
}
