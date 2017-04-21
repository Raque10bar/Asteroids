package aoop.asteroids.server;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Address implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String ipAddress;
	private final int port;
	
	public Address(int port) {
		this(Address.getValidIP().getHostAddress(), port);
	}	
	
	public Address(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public final String getStAddress(){
		return ipAddress;
	}
	
	public final InetAddress getInetAddress(){
		InetAddress address = null;
		try {
			address = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	public final int getPort() {
		return port;
	}
	
	/**
	 * Check if the address and port of a connection c are the same of this one
	 * @param c The connection to check
	 * @return true if they have the same address and port, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Address) {
			Address c = (Address) o;
			return (this.ipAddress.equals(c.getStAddress())) && (this.port == c.getPort());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "{" + this.getStAddress() + "-" + this.getPort() + "}"; 
	}
	
	/**
	 * Checks if a given connection has a valid address and port
	 * @param address String with the address
	 * @param port String with the port
	 * @return true if the connection is valid, false otherwise
	 */
	public static boolean isValidConnection(String address, String port) {
		return checkIPv4(address) && isValidPort(port);
	}
	
	/**
	 * Checks if a String is a valid port that is not a well-known port
	 * @param st String to check
	 * @return true if it is valid, false otherwise
	 */
	private static boolean isValidPort (String st) {
		if (isNumeric(st)) {
			int num = Integer.parseInt(st);
			if (num > 1024 && num <= 65536) {
				return true;
			}
		}
		return false;
	}
	
	public InetSocketAddress toSocketAddress () {
		return new InetSocketAddress(this.getInetAddress(), this.getPort());
	}
	
	/**
	 * Checks if all characters of a String are digits
	 * @param st Strint to check
	 * @return true if all are digits, false otherwise
	 */
	private static boolean isNumeric (String st) {
		return st.chars().allMatch( Character::isDigit );
	}
	
	/**
	 * Checks if a String is a valid IP address in IPv4 format
	 * @param ip String to check
	 * @return true if it is valid, false otherwise
	 */
	private static final boolean checkIPv4(final String ip) {
	    boolean isIPv4;
	    try {
		    final InetAddress inet = InetAddress.getByName(ip);
		    isIPv4 = inet.getHostAddress().equals(ip)
		            && inet instanceof Inet4Address;
	    } catch (final UnknownHostException e) {
	    	isIPv4 = false;
	    	ExceptionPrinter.print("Could not recognize address: " + ip, e);
	    }
	    return isIPv4;
	}
	
	public static InetAddress getValidIP() {
		Enumeration<NetworkInterface> e;
		Enumeration<InetAddress> ee;
		InetAddress i;
		
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    if(n.isLoopback())
	                continue;
	            if(n.isPointToPoint())
	                continue;
			    ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        i = (InetAddress) ee.nextElement();
			        if (i instanceof Inet4Address) {
			        	if (!i.isLinkLocalAddress()) { // && !i.isSiteLocalAddress()
			        		return i;
			        	}
			        } 
			    }
			}
		} catch (SocketException ex) {
			ExceptionPrinter.print("Could not get the netowrk interfaces.", ex);
		}
		return null;
	}
	
}
