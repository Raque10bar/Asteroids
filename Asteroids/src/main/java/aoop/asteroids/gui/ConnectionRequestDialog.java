package aoop.asteroids.gui;

import java.awt.GridLayout;

import javax.swing.*;

import aoop.asteroids.server.Address;

public class ConnectionRequestDialog {
	
	public static Address requestAddress(){
		return requestAddress("", 0);
	}
	public static Address requestAddress(String defaultIP, int defaultPort)
	{
		JTextField ipAddressField = new JTextField(10);
		if(defaultIP != ""){
			ipAddressField.setText(defaultIP);
		}
		
		JTextField portField = new JTextField(5);
		if(defaultPort != 0){
			portField.setText(Integer.toString(defaultPort));
		}

		JPanel myPanel = new JPanel(new GridLayout(2, 2));
		myPanel.add(new JLabel("IP Address:"));
		myPanel.add(ipAddressField);
		myPanel.add(new JLabel("Port:"));
		myPanel.add(portField);

		int result = JOptionPane.showConfirmDialog(null, myPanel, "To which address do you wish to connect?",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			
			if(ipAddressField.getText() == "" || portField.getText() == ""){
				JOptionPane.showMessageDialog(null, "This is not a valid IP-address and port number");
				return null;
			}
			String ipAddress = ipAddressField.getText();
			int port;
			try{
				port = Integer.parseInt(portField.getText());
			} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "This is not a valid IP-address and port number");
				return null;
			}
			Address address = new Address(ipAddress, port);
			return address;
		} else {
			return null;
		}
	}
}