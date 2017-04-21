package aoop.asteroids.server;

import java.io.IOException;
import java.util.ArrayList;

import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Spaceship;

public class ServerInputHandler extends ConnectionHandler {
	
	private Server server;
	
	public ServerInputHandler(Server s) {
		super(s.getSocket(), s);
		server = s;
	}
	
	@Override
	public void run() {
		Address packetAddress;
		int request;
		
		while (!this.isInterrupted() && !server.getGame().gameOver()) {
			request = receivePacket();
			packetAddress = getSenderAddress();
			
			switch (request) {
				case SPECTATE_REQUEST:
					server.addAddress(packetAddress);
					sendPacket(OK, packetAddress);
					break;
				case QUIT_SPECTATE_REQUEST:
					server.removeAddress(packetAddress);
					break;
				case MODEL:
					readPacket();
					server.getGame().setMultiplePlayers();
					break;
				case GAMEOVER:
					server.removeShip(packetAddress);
					break;
				case END:
					server.removeAddress(packetAddress);
					server.removeShip(packetAddress);
					break;
				default:
					System.out.println("Error");
			}
		}	
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void readPacket() {
		try {
			in.readInt();
			server.setModel((Spaceship) in.readObject(), (ArrayList <Bullet>) in.readObject());
		} catch (ClassNotFoundException e) {
			ExceptionPrinter.print("Error while trying to cast the objects of the model", e);
		} catch (IOException e) {
			ExceptionPrinter.print("Error while trying to read the model from the stream", e);
		}
		
	}

	@Override
	protected void writePacket() {}
	
}
