package com.epam.search.thegame;

import java.util.List;
import java.util.Random;

import javax.swing.JTextArea;

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

	public static void drawGameBoard(JTextArea board, List<UFO> beans) {
		StringBuffer gameBoard = new StringBuffer();
		for (int i = 0; i < Game.MAX_X; i++) {
			for (int j = 0; j < Game.MAX_Y; j++) {
				if (i == 0 || i == Game.MAX_X - 1 || j == 0 || j == Game.MAX_Y - 1) {
					gameBoard.append("x");
				} else {
					boolean drawn = false;
					for (UFO ufo : beans) {
						Point position = ufo.getPosition();
						if ((position.getX() + 1 == i && position.getY() + 1 == j)) {
							if (ufo.isCollision()) {
								gameBoard.append("|");
							} else {
								gameBoard.append("*");
							}
							drawn = true;
							break;
						}
					}
					if (!drawn) {
						gameBoard.append(" ");
					}
				}
			}
			gameBoard.append("\n");
		}
		board.setText(gameBoard.toString());
	}
}
