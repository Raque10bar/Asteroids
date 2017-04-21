package aoop.asteroids.server;

import java.net.DatagramSocket;

public abstract class ConnectionOutputHandler extends ConnectionHandler {

	protected int packetCounter;
	
	public ConnectionOutputHandler(DatagramSocket s, UDPEntity e) {
		super(s, e);
	}

	@Override
	public void run() {
		long executionTime, sleepTime;
		packetCounter = 0;
		while (!this.isInterrupted() && !isOver()) {
			executionTime = System.currentTimeMillis ();
			sendModel();
			packetCounter++;
			executionTime -= System.currentTimeMillis ();
			sleepTime = 40 - executionTime;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				break;
			}
		}
		sendEnd();
	}
	
	protected abstract boolean isOver();
	
	@Override
	protected void readPacket() {
		
	}
	
	protected abstract void waitConnection();
	protected abstract void sendModel();
	protected abstract void sendEnd();

	
}
