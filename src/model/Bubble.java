package model;

import utils.Constants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Bubble {
    /**
     * the color of the bubble
     */
    Color color;
    /**
     * radius of a bubble
     */
    public static final int RADIUS = 14;
    /**
     * visibility of the bubble on the screen
     */
    private boolean visible;
    /**
     * location of the top left corner of the bubble
     */
    public Point loc;
    /**
     * used for marking the bubble (mostly to be removed)
     */
    private boolean marked;

    /**
     * constructor for the class bubble
     *
     * @param c the color of the bubble
     */
    public Bubble(Color c) {
        color = c;
        marked = false;
    }

    /**
     * returns the y coordinate of the bubble within the bubble matrix
     *
     * @return the row of the bubble matrix in which this bubble can be found
     */
    public int getRow() {
        return loc.y / (Constants.ROW_DISTANCE / 2);
    }

    /**
     * returns the x coordinate of the bubble within the bubble matrix
     *
     * @return the column of the bubble matrix in which this bubble can be found
     */
    public int getCol() {
        return loc.x / ((Bubble.RADIUS + 1) * 2);
    }

    /**
     * marks the bubble for further operations
     */
    public void mark() {
        marked = true;
    }

    /**
     * unmarks the bubble
     */
    public void unmark() {
        marked = false;
    }

    /**
     * returns if the bubble is marked
     *
     * @return true if the bubble is marked, else false
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * getter for the color of the bubble
     *
     * @return the color of the bubble
     */
    public Color getColor() {
        return color;
    }

    /**
     * setter for visiblity
     *
     * @param v true for setting visible, else false
     */
    public void setVisible(boolean v) {
        visible = v;
    }

    /**
     * getter for visibility
     *
     * @return true is the bubble is visible, else false
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * setter for the location of the bubble (top left corner)
     *
     * @param p the desired location
     */
    public void setLocation(Point p) {
        this.loc = p;
    }

    /**
     * getter for the location of the bubble (top left corner)
     *
     * @return the position of the top left corner of the bubble
     */
    public Point getLocation() {
        return loc;
    }

    /**
     * getter for the location of the center of the bubble
     *
     * @return the location of the center of the bubble
     */
    public Point getCenterLocation() {
        return new Point(loc.x + RADIUS + 1,
                loc.y + RADIUS + 1);
    }

    /**
     * paints the bubble on the given object with the specified color
     * to the specified location
     *
     * @param g2d the graphics object where the bubble should be drawn
     */
    public void paintBubble(Graphics2D g2d) {
        if (isVisible()) {
            g2d.setColor(color);
            g2d.fillOval(loc.x, loc.y, RADIUS * 2, RADIUS * 2);
        }
    }

    /**
     * static method for getting a random color that can be set
     * as the color of a bubble
     *
     * @param bound the number of possibilities when choosing randomly, maximal 8
     * @return the generated color
     */
    public static Color getRandomColor(int bound) {
        int rnd = (int) (bound <= 8 ? Math.random() * bound : Math.random() * 8);
        switch (rnd) {
            case 0:
                return Color.green;
            case 1:
                return Color.pink;
            case 2:
                return Color.yellow;
            case 3:
                return Color.red;
            case 4:
                return Color.cyan;
            case 5:
                return Color.magenta;
            case 6:
                return Color.orange;
            case 7:
                return Color.black;
            default:
                break;
        }
        return null;
    }

}
