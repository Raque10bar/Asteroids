package aoop.asteroids.objectdb;

import java.io.Serializable;
import javax.persistence.*;
 
@Entity
public class HighScoreEntry implements Serializable {
    private static final long serialVersionUID = 1L;
 
    @Id @GeneratedValue
    private long id;
 
    private String name;
    private int score;
    private int mode;

    public HighScoreEntry() {
    }
    
    public HighScoreEntry(String name, int score, int mode) {
    	this.name = name;
    	this.score = score;
    	this.mode = mode;
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %d)", this.name, this.score);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}
