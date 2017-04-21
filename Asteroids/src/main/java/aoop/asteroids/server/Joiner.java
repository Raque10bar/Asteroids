package aoop.asteroids.server;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;

public class Joiner extends Client {
	
	private JoinerOutputHandler outputThread;
	
    public Joiner(Address sa, AsteroidsFrame af) {
    	super(sa, af);
    }
    
	public void interrupt() {
		inputThread.interrupt();
		outputThread.interrupt();
		this.socket.disconnect();
		this.socket.close();
		asteroidsFrame.finishWait();
	}

	@Override
	public void init() {
		asteroidsFrame.getGame().setShipAddress(address);
		inputThread = new ClientInputHandler(this.getSocket(), this); 
		inputThread.start();
	}
	
	public Game getGame() {
		return this.asteroidsFrame.getGame();
	}

	@Override
	protected void closeConnections() {
		inputThread.interrupt();
		outputThread.interrupt();
		asteroidsFrame.finishWait();
	}

	@Override
	void setModel(Collection<Asteroid> asts, Collection<Bullet> bulls, CopyOnWriteArrayList<Spaceship> ss) {
		Game g = asteroidsFrame.getGame();
		g.setModelJoiner(asts, bulls, ss);
	}

	protected void resume() {
		this.asteroidsFrame.getGame().initGameData();
		new Thread(this.asteroidsFrame.getGame()).start();
		System.out.println(this.asteroidsFrame.getGame().getMultiplePlayers());
		outputThread = new JoinerOutputHandler(this.getSocket(), this);
		outputThread.start();
	}

	@Override
	protected void setMultiplayer() {
		this.asteroidsFrame.getGame().setMultiplePlayers();	
	}
	
	public void saveScore() {
		this.asteroidsFrame.getGame().saveMultiScore();
	}
}

