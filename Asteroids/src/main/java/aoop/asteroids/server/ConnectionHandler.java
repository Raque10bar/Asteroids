package aoop.asteroids.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public abstract class ConnectionHandler extends Thread {

	protected ObjectOutputStream out = null;
	protected ByteArrayOutputStream bout = null;
	protected ObjectInputStream in = null;
	protected ByteArrayInputStream bin = null;
    protected DatagramPacket receivePacket = null;
    protected DatagramSocket socket;
    
    public static final int ERROR = -1;
    public static final int OK = 0;
    public final int SPECTATE_REQUEST = 1;
	public final int QUIT_SPECTATE_REQUEST = 2;
	public static final int END = 3;
	public static final int MODEL = 4;
	public static final int GAMEOVER = 5;
	public static final int WAIT = 6;
	
	protected UDPEntity entity;
	
    public ConnectionHandler (DatagramSocket s, UDPEntity e) {
    	socket = s;
    	entity = e;
    }
    
    protected synchronized int receivePacket() {
    	int request = ERROR;
		try {
			receivePacket = new DatagramPacket(new byte[4096], 4096);
			socket.receive(receivePacket);
			prepareInputStream();
			request = in.readInt();
		} catch (SocketTimeoutException e) {
			ExceptionPrinter.print("No packet was received in this time", e);
		} catch (IOException e) {
			ExceptionPrinter.print("Error trying to receive a message", e);
		}
		return request;
    }
	
	protected synchronized void sendPacket(int request, Address address) {
		DatagramPacket sendPacket;
		try {
            prepareOutputStream();
            out.writeInt(request);
            writePacket();
            out.flush();
            sendPacket = new DatagramPacket(bout.toByteArray(), bout.toByteArray().length, address.toSocketAddress());
            socket.send(sendPacket);
        } catch (IOException e) {
        	ExceptionPrinter.print("Error while trying to send a packet.", e);
        }
    }
	
	protected void prepareInputStream() {
        try {
            bin = new ByteArrayInputStream(receivePacket.getData());
            in = new ObjectInputStream(bin);
        } catch (IOException e) {
        	ExceptionPrinter.print("Error while trying to read from the stream.", e);
        }
    }

	protected void prepareOutputStream() {
        try {
        	bout = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bout);
		} catch (IOException e) {
			ExceptionPrinter.print("Error while trying to wite to the stream.", e);
		}
    }

	protected Address getSenderAddress() {
		if(receivePacket.getAddress() == null){
			return null;
		} else {
			return new Address(receivePacket.getAddress().getHostAddress(), receivePacket.getPort());
		}
    }
	
	abstract protected void readPacket();
	abstract protected void writePacket();
}
