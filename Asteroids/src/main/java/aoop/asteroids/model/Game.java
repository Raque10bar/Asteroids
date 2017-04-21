package aoop.asteroids.model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import aoop.asteroids.gui.Player;
import aoop.asteroids.objectdb.ObjectDB;
import aoop.asteroids.server.Address;
import aoop.asteroids.server.ExceptionPrinter;

/**
 *	The game class is the backbone of all simulations of the asteroid game. It 
 *	contains all game object and keeps track of some other required variables 
 *	in order to specify game rules.
 *	<p>
 *	The game rules are as follows:
 *	<ul>
 *		<li> All game objects are updated according to their own rules every 
 *			game tick. </li>
 *		<li> Every 200th game tick a new asteroid is spawn. An asteroid cannot 
 *			spawn within a 50 pixel radius of the player. </li>
 *		<li> There is a maximum amount of asteroids that are allowed to be 
 *			active simultaneously. Asteroids that spawn from destroying a 
 *			larger asteroid do count towards this maximum, but are allowed to 
 *			spawn if maximum is exceeded. </li>
 *		<li> Destroying an asteroid spawns two smaller asteroids. I.e. large 
 *			asteroids spawn two medium asteroids and medium asteroids spawn two 
 *			small asteroids upon destruction. </li>
 *		<li> The player dies upon colliding with either a buller or an 
 *			asteroid. </li>
 *		<li> Destroying every 5th asteroid increases the asteroid limit by 1, 
 *			increasing the difficulty. </li>
 *	</ul>
 *	<p>
 *	This class implements Runnable, so all simulations will be run in its own 
 *	thread. This class extends Observable in order to notify the view element 
 *	of the program, without keeping a reference to those objects.
 *
 *	@author Yannick Stoffers
 */
public class Game extends Observable implements Runnable, Serializable
{
	
	private boolean multiplePlayers = false;
	private boolean isHosted = false;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setHosted() {
		this.isHosted = true;
	}
	
	public boolean isHosted() {
		return this.isHosted;
	}
	
	private CopyOnWriteArrayList <Spaceship> ships;

	/** List of bullets. */
	private Collection <Bullet> bullets;

	/** List of asteroids. */
	private Collection <Asteroid> asteroids;

	/** Random number generator. */
	private static Random rng;

	/** Game tick counter for spawning random asteroids. */
	private int cycleCounter;

	/** Asteroid limit. */
	private int asteroidsLimit;

	/** 
	 *	Indicates whether the a new game is about to be started. 
	 *
	 *	@see #run()
	 */
	private boolean aborted;
	
	private Address address;

	/** Initializes a new game from scratch. */
	public Game ()
	{
		Game.rng = new Random ();
		this.ships = new CopyOnWriteArrayList<>();
		this.ships.add(new Spaceship());
		this.initGameData (Color.WHITE, "");
	}
	
	public void setMultiplePlayers() {
		multiplePlayers = true;
	}
	
	public boolean getMultiplePlayers() {
		return multiplePlayers;
	}
	
	/** Sets all game data to hold the values of a new game. */
	public void initGameData (Color color, String name)
	{
		initialize();
		initSpaceship(color, name);
	}
	
	public void initGameData() {
		initialize();
		initSpaceship();
	}
	
	private void initialize() {
		this.aborted = false;
		this.cycleCounter = 0;
		this.asteroidsLimit = 7;
		this.bullets = new CopyOnWriteArrayList <> ();
		this.asteroids = new CopyOnWriteArrayList <> ();
		this.ships.subList(1, ships.size()).clear();
		this.multiplePlayers = false;
	}
	
	public void initSpaceship() {
		this.ships.get(0).reinit();
	}
	
	public void initSpaceship(Color color, String name) {
		Spaceship ship = this.ships.get(0);
		ship.setColor(color);
		ship.setName(name);
		ship.reinit ();
		ship.restartScore();
		this.ships.remove(0);
		this.ships.add(ship);
	}
	
	public void setModel(Collection<Asteroid> asts, Collection<Bullet> bulls, CopyOnWriteArrayList<Spaceship> ss) {
		asteroids = asts;
		bullets = bulls;
		ships = ss;
		setChanged();
		notifyObservers();
	}
	
	public void setShip(Spaceship s) {
		if (ships.contains(s)) {
			updateShip(s);
		} else {
			addNewShip(s);
		}
	}
	
	public void setModelServer(Spaceship s, ArrayList<Bullet> bulls) {
		setShip(s);
		updateBullets(bulls);
		setChanged();
		notifyObservers();
	}
	
	public void removeShip(Address a) {
		Spaceship s = new Spaceship();
		s.setAddress(a);
		ships.remove(s);
		setChanged();
		notifyObservers();
	}

	public void updateBullets(ArrayList<Bullet> bulls) {
		ArrayList<Bullet> toDelete = new ArrayList<>();
		if (!bulls.isEmpty()) {
			Address owner = bulls.get(0).getAddress();
			Iterator<Bullet> iter = this.bullets.iterator();
			Bullet b;
			while (iter.hasNext()) {
			  b = iter.next();
			  if (b.getAddress().equals(owner)) {
				  toDelete.add(b);
			  }
			}
			this.bullets.removeAll(toDelete);
			this.bullets.addAll(bulls);
		}
	}
	
	public void setModelJoiner(Collection<Asteroid> asts, Collection<Bullet> bulls, CopyOnWriteArrayList<Spaceship> ss) {
		asteroids = asts;
		setOthersBullets((ArrayList <Bullet>)bulls);
		updateShips(ss);
		setChanged();
		notifyObservers();
	}
	
	public void addNewShip(Spaceship s) {
		ships.add(s);
	}
	
	public void updateShip(Spaceship s) {
		int indx = ships.indexOf(s);
		ships.set(indx, s);
	}
	
	public void updateShips(CopyOnWriteArrayList<Spaceship> ss) {
		ships.subList(1, ships.size()).clear();
		int indx = ss.indexOf(ships.get(0));
		if (indx != -1) {
			ss.remove(indx);
		}
		ships.addAll(ss);
	}
	
	/** 
	 *	Links the given controller to the spaceship. 
	 *
	 *	@param p the controller that is supposed to control the spaceship.
	 */
	public void linkController (Player p)
	{
		p.addShip (this.ships.get(0));
	}

	/** 
	 *	Returns a clone of the spaceship, preserving encapsulation. 
	 *
	 *	@return a clone the spaceship.
	 */
	public Spaceship getPlayer ()
	{
		return this.ships.get(0).clone();
	}

	public void setShipAddress(Address a) {
		ships.get(0).setAddress(a);
	}
	
	public CopyOnWriteArrayList <Spaceship> getPlayers ()
	{
		CopyOnWriteArrayList <Spaceship> c = new CopyOnWriteArrayList <> ();
		for (Spaceship s : this.ships) c.add (s.clone ());
		return c;
	}
	
	public void setOthersBullets(ArrayList<Bullet> bulls) {
		Address owner = this.ships.get(0).getAddress();
		ArrayList<Bullet> toDelete = new ArrayList<>();
		Iterator<Bullet> iter = bulls.iterator();
		Bullet b;
		while (iter.hasNext()) {
		  b = iter.next();
		  if (b.getAddress().equals(owner)) {
			  iter.remove();
		  }
		}
		
		iter = this.bullets.iterator();
		while (iter.hasNext()) {
		  b = iter.next();
		  if (!b.getAddress().equals(owner)) {
			  toDelete.add(b);
		  }
		}
		this.bullets.removeAll(toDelete);
		this.bullets.addAll(bulls);
	}
	
	/** 
	 *	Returns a clone of the asteroid set, preserving encapsulation.
	 *
	 *	@return a clone of the asteroid set.
	 */
	public Collection <Asteroid> getAsteroids ()
	{
		Collection <Asteroid> c = new ArrayList <> ();
		for (Asteroid a : this.asteroids) c.add (a.clone ());
		return c;
	}

	/** 
	 *	Returns a clone of the bullet set, preserving encapsulation.
	 *
	 *	@return a clone of the bullet set.
	 */
	public Collection <Bullet> getBullets ()
	{
		Collection <Bullet> c = new ArrayList <> ();
		for (Bullet b : this.bullets) c.add (b.clone ());
		return c;
	}

	
	public Collection <Bullet> getPlayerBullets ()
	{
		Collection <Bullet> c = new ArrayList <> ();
		
		for (Bullet b : this.bullets) {
			if (b.getAddress().equals(this.ships.get(0).getAddress())) {
				c.add (b.clone ());
			}
		}
		return c;
	}
	
	/**
	 *	Method invoked at every game tick. It updates all game objects first. 
	 *	Then it adds a bullet if the player is firing. Afterwards it checks all 
	 *	objects for collisions and removes the destroyed objects. Finally the 
	 *	game tick counter is updated and a new asteroid is spawn upon every 
	 *	200th game tick.
	 */
	public void update ()
	{
		for (Asteroid a : this.asteroids) a.nextStep ();
		for (Bullet b : this.bullets) b.nextStep ();
		
		this.ships.get(0).nextStep ();

		if (this.ships.get(0).isFiring ())
		{
			double direction = this.ships.get(0).getDirection ();
			Bullet b = new Bullet(this.ships.get(0).getLocation (), this.ships.get(0).getVelocityX () + Math.sin (direction) * 15, this.ships.get(0).getVelocityY () - Math.cos (direction) * 15);
			b.setAddress(this.ships.get(0).getAddress());
			this.bullets.add (b);
			this.ships.get(0).setFired ();
		}

		this.checkCollisions ();
		this.removeDestroyedObjects ();

		if (this.cycleCounter == 0 && this.asteroids.size () < this.asteroidsLimit) this.addRandomAsteroid ();
		this.cycleCounter++;
		this.cycleCounter %= 200;

		this.setChanged ();
		this.notifyObservers ();
	}

	/** 
	 *	Adds a randomly sized asteroid at least 50 pixels removed from the 
	 *	player.
	 */
	private void addRandomAsteroid ()
	{
		int prob = Game.rng.nextInt (3000);
		Point loc, shipLoc = this.ships.get(0).getLocation ();
		int x, y;
		do
		{
			loc = new Point (Game.rng.nextInt (800), Game.rng.nextInt (800));
			x = loc.x - shipLoc.x;
			y = loc.y - shipLoc.y;
		}
		while (Math.sqrt (x * x + y * y) < 50);

		if (prob < 1000)		this.asteroids.add (new LargeAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
		else if (prob < 2000)	this.asteroids.add (new MediumAsteroid (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
		else					this.asteroids.add (new SmallAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
	}

	/** 
	 *	Checks all objects for collisions and marks them as destroyed upon
	 *	collision. All objects can collide with objects of a different type, 
	 *	but not with objects of the same type. I.e. bullets cannot collide with 
	 *	bullets etc.
	 */
	private void checkCollisions ()
	{ // Destroy all objects that collide.
		for (Bullet b : this.bullets)
		{ // For all bullets.
			for (Asteroid a : this.asteroids)
			{ // Check all bullet/asteroid combinations.
				if (a.collides (b))
				{ // Collision -> destroy both objects.
					b.destroy ();
					a.destroy ();
				}
			}
			
			for (Spaceship s: this.ships) {
				if (b.collides (s))
				{ // Collision with playerß -> destroy both objects
					b.destroy ();
					s.destroy ();
				}
			}
		}

		for (Asteroid a : this.asteroids)
		{	
			for (Spaceship s: this.ships) {
				if (a.collides (s))
				{ // Collision with player -> destroy both objects.
					a.destroy ();
					s.destroy ();
				}
			}
		}
	}

	/**
	 * 	Increases the score of the player by one and updates asteroid limit 
	 *	when required.
	 */
	private void increaseScore ()
	{
		this.ships.get(0).increaseScore ();
		if (this.ships.get(0).getScore () % 5 == 0) this.asteroidsLimit++;
	}

	/**
	 *	Removes all destroyed objects. Destroyed asteroids increase the score 
	 *	and spawn two smaller asteroids if it wasn't a small asteroid. New 
	 *	asteroids are faster than their predecessor and travel in opposite 
	 *	direction.
	 */
	private void removeDestroyedObjects ()
	{
		Collection <Asteroid> newAsts = new CopyOnWriteArrayList <> ();
		for (Asteroid a : this.asteroids)
		{
			if (a.isDestroyed ())
			{
				if (!isHosted) {
					this.increaseScore ();
				}	
				Collection <Asteroid> successors = a.getSuccessors ();
				newAsts.addAll (successors);
			}
			else newAsts.add (a);
		}
		this.asteroids = newAsts;

		Collection <Bullet> newBuls = new CopyOnWriteArrayList <> ();
		for (Bullet b : this.bullets) {
			if (!b.isDestroyed ()) {
				newBuls.add (b);
			}
		}
		this.bullets = newBuls;
	}

	/**
	 *	Returns whether the game is over. The game is over when the spaceship 
	 *	is destroyed.
	 *
	 *	@return true if game is over, false otherwise.
	 */ 
	public boolean gameOver ()
	{
		if (multiplePlayers) {
			return oneLeft();
		} else {
			return this.ships.get(0).isDestroyed();
		}
	}

	private boolean oneLeft() {
		int count = 0;
		for (Spaceship s: this.ships) {
			if (!s.isDestroyed()) {
				count ++;
			}
		}
		return count == 1;
	}
	
	/** 
	 *	Aborts the game. 
	 *
	 *	@see #run()
	 */
	public void abort ()
	{
		this.aborted = true;
	}

	/**
	 *	This method allows this object to run in its own thread, making sure 
	 *	that the same thread will not perform non essential computations for 
	 *	the game. The thread will not stop running until the program is quit. 
	 *	If the game is aborted or the player died, it will wait 100 
	 *	milliseconds before reevaluating and continuing the simulation. 
	 *	<p>
	 *	While the game is not aborted and the player is still alive, it will 
	 *	measure the time it takes the program to perform a game tick and wait 
	 *	40 minus execution time milliseconds to do it all over again. This 
	 *	allows the game to update every 40th millisecond, thus keeping a steady 
	 *	25 frames per second. 
	 *	<p>
	 *	Decrease waiting time to increase fps. Note 
	 *	however, that all game mechanics will be faster as well. I.e. asteroids 
	 *	will travel faster, bullets will travel faster and the spaceship may 
	 *	not be as easy to control.
	 */
	public void run ()
	{ // Update -> sleep -> update -> sleep -> etc...
		long executionTime, sleepTime;
		while (!this.aborted && !this.gameOver())
		{
			executionTime = System.currentTimeMillis ();
			this.update ();
			executionTime -= System.currentTimeMillis ();
			sleepTime = Math.max (0, 40 + executionTime);

			try
			{
				Thread.sleep (sleepTime);
			}
			catch (InterruptedException e)
			{
				ExceptionPrinter.print("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).", e);
			}
		}
		awardPointToWinner();
		saveSingleScore();
	}
	
	public void saveSingleScore() {
		if (!multiplePlayers) {
			if(this.getPlayer().getScore() > 0){
				ObjectDB.storeHighscore(this.getPlayer().getPlayerName(), this.getPlayer().getScore(), ObjectDB.SINGLE_PLAYER);
			}
		}
	}

	public void saveMultiScore() {
		if(this.getPlayer().getScore() > 0){
			ObjectDB.storeHighscore(this.getPlayer().getPlayerName(), this.getPlayer().getScore(), ObjectDB.MULTI_PLAYER);
		}
	}
	
	public void awardPointToWinner() {
		for (Spaceship s: this.ships) {
			if(!s.isDestroyed()) {
				s.increaseScore();
			}
		}
		this.setChanged();
		this.notifyObservers();
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
    
}
