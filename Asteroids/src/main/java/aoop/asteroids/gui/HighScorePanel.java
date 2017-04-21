package aoop.asteroids.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import aoop.asteroids.objectdb.HighScoreEntry;
import aoop.asteroids.objectdb.ObjectDB;

/**
 * 
 */
public class HighScorePanel extends JPanel implements ActionListener {

	/** serialVersionUID */
	public static final long serialVersionUID = 4L;

	private AbstractAction resetHighScoresAction;
	private AbstractAction mainMenuAction;

	private AsteroidsFrame frame;

	private JTabbedPane tabbedPane;
	private JPanel SPHighscoresPanel;
	private JPanel MPHighscoresPanel;

	/**
	 */
	public HighScorePanel(AsteroidsFrame frame) {
		this.frame = frame;

		this.createTabPanel();
		
		setMenu();
	}
	
	private void createTabPanel(){
		tabbedPane = new JTabbedPane();

		this.SPHighscoresPanel = setHighScoreLayout(this.SPHighscoresPanel, ObjectDB.SINGLE_PLAYER);
        tabbedPane.addTab("Singleplayer", null, this.SPHighscoresPanel,
                "Singleplayer highscores");

        this.MPHighscoresPanel = setHighScoreLayout(this.MPHighscoresPanel, ObjectDB.MULTI_PLAYER);
        tabbedPane.addTab("Multiplayer", null, MPHighscoresPanel,
                "Multiplayer highscores");
        
        //Add the tabbed pane to this panel.
        this.add(tabbedPane);
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	private JPanel setHighScoreLayout(JPanel panel, int mode) {
		panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		GridBagConstraints c = new GridBagConstraints();
		this.setSize (750, 750);
		panel.setPreferredSize(new Dimension(700, 700));
		panel.setSize(new Dimension(700, 700));
		JLabel title;
		if(mode == ObjectDB.MULTI_PLAYER){
			title = new JLabel("Multiplayer Highscores", SwingConstants.CENTER);
		} else {
			title = new JLabel("Singleplayer Highscores", SwingConstants.CENTER);
		}
		
		title.setFont(new Font("SansSerif", Font.BOLD, 30));
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		panel.add(title, c);
		

		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(this.setScores(mode), c);
		
		JButton returnButton = new JButton("Back");
		returnButton.setActionCommand("back");
		returnButton.addActionListener(this);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		panel.add(returnButton, c);
		return panel;
	}
	
	private JPanel setScores(int mode) {
		ArrayList<HighScoreEntry> highscores = ObjectDB.getHighscores(mode);
		JPanel scoresPanel;
		if(highscores.size() == 0){
			scoresPanel = new JPanel(new BorderLayout());
			JLabel empty = new JLabel("No highscores yet.");
			scoresPanel.add(empty);
		}  else {
			int numRows = highscores.size() < 10 ? highscores.size() : 10;
			scoresPanel = new JPanel(new GridLayout(numRows, 1, 0, 10));
			for(int x = 0; x < numRows; x++){
				JPanel row = new JPanel(new FlowLayout());
				HighScoreEntry hse = highscores.get(x);
				row.add(new JLabel(Integer.toString(x+1)+": ", JLabel.LEFT));
				row.add(new JLabel(hse.getName(), JLabel.LEFT));
				row.add(new JLabel(Integer.toString(hse.getScore()), JLabel.LEFT));
				scoresPanel.add(row);
			}
		}
		return scoresPanel;
	}
	
	private void setMenu(){
		JMenuBar mb = new JMenuBar();
		JMenu m = new JMenu("Game");
		mb.add(m);
		
		this.resetHighScoresAction = new AbstractAction("Reset Highscores") {
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetScores();
			}
		};
		
		this.mainMenuAction = new AbstractAction("Main Menu") {
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				backToMainMenu();
			}
		};

		m.add(this.mainMenuAction);
		m.add(this.resetHighScoresAction);
		this.frame.setJMenuBar(mb);
		
	}
	
	private void resetScores(){
		ObjectDB.deleteAllHighscores();

		this.remove(tabbedPane);
		this.createTabPanel();
		this.revalidate();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("back")) {
			this.backToMainMenu();
		}
	}

	private void backToMainMenu() {
		this.frame.toMainMenu();
	}
}
