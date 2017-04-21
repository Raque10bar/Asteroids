package aoop.asteroids.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class UDPEntity {
	
	private static final int DEFAULT_PORT = 0;
	
	protected DatagramSocket socket;
	protected Address address;
	
	public UDPEntity() {
		this(DEFAULT_PORT);
	}

	public UDPEntity(int port) {

		InetAddress valid = Address.getValidIP();
		try {
			socket = new DatagramSocket(port, valid);
			address = new Address(valid.getHostAddress(), socket.getLocalPort());
		} catch (SocketException e) {
			ExceptionPrinter.print("Error while trying to create a socket in address: " + address, e);
		}
	}
	
	public abstract void init();
	
	protected DatagramSocket getSocket() {
		return socket;
	}
	
	public Address getAddress(){
		return this.address;
	}
	
	public void finish() {
		closeConnections();
		socket.close();
	}
	
	protected abstract void closeConnections();
}
