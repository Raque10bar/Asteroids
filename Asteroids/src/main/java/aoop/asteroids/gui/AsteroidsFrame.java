package aoop.asteroids.gui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import aoop.asteroids.model.Game;
import aoop.asteroids.objectdb.ObjectDB;
import aoop.asteroids.server.Address;
import aoop.asteroids.server.ExceptionPrinter;
import aoop.asteroids.server.Joiner;
import aoop.asteroids.server.Server;
import aoop.asteroids.server.Spectator;
import aoop.asteroids.server.UDPEntity;

/**
 * AsteroidsFrame is a class that extends JFrame and thus provides a game window
 * for the Asteroids game.
 *
 * @author Yannick Stoffers
 */
public class AsteroidsFrame extends JFrame {

	/** serialVersionUID */
	public static final long serialVersionUID = 1L;

	/** New game action. */
	private AbstractAction newGameAction;

	/** Main menu action. */
	private AbstractAction mainMenuAction;

	/** Quit action. */
	private AbstractAction quitAction;

	/** The game model. */
	private Game game;

	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;

	private MenuPanel mp;

	private HighScorePanel hp;

	private JPanel load;
	
	private UDPEntity udpE;

	/**
	 * Constructs a new Frame, requires a game model.
	 *
	 * @param game
	 *            game model.
	 * @param controller
	 *            key listener that catches the users actions.
	 */
	public AsteroidsFrame(Game game, Player controller) {
		this.game = game;

		this.initActions();

		this.setTitle("Asteroids");
		this.setSize(800, 800);
		this.addKeyListener(controller);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.createMenu();

		this.mp = new MenuPanel(this);
		this.add(this.mp);

		this.load = loadingPanel();
		this.ap = new AsteroidsPanel(this.game);

		this.setVisible(true);
	}

	public void createMenu() {
		JMenuBar mb = new JMenuBar();
		JMenu m = new JMenu("Game");
		mb.add(m);
		m.add(this.newGameAction);
		m.add(this.mainMenuAction);
		m.add(this.quitAction);
		this.setJMenuBar(mb);
	}

	public void createClientMenu() {
		JMenuBar mb = new JMenuBar();
		JMenu m = new JMenu("Game");
		mb.add(m);
		m.add(this.mainMenuAction);
		m.add(this.quitAction);
		this.setJMenuBar(mb);
	}

	public void startAsteroids() {
		this.newGame();
		this.game.initGameData(this.mp.getColor(), this.mp.getUserName());
		this.game.setHosted();
		this.setVisible(false);
		this.remove(this.mp);
		this.add(this.ap);
		this.setVisible(true);
		new Thread(this.game).start();
	}

	public void startHostedAsteroids() {
		udpE = new Server(game);
		game.setAddress(udpE.getAddress());
		this.changeActions((Server) udpE);
		this.createMenu();
		this.startAsteroids();
	}

	public void newSinglePlayerGame() {
		this.newGame();
		this.game.initGameData(this.mp.getColor(), this.mp.getUserName());
		this.setVisible(false);
		this.remove(this.mp);
		this.add(this.ap);
		this.setVisible(true);
		new Thread(this.game).start();
	}

	public void newHostedGame() {
		this.newGame();
		this.game.initGameData();
		((Server)udpE).init();
		new Thread(this.game).start();
	}
	
	public void spectateAsteroids() {
		Address address = ConnectionRequestDialog.requestAddress(Address.getValidIP().getHostAddress(),
				Server.DEFAULT_SERVER_PORT);
		if (address == null) {
			return;
		}
		this.setVisible(false);
		this.remove(this.mp);
		Spectator s = new Spectator (address, this);
		s.init();
		this.changeActions(s);
		this.createClientMenu();
	}

	public void joinAsteroids() {
		Address address = ConnectionRequestDialog.requestAddress(Address.getValidIP().getHostAddress(),
				Server.DEFAULT_SERVER_PORT);
		if (address == null) {
			return;
		}
		this.setVisible(false);
		this.remove(this.mp);
		this.game.initGameData(this.mp.getColor(), this.mp.getUserName());
		this.game.setHosted();
		Joiner j = new Joiner (address, this);
		this.changeActions(j);
		j.init();
		this.createClientMenu();
	}

	public Game getGame() {
		return this.game;
	}
	
	public void startWait() {
		this.game.abort();
		this.setVisible(false);
		this.remove(this.ap);
		this.add(load);
		this.setVisible(true);
	}

	public void finishWait() {
		this.setVisible(false);
		this.remove(load);
		this.add(this.ap);
		this.setVisible(true);
	}

	public void toMainMenu() {
		this.game.abort();
		this.setVisible(false);
		if(this.ap != null) this.remove(this.ap);
		if(this.hp != null) this.remove(this.hp);
		this.add(this.mp);
		this.createMenu();
		this.setVisible(true);
	}

	public void toHighScores() {
		this.game.abort();
		this.hp = new HighScorePanel(this);
		this.setVisible(false);
		this.remove(this.ap);
		this.remove(this.mp);
		this.add(this.hp);
		this.setVisible(true);
	}

	private JPanel loadingPanel() {
		JPanel panel = new JPanel();
		BoxLayout layoutMgr = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
		panel.setLayout(layoutMgr);

		URL imgUrl = this.getClass().getResource("/resources/loading_icon.gif");
		ImageIcon imageIcon = new ImageIcon(imgUrl);
		JLabel iconLabel = new JLabel();
		iconLabel.setIcon(imageIcon);
		imageIcon.setImageObserver(iconLabel);

		JLabel label = new JLabel("Waiting for server...");
		panel.add(iconLabel);
		panel.add(label);
		return panel;
	}

	/** Quits the old game and starts a new one. */
	private void newGame() {
		this.game.abort();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			ExceptionPrinter.print("Could not sleep before initialing a new game.", e);
		}

//		if(this.game.getPlayer().getScore() > 0){
//			ObjectDB.storeHighscore(this.mp.getUserName(), this.game.getPlayer().getScore(), ObjectDB.SINGLE_PLAYER);
//		}
	}

	/** Initializes the quit- and new game action. */
	private void initActions() {
		// Creates a new model
		this.newGameAction = new AbstractAction("New Game") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AsteroidsFrame.this.newSinglePlayerGame();
			}
		};

		// Goes back to the main menu
		this.mainMenuAction = new AbstractAction("Main Menu") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AsteroidsFrame.this.toMainMenu();
			}
		};

		// Quits the application
		this.quitAction = new AbstractAction("Quit") {
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		};

	}

	private void changeActions(Server s) {
		this.newGameAction = new AbstractAction("New Game") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AsteroidsFrame.this.newHostedGame();
			}
		};
		
		// Goes back to the main menu
		this.mainMenuAction = new AbstractAction("Main Menu") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AsteroidsFrame.this.toMainMenu();
				s.finish();
				s.saveScore();
			}
		};

		// Quits the application
		this.quitAction = new AbstractAction("Quit") {
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.finish();
				s.saveScore();
				System.exit(0);
			}
		};

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				s.finish();
				s.saveScore();
				System.exit(0);
			}
		});

	}

	private void changeActions(Joiner j) {
		
		// Goes back to the main menu
		this.mainMenuAction = new AbstractAction("Main Menu") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AsteroidsFrame.this.toMainMenu();
				j.saveScore();
				j.finish();
			}
		};

		// Quits the application
		this.quitAction = new AbstractAction("Quit") {
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				j.finish();
				j.saveScore();
				System.exit(0);
			}
		};

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				j.finish();
				j.saveScore();
				System.exit(0);
			}
		});

	}
	
	private void changeActions(Spectator s) {

		// Goes back to the main menu
		this.mainMenuAction = new AbstractAction("Main Menu") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AsteroidsFrame.this.toMainMenu();
				s.finish();
			}
		};

		// Quits the application
		this.quitAction = new AbstractAction("Quit") {
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.finish();
				System.exit(0);
			}
		};

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				s.finish();
				System.exit(0);
			}
		});

	}
}
