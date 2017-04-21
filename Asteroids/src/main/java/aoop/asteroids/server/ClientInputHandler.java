package aoop.asteroids.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Spaceship;

public class ClientInputHandler extends ConnectionHandler {

	protected Client client;
	protected int lastPacketNum;
	
	public ClientInputHandler(DatagramSocket s, Client c) {
		super(s, c);
		client = c;
		lastPacketNum = -1;
	}

	@Override
	public void run() {
		waitForServer();
		int request;
		
		while (!this.isInterrupted()) {
			request = receivePacket();
			if (request == END) {
				waitForServer();
			} else if (request == MODEL) {
				readPacket();
			} else if (request == WAIT) {
				waitForGame();
			}
		}
		sendToServer(QUIT_SPECTATE_REQUEST);
	}

	@SuppressWarnings("unchecked")
	protected void readPacket() {
		int newPacketNum;
		try {
			newPacketNum = in.readInt();
			if (newPacketNum > lastPacketNum) {
				client.setModel((Collection<Asteroid>) in.readObject(), 
						(Collection<Bullet>) in.readObject(), 
						(CopyOnWriteArrayList<Spaceship>) in.readObject());
			}
		} catch (IOException e) {
			ExceptionPrinter.print("Error while trying to read the model from the stream", e);
		} catch (ClassNotFoundException e) {
			ExceptionPrinter.print("Error while trying to cast the objects of the model", e);
		}
	}
	
	protected void sendToServer(int request) {
		sendPacket(request, this.client.getServerAddress());
	}
	
	protected void writePacket() {};
	
	public void waitForServer() {
    	boolean wait = true;
    	client.getFrame().startWait();
    	int request;
    	while (wait && !this.isInterrupted()) {
    		do {
    			sendToServer(SPECTATE_REQUEST);
    			request = receivePacket();
    		} while (request == ERROR);	
    		client.getFrame().finishWait();
    		client.resume();
			wait = false;
    	}
    }
	
	public void waitForGame() {
    	boolean wait = true;
    	this.client.asteroidsFrame.getGame().abort();
    	int request;
    	while (wait && !this.isInterrupted()) {
    		do {
    			request = receivePacket();
    		} while (request == ERROR);	
    		client.resume();
			wait = false;
    	}
    }
	
}
