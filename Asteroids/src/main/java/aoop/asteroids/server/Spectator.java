package aoop.asteroids.server;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Spaceship;

public class Spectator extends Client {
	
    public Spectator(Address sa, AsteroidsFrame af) {
    	super(sa, af);
    }

	@Override
	protected void closeConnections() {
		inputThread.interrupt();
		asteroidsFrame.finishWait();
	}

	@Override
	public void init() {
		inputThread = new ClientInputHandler(this.getSocket(), this);
		inputThread.start();
	}

	@Override
	void setModel(Collection<Asteroid> asts, Collection<Bullet> bulls, CopyOnWriteArrayList<Spaceship> ss) {
		asteroidsFrame.getGame().setModel(asts, bulls, ss);	
	}

	@Override
	void resume() {
	}

	@Override
	protected void setMultiplayer() {
	}

}
