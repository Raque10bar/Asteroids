package aoop.asteroids.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;

/**
 *	AsteroidsPanel extends JPanel and thus provides the actual graphical 
 *	representation of the game model.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsPanel extends JPanel
{

	/** serialVersionUID */
	public static final long serialVersionUID = 4L;

	/** Game model. */
	private Game game;

	/** 
	 *	Constructs a new game panel, based on the given model.
	 *
	 *	@param game game model.
	 */
	public AsteroidsPanel (Game game)
	{
		this.game = game;
		this.game.addObserver (new Observer ()
		{
			@Override
			public void update (Observable o, Object arg)
			{
				AsteroidsPanel.this.repaint ();
			}
		});
	}
	
	/**
	 *	Method for refreshing the GUI.
	 *
	 *	@param g graphics instance to use.
	 */
	@Override
	public void paintComponent (Graphics g)
	{
		super.paintComponent (g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.setBackground (Color.black);

		this.paintSpaceships (g2);
		this.paintAsteroids (g2);
		this.paintBullets (g2);

		for(int x = 0; x < this.game.getPlayers().size(); x++){
			Spaceship player = this.game.getPlayers().get(x);
			g2.setColor (player.getColor());
			if(player.getPlayerName() == null){
				g2.drawString (String.valueOf (player.getScore ()), 10, ((x+1)*20));
			} else {
				g2.drawString (player.getPlayerName()+" - "+String.valueOf (player.getScore ()), 10, ((x+1)*20));
			}
		}

		g2.setColor (Color.WHITE);
		if(this.game.getAddress() != null ){
			g2.drawString (String.valueOf ("IP: "+this.game.getAddress().getStAddress()), 10, 710);
			g2.drawString (String.valueOf ("Port: "+this.game.getAddress().getPort()), 10, 730);
		}
	}

	/**
	 *	Draws all bullets in the GUI as a yellow circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintBullets (Graphics2D g)
	{
		g.setColor(Color.yellow);

		for (Bullet b : this.game.getBullets ())
		    g.drawOval (b.getLocation ().x - 2, b.getLocation ().y - 2, 5, 5);
	}

	/**
	 *	Draws all asteroids in the GUI as a filled gray circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintAsteroids (Graphics2D g)
	{
		g.setColor (Color.GRAY);

		for (Asteroid a : this.game.getAsteroids ())
		{
			Ellipse2D.Double e = new Ellipse2D.Double ();
			e.setFrame (a.getLocation ().x - a.getRadius (), a.getLocation ().y - a.getRadius (), 2 * a.getRadius (), 2 * a.getRadius ());
			g.fill (e);
		}
	}
	
	/**
	 *	Draws the players in the GUI as a see-through white triangle. If the 
	 *	player is accelerating a yellow triangle is drawn as a simple
	 *	representation of flames from the exhaust.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintSpaceships (Graphics2D g)
    {
		// Draw body of the spaceship
		for (Spaceship s : this.game.getPlayers ()) {
			
			if (s.isDestroyed()) continue;
			
			Polygon p = new Polygon ();
			p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection ()				  ) * 20), (int)(s.getLocation ().y - Math.cos (s.getDirection ()				 ) * 20));
			p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 0.8 * Math.PI) * 20), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 0.8 * Math.PI) * 20));
			p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 1.2 * Math.PI) * 20), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 1.2 * Math.PI) * 20));
	
			g.setColor (Color.BLACK);
			g.fill (p);
			g.setColor (s.getColor());
			g.draw (p);
	
			// Spaceship accelerating -> continue, otherwise abort.
			if (!s.isAccelerating ()) continue;
	
			// Draw flame at the exhaust
			p = new Polygon ();
			p.addPoint ((int)(s.getLocation ().x - Math.sin (s.getDirection ()				  ) * 25), (int)(s.getLocation ().y + Math.cos (s.getDirection ()				 ) * 25));
			p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 0.9 * Math.PI) * 15), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 0.9 * Math.PI) * 15));
			p.addPoint ((int)(s.getLocation ().x + Math.sin (s.getDirection () + 1.1 * Math.PI) * 15), (int)(s.getLocation ().y - Math.cos (s.getDirection () + 1.1 * Math.PI) * 15));
			g.setColor(Color.yellow);
			g.fill(p);
		}
	}
}
