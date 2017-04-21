package aoop.asteroids.server;

import java.io.IOException;

public class ServerOutputHandler extends ConnectionOutputHandler {
	
	private Server server;
	
	public ServerOutputHandler(Server s) {
		super(s.getSocket(), s);
		server = s;
	}
	
    protected void sendModel() {
    	for (Address client : server.getAddresses()) {
    		sendPacket(MODEL, client);
    	}
    }
    
    protected void sendEnd() {
		for (Address client : server.getAddresses()) {
			if (isOver()) {
				sendPacket(WAIT, client);
			} else {
        		sendPacket(END, client);
			}
    	}	
    }

	@Override
	protected void waitConnection() {
	}

	@Override
	protected void writePacket() {
		try {
			out.writeInt(packetCounter);
			out.writeObject(server.getGame().getAsteroids());
			out.writeObject(server.getGame().getBullets());
			out.writeObject(server.getGame().getPlayers());
		} catch (IOException e) {
			ExceptionPrinter.print("Error while trying to write the model to the stream", e);
		}
	}

	@Override
	protected boolean isOver() {
		return this.server.getGame().gameOver();
	}
}
