package model.score;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;

public class HighscoreEntry implements Serializable, Comparable<HighscoreEntry>, Comparator<HighscoreEntry>{
	private String name;
	private long score;
	private int rows;
	private int color;
	
	/**
	 * constructor for the class
	 * @param name name of the player
	 * @param score achieved score
	 * @param rows initial number of rows
	 * @param color colors used
	 */
	public HighscoreEntry( String name, long score, int rows, int color) {
		this.name = name;
		this.score = score;
		this.rows = rows;
		this.color = color;
	}


	/**
	 * getter for the name
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}
	/**
	 * setter for the name
	 * @param name the desired name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * getter for score
	 * @return achieved score
	 */
	public long getScore() {
		return score;
	}
	/**
	 * setter for score
	 * @param score the achieved score
	 */
	public void setScore(long score) {
		this.score = score;
	}
	/**
	 * getter for rows
	 * @return returns the initial number of rows
	 */
	public int getRows() {
		return rows;
	}
	/**
	 * setter for rows
	 * @param rows the desired number of initial rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}
	/**
	 * getter for color
	 * @return the number of colors
	 */
	public int getColor() {
		return color;
	}
	/**
	 * setter for color
	 * @param color the desired number of colors
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * serialisation
	 * @param o the objectouptupstream to write to
	 * @throws IOException
	 */
	public void writeObject(ObjectOutputStream o) throws IOException{
		o.writeObject(name);
		o.writeLong(score);
		o.writeInt(rows);
		o.writeInt(color);
	}
	
	/**
	 * deserialisation
	 * @param o the objectinputstream to read from
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException{
		name = (String) o.readObject();
		score = (long) o.readLong();
		rows = (int) o.readInt();
		color = (int) o.readInt();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(HighscoreEntry other) {
		if(score > other.getScore()) return -1;
		if(score < other.getScore()) return 1;
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(HighscoreEntry h1, HighscoreEntry h2) {
		if(h1.score > h2.getScore()) return -1;
		if(h1.score < h2.getScore()) return 1;
		return 0;
	}
}
