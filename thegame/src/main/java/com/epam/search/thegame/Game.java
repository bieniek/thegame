package com.epam.search.thegame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

public class Game {
	public static final int MAX_X = 42;

	public static final int MAX_Y = 102;

	private static final int UFOS_NUMBER = 50;

	private static final int DISTANCE = 1;

	private JTextArea board;

	public static void main(String[] args) throws Exception {
		final SolrServer server = new HttpSolrServer("http://localhost:8983/solr");

		server.deleteByQuery("*:*");
		server.commit();

		List<UFO> ufos = generate();

		server.addBeans(ufos);
		server.commit();

		final Game g = new Game();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				g.createAndShowGui();

				GameLoop loop = g.new GameLoop(server, g.board);
				(new Thread(loop)).start();
			}
		});
	}

	private class GameLoop implements Runnable {
		private final SolrQuery allQuery;

		private SolrServer server;

		private JTextArea board;

		public GameLoop(SolrServer server, JTextArea board) {
			this.server = server;
			this.board = board;
			this.allQuery = new SolrQuery("*:*").setRows(UFOS_NUMBER);
		}

		public void run() {
			try {
				while (true) {
					QueryResponse response = server.query(allQuery);
					List<UFO> beans = response.getBeans(UFO.class);
					Iterator<UFO> it = beans.iterator();
					while (it.hasNext()) {
						UFO ufo = it.next();
						if (!ufo.isCollision()) {
							ufo.move(1);
							List<UFO> walls = getWalls(ufo);
							if (!walls.isEmpty()) {
								ufo.escape(DISTANCE);
							} else {
								// find colision points
								List<UFO> collisions = getCollisions(ufo);
								if (!collisions.isEmpty()) {
									ufo.setCollision(true);
									server.addBean(ufo);
									server.commit();
								}
							}
						}
					}
					server.addBeans(beans);
					server.commit();
					GameUtils.drawGameBoard(board, beans);
					Thread.sleep(10L);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private List<UFO> getCollisions(UFO ufo) throws Exception {
			SolrQuery query = new SolrQuery("-id:" + ufo.getId());
			query.addFilterQuery(createCollistionFilterQuery(ufo));
			QueryResponse response = server.query(query);
			List<UFO> resp = response.getBeans(UFO.class);
			return resp;
		}

		private List<UFO> getWalls(UFO ufo) throws Exception {
			SolrQuery query = new SolrQuery("-id:" + ufo.getId());
			query.addFilterQuery("collision:true");
			query.addFilterQuery(createCollistionFilterQuery(ufo));
			QueryResponse response = server.query(query);
			List<UFO> resp = response.getBeans(UFO.class);
			return resp;
		}

		private String createCollistionFilterQuery(UFO ufo) {
			return String.format("{!bbox pt=%f,%f sfield=position d=2}",
					ufo.getPosition().getX(),
					ufo.getPosition().getY());
		}
	}

	private static List<UFO> generate() {
		List<UFO> ufos = new ArrayList<UFO>();
		for (int i = 0; i < UFOS_NUMBER; i++) {
			int dir = GameUtils.randDirection();
			int x = new Random().nextInt(MAX_X) + 1;
			int y = new Random().nextInt(MAX_Y) + 1;
			ufos.add(new UFO("" + i, "ufo" + i, new Point(x, y), dir));
		}
		return ufos;
	}

	private void createAndShowGui() {
		JFrame frame = new JFrame("The game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		board = new JTextArea(Game.MAX_X, Game.MAX_Y);
		board.setFont(new Font("monospaced", Font.PLAIN, 12));
		panel.add(board);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
