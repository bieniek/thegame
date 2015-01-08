package com.epam.search.thegame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

public class Game {
	public static final int MAX_X = 42;

	public static final int MAX_Y = 102;

	private static final int UFOS_NUMBER = 50;

	private static final int DISTANCE = 1;

	public static void main(String[] args) throws Exception {
		SolrServer server = new HttpSolrServer("http://localhost:8983/solr");

		server.deleteByQuery("*:*");
		server.commit();

		List<UFO> ufos = new ArrayList<UFO>();
		for (int i = 0; i < UFOS_NUMBER; i++) {
			int dir = new Random().nextInt(361) + 1;
			int x = new Random().nextInt(MAX_X) + 1;
			int y = new Random().nextInt(MAX_Y) + 1;
			ufos.add(new UFO("" + i, "ufo" + i, new Point(x, y), dir));
		}
		server.addBeans(ufos);
		server.commit();

		Game g = new Game();
		GameLoop loop = g.new GameLoop(server);
		(new Thread(loop)).start();

	}

	private class GameLoop implements Runnable {
		private final SolrQuery allQuery;

		private SolrServer server;

		public GameLoop(SolrServer server) {
			this.server = server;
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
								//find colision points
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
					draw(beans);
					//Thread.sleep(10L);
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
			return "{!bbox pt=" + ufo.getPosition().getX() + "," + ufo.getPosition().getY() + " sfield=position d=2}";
		}

		private void draw(List<UFO> beans) {
			System.out.print("\n");
			for (int i = 0; i < MAX_X; i++) {
				for (int j = 0; j < MAX_Y; j++) {
					if (i == 0 || i == MAX_X - 1 || j == 0 || j == MAX_Y - 1) {
						System.out.print("x");
					} else {
						boolean draw = false;
						for (UFO ufo : beans) {
							Point position = ufo.getPosition();
							if ((position.getX() + 1 == i && position.getY() + 1 == j)) {
								if (ufo.isCollision()) {
									System.out.print("|");
								} else {
									System.out.print("*");
								}
								draw = true;
								break;
							}
						}
						if (!draw) {
							System.out.print(" ");
						}
					}
				}
				System.out.print("\n");
			}
		}
	}
}
