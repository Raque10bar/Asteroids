package aoop.asteroids.objectdb;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.Query;

public class ObjectDB {

	private static EntityManagerFactory emf;
	private static EntityManager em;
	public static final int SINGLE_PLAYER = 1;
	public static final int MULTI_PLAYER = 2;

	private static void openConnection() {
		emf = Persistence.createEntityManagerFactory("$objectdb/db/highscores.odb");
		em = emf.createEntityManager();
	}
	
	private static void closeConnection() {
		em.close();
		emf.close();
	}

	public static ArrayList<HighScoreEntry> getSinglePlayerHighscores() {
		return ObjectDB.getHighscores(ObjectDB.SINGLE_PLAYER);
	}
	
	public static ArrayList<HighScoreEntry> getMultiPlayerHighscores() {
		return ObjectDB.getHighscores(ObjectDB.MULTI_PLAYER);
	}

	public static ArrayList<HighScoreEntry> getHighscores(int mode) {
		openConnection();
		// Store 1000 Point objects in the database:
		em.getTransaction().begin();
		// Retrieve all the Point objects from the database:
		TypedQuery<HighScoreEntry> query = em.createQuery("SELECT e FROM HighScoreEntry e WHERE e.mode = :mode  ORDER BY e.score DESC", HighScoreEntry.class);
		query.setParameter("mode", mode);
		ArrayList<HighScoreEntry> results = (ArrayList<HighScoreEntry>) query.getResultList();
		
		// Close the database connection:
		closeConnection();
		return results;
	}

	public static boolean storeHighscore(String name, int score, int mode) {
		openConnection();
		em.getTransaction().begin();

		HighScoreEntry entry = new HighScoreEntry(name, score, mode);
		em.persist(entry);
		em.getTransaction().commit();
		closeConnection();
		return true;
	}

	public static boolean deleteAllHighscores() {
		openConnection();
		em.getTransaction().begin();
		Query q1 = em.createQuery("DELETE FROM HighScoreEntry");
		q1.executeUpdate();
		em.getTransaction().commit();
		closeConnection();
		return true;
	}
}
