package model.score;

import model.score.HighscoreEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

public class Highscores extends AbstractTableModel implements Serializable{

	/**
	 * the object to store the entries
	 */
	ArrayList<HighscoreEntry> entries = new ArrayList<HighscoreEntry>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return entries.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(int row, int col) {
		HighscoreEntry entry = entries.get(row);
		switch (col) {
		case 0: return entry.getName();
		case 1: return entry.getScore();
		case 2: return entry.getRows();
		default: return entry.getColor();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnName(int index){
		switch(index) {
			case 0: return "Name";
			case 1: return "Score";
			case 2: return "Rows";
			default: return "Colors";
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class getColumnClass(int index){
		switch(index) {
			case 0: return String.class;
			case 1: return Long.class;
			case 2: return Integer.class;
			default: return Integer.class;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int colIndex) {
		return false;
	}
	
	/**
	 * adds an entry to the list
	 * @param e the entry to add
	 */
	public void addEntry(HighscoreEntry e){
		entries.add(e);
		Collections.sort(entries);
	}
	
	/**
	 * serialisation
	 * @param o objectoutputstream to write to
	 * @throws IOException
	 */
	public void writeObject(ObjectOutputStream o) throws IOException{
		o.writeObject(entries);
	}
	
	/**
	 * deserialisation
	 * @param o objectinputstream to read from
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException{
		entries = (ArrayList<HighscoreEntry>) o.readObject();
	}
	
	/**
	 * prints all entries on the standard output, only exists
	 * for debugging purposes
	 */
	public void print(){
		for (HighscoreEntry h : entries){
			System.out.println(h.getName()+" "+h.getScore()+" "+h.getRows()+" "+h.getColor());
		}
	}
	
}
