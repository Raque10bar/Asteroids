package aoop.asteroids.server;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;

public class Server extends UDPEntity {
	
	public final static int DEFAULT_SERVER_PORT = 5555;
	private CopyOnWriteArrayList<Address> addressMap = new CopyOnWriteArrayList<Address>();
	private Game game;
	private ServerInputHandler inputThread;
	private ServerOutputHandler outputThread;
	
	public Server(Game g) {
		super(DEFAULT_SERVER_PORT);
		this.game = g;
		init();
	}
    
	public synchronized void addAddress(Address a) {
		if (!addressMap.contains(a)) {
			addressMap.add(a);
		}
	}
	
	public synchronized void removeAddress(Address a) {
		if (addressMap.contains(a)) {
			addressMap.remove(a);
		}
	}
	
	public CopyOnWriteArrayList<Address> getAddresses() {
		return addressMap;
	}
	
	public Game getGame() {
		return game;
	}


	public void setModel(Spaceship s, ArrayList<Bullet> bulls) {
		game.setModelServer(s, bulls);
	}

	@Override
	protected void closeConnections() {
		inputThread.interrupt();
		outputThread.interrupt();
	}
	
	public void removeShip(Address a) {
		this.game.removeShip(a);
	}

	@Override
	public void init() {
		game.setShipAddress(address);
		inputThread = new ServerInputHandler(this);
		inputThread.start();
		outputThread = new ServerOutputHandler(this);
		outputThread.start();
	}
	
	public void saveScore() {
		this.game.saveMultiScore();
	}
}
