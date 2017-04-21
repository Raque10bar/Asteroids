package aoop.asteroids.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aoop.asteroids.server.Address;
import aoop.asteroids.server.Server;

/**
 * 
 */
public class MenuPanel extends JPanel implements ActionListener {

	/** serialVersionUID */
	public static final long serialVersionUID = 4L;

	private AsteroidsFrame frame;
	private JLabel ipLabel = new JLabel();
	private JLabel portLabel = new JLabel();
	private JLabel userLabel = new JLabel();
	private JLabel colorLabel = new JLabel();

	/**
	 */
	public MenuPanel(AsteroidsFrame frame) {
		this.frame = frame;

		setMenuLayout();
	}

	private void setMenuLayout() {
		JPanel grid = new JPanel(new BorderLayout());
		this.setSize (750, 750);
		grid.setPreferredSize(new Dimension(700, 700));

		grid.add(this.createMenuButtons(), BorderLayout.PAGE_START);
		
		JPanel lowerGrid = new JPanel(new BorderLayout());
		lowerGrid.setPreferredSize(new Dimension(750, 100));
		
		lowerGrid.add(this.createIPAndPortText(), BorderLayout.WEST);
		lowerGrid.add(this.createUserPanel(), BorderLayout.EAST);

		grid.add(lowerGrid, BorderLayout.PAGE_END);
		
		this.add(grid);
	}

	public JPanel createMenuButtons() {
		JPanel grid = new JPanel(new GridLayout(5, 1, 0, 10));
		JButton newGameButton = new JButton("New Singleplayer game");
		JButton hostMultiGameButton = new JButton("Host Multiplayer game");
		JButton specMultiGameButton = new JButton("Spectate Multiplayer game");
		JButton joinMultiGameButton = new JButton("Join Multiplayer game");
		JButton highscoresButton = new JButton("Highscores");

		newGameButton.setActionCommand("newgame");
		newGameButton.addActionListener(this);

		hostMultiGameButton.setActionCommand("hostgame");
		hostMultiGameButton.addActionListener(this);

		specMultiGameButton.setActionCommand("spectategame");
		specMultiGameButton.addActionListener(this);

		joinMultiGameButton.setActionCommand("joingame");
		joinMultiGameButton.addActionListener(this);

		highscoresButton.setActionCommand("highscores");
		highscoresButton.addActionListener(this);

		newGameButton.setToolTipText("Start new Singleplayer game");
		hostMultiGameButton.setToolTipText("Host new Multiplayer game");
		specMultiGameButton.setToolTipText("Spectate a Multiplayer game");
		joinMultiGameButton.setToolTipText("Join a Multiplayer game");
		joinMultiGameButton.setToolTipText("See Highscores");

		grid.add(newGameButton);
		grid.add(hostMultiGameButton);
		grid.add(specMultiGameButton);
		grid.add(joinMultiGameButton);
		grid.add(highscoresButton);

		return grid;
	}

	public JPanel createIPAndPortText() {
		JPanel gridText = new JPanel(new GridLayout(2, 2));

		JLabel ipAddress = new JLabel("IP: ");
		JLabel port = new JLabel("Port: ");
		gridText.add(ipAddress);
		this.ipLabel.setText(Address.getValidIP().getHostAddress());
		gridText.add(this.ipLabel);
		gridText.add(port);
		this.portLabel.setText(Integer.toString(Server.DEFAULT_SERVER_PORT));
		gridText.add(this.portLabel);

		return gridText;
	}

	public JPanel createUserPanel() {
		JPanel userPanel = new JPanel(new GridLayout(3, 2));

		JLabel nameText = new JLabel("Name: ");
		JLabel colorText = new JLabel("Color: ");
		userPanel.add(nameText);
		
		this.colorLabel.setBackground(new Color((int)(Math.random() * 0x1000000)));
//		this.userLabel.setText(Long.toHexString(Double.doubleToLongBits(Math.random())).substring(4,9));
		this.userLabel.setText("Anonymous");
		userPanel.add(this.userLabel);
		userPanel.add(colorText);
		
        this.colorLabel.setOpaque(true);
		userPanel.add(this.colorLabel);

		JButton changeNameButton = new JButton("Change name");
		changeNameButton.setActionCommand("changename");
		changeNameButton.addActionListener(this);
		userPanel.add(changeNameButton);

		JButton changeColorButton = new JButton("Change color");
		changeColorButton.setActionCommand("changecolor");
		changeColorButton.addActionListener(this);
		userPanel.add(changeColorButton);

		return userPanel;
	}
	

	public void setUserName() {
		String name = JOptionPane.showInputDialog("What is your name?");
		if(name != null) this.userLabel.setText(name);
	}
	
	public void setColor() {
		Color newColor = JColorChooser.showDialog(null, "Choose a color", this.colorLabel.getBackground());
		if(newColor != null) this.colorLabel.setBackground(newColor);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("newgame")) {
			newSingleGame();
		} else if (e.getActionCommand().equals("hostgame")) {
			hostMultiGame();
		} else if (e.getActionCommand().equals("spectategame")) {
			spectateMultiGame();
		} else if (e.getActionCommand().equals("joingame")) {
			joinMultiGame();
		} else if (e.getActionCommand().equals("changename")) {
			setUserName();
		} else if (e.getActionCommand().equals("changecolor")) {
			setColor();
		} else if (e.getActionCommand().equals("highscores")) {
			toHighscores();
		} else {

		}
	}

	private void newSingleGame() {
		this.frame.newSinglePlayerGame();
	}

	private void hostMultiGame() {
		this.frame.startHostedAsteroids();
	}

	private void spectateMultiGame() {
		this.frame.spectateAsteroids();
	}

	private void joinMultiGame() {
		this.frame.joinAsteroids();
	}
	
	public Color getColor(){
		return this.colorLabel.getBackground();
	}
	
	public String getUserName() {
		return this.userLabel.getText();
	}
	
	private void toHighscores(){
		this.frame.toHighScores();
	}
}