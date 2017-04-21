package aoop.asteroids.server;

import java.net.SocketException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Spaceship;

public abstract class Client extends UDPEntity {

	private static final int DEFAULT_TIMEOUT = 1000;
	
	protected Address serverAddress;
	protected AsteroidsFrame asteroidsFrame;
	protected ConnectionHandler inputThread;
	
	protected Client(Address sa, AsteroidsFrame af) {
		super();
		this.serverAddress = sa;
		this.asteroidsFrame = af;
		initSocket();
	}
	
	public Address getServerAddress() {
    	return serverAddress;
    }
	protected abstract void setMultiplayer();
    public AsteroidsFrame getFrame() {
    	return asteroidsFrame;
    }
    
	private void initSocket() {
		try {
			this.socket.setSoTimeout(DEFAULT_TIMEOUT);
		} catch (SocketException e) {
			ExceptionPrinter.print("Error while tryig to set a timeout on the client socket", e);
		}
	}
	
	abstract void resume();
	abstract void setModel(Collection<Asteroid> asts, Collection<Bullet> bulls, CopyOnWriteArrayList <Spaceship> ships);
}
