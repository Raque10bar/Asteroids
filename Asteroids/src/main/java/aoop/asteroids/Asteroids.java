package aoop.asteroids;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.Player;
import aoop.asteroids.model.Game;

/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *
 *	@author Yannick Stoffers
 */
public class Asteroids 
{

	/** Constructs a new instance of the program. */
	public Asteroids ()
	{
		Player player = new Player ();
		Game game = new Game ();
		game.linkController (player);
		new AsteroidsFrame (game, player);
	}

	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	public static void main (String [] args)
	{
		if (System.getProperty ("os.name").contains ("Mac")) 
		{
			System.setProperty ("apple.laf.useScreenMenuBar", "true");
		}
		new Asteroids ();
	}
	
}
