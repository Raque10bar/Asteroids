package aoop.asteroids.server;

import java.io.IOException;
import java.net.DatagramSocket;

public class JoinerOutputHandler extends ConnectionOutputHandler {
	
	private Joiner joiner;
	
	public JoinerOutputHandler(DatagramSocket s, Joiner j) {
		super(j.getSocket(), j);
		joiner = j;
	}
	
    public synchronized void sendModel() {
        sendPacket(MODEL, joiner.getServerAddress());
    }
    
    @Override
	protected void writePacket() {
		try {
			out.writeInt(packetCounter);
			out.writeObject(joiner.getGame().getPlayer());
			out.writeObject(joiner.getGame().getPlayerBullets());
		} catch (IOException e) {
			ExceptionPrinter.print("Error while trying to write the model to the stream", e);
		}
	}
    
    public synchronized void sendEnd() {
    	if (!isOver()) {
    		sendPacket(END, joiner.getServerAddress());
    	}
    }

	@Override
	protected void readPacket() {
	}

	@Override
	protected void waitConnection() {
    	boolean wait = true;
    	int request;
    	while (wait && !this.isInterrupted()) {
    		do {
    			sendPacket(SPECTATE_REQUEST, joiner.serverAddress);
    			request = receivePacket();
    		} while (request == ERROR);	
			wait = false;
    	}
    }

	@Override
	protected boolean isOver() {
		return this.joiner.asteroidsFrame.getGame().gameOver();
	}
}
