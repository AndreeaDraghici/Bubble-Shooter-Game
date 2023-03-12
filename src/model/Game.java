package model;

import controller.MainFrame;
import implementation.Canvas;
import implementation.MovingBubble;
import utils.Constants;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

import javax.swing.Timer;

/**
 * the engine of the game
 */
public class Game implements ActionListener {
    /**
     * the container of the bubbles on the screen
     */
    private final ArrayList<RowList> bubbles;

    /**
     * the container of the 4 bubbles waiting to be shot
     */
    private final LinkedList<Bubble> upcoming;

    /**
     * the bubble that currently moves on the screen
     */
    private MovingBubble moving_bubble;

    /**
     * number of the initial rows
     */
    private final int initial_rows;

    /**
     * number of the possible colors of bubbles
     */
    private final int colors;

    /**
     * timer object for the linear movement of the moving bubble
     */
    private Timer timer;

    /**
     * the canvas object where the bubbles should be painted
     */
    private final Canvas canvas;

    /**
     * number of the unsuccessful (no bubbles disappeared) shots.
     * used for adding a top row when it reaches 5
     */
    private int shotCount;

    /**
     * number of bubbles on the screen minus the upcoming ones
     */
    private int numOfBubbles;

    /**
     * the frame where the game is located
     */
    private MainFrame mainFrame;

    /**
     * the achieved score
     */
    private long score;

    /**
     * true if the game is stopped (the highscore is being displayed),
     * else false
     */
    private boolean stopped;

    /**
     * the maximal number of rows of bubbles on the screen
     */
    public static final int ROW_COUNT = 16;

    /**
     * the number of bubbles in a row that fill the field in width
     */
    public static final int COL_COUNT_FULL = 14;

    /**
     * the number of bubbles in a row that does not fill the field in width
     */
    public static final int COL_COUNT = 13;

    /**
     * the score received for a shot bubble
     */
    public static final int SCORE_SHOT = 10;

    /**
     * the score received for removing a bubble of the same color
     * as the one shot
     */
    public static final int SCORE_COHERENT = 20;

    /**
     * the score received for removing a bubble that anyway would
     * just float on the field
     */
    public static final int SCORE_FLOATING = 40;

    /**
     * contructor for a new game. initalises the bubble matrix, the
     * upcoming bubbles and other parameters
     *
     * @param row    initial number of rows
     * @param colors number of colors that the bubbles can colored with
     * @param c      the canvas for the game
     */
    public Game(int row, int colors, Canvas c) {
        canvas = c;
        stopped = false;
        initial_rows = row;
        this.colors = colors;
        shotCount = 0;
        numOfBubbles = 0;
        score = 0;
        bubbles = new ArrayList<>();
        for (int i = 0; i < ROW_COUNT; i++) {
            RowList r = new RowList((i % 2 == 0));
            bubbles.add(r);
            for (int j = 0; j < (r.isFullFlag() ? 14 : 13); j++) {

                Bubble b = new Bubble(Bubble.getRandomColor(colors));
                b.setLocation(
                        new Point(r.isFullFlag() ?
                                j * 2 * (Bubble.RADIUS + 1) :
                                j * 2 * (Bubble.RADIUS + 1) + (Bubble.RADIUS + 1),
                                r.isFullFlag() ?
                                        (i / 2) * Constants.ROW_DISTANCE :
                                        (i / 2) * Constants.ROW_DISTANCE + Constants.ROW_DISTANCE / 2));
                r.add(b);
                if (i < initial_rows) {
                    b.setVisible(true);
                    numOfBubbles++;
                } else
                    b.setVisible(false);
            }
        }

        upcoming = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            Bubble b = new Bubble(Bubble.getRandomColor(colors));
            upcoming.add(b);
        }
        arrangeUpcoming();
    }

    /**
     * setter for the mainframe
     *
     * @param m mainframe to be set
     */
    public void setMainFrame(MainFrame m) {
        mainFrame = m;
    }

    /**
     * paints the bubbles on the screen (the upcoming ones as well)
     *
     * @param g2d the graphics object to paint to
     */
    public void paintBubbles(Graphics2D g2d) {
        for (RowList r : bubbles) {
            for (Bubble b : r) {
                b.paintBubble(g2d);
            }
        }
        for (Bubble b : upcoming) {
            b.paintBubble(g2d);
        }
        if (moving_bubble != null)
            moving_bubble.paintBubble(g2d);
    }

    /**
     * shifts the upcoming bubbles by one. the first is removed and
     * one is added to the end
     */
    private void arrangeUpcoming() {
        upcoming.element().setLocation(
                new Point(Constants.FIELD_SIZE_X / 2 - Bubble.RADIUS,
                        Constants.FIELD_SIZE_Y - Bubble.RADIUS));
        upcoming.element().setVisible(true);
        for (int i = 1; i < 4; i++) {
            upcoming.get(i).setLocation(new Point(
                    Constants.FIELD_SIZE_X - (4 - i) * (2 * (Bubble.RADIUS + 6)),
                    Constants.FIELD_SIZE_Y - (Bubble.RADIUS + 1)));
            upcoming.get(i).setVisible(true);
        }
    }

    /**
     * fires the bubble that is on the first place in the upcoming list
     *
     * @param mouseLoc location of the mouse on the screen
     * @param panelLoc location of the panel on the screen
     */
    public void fire(Point mouseLoc, Point panelLoc) {
        boolean movingExists = !(moving_bubble == null);
        movingExists = (movingExists && moving_bubble.isMoving());
        if (!movingExists) {
            Point dir = new Point(mouseLoc.x - panelLoc.x,
                    mouseLoc.y - panelLoc.y);
            moving_bubble = new MovingBubble(upcoming.remove(), dir);
            upcoming.add(new Bubble(Bubble.getRandomColor(colors)));
            arrangeUpcoming();
            numOfBubbles++;
            score += SCORE_SHOT;
            mainFrame.updateScore(score);
            timer = new Timer(20, this);
            timer.start();
        }
    }

    /**
     * checks whether the moving bubble is close to a fixed one.
     * if yes, then it will be fixed in the hexile grid
     */
    public void checkProximity() {
        int currentPosX = moving_bubble.getCenterLocation().x;
        int currentPosY = moving_bubble.getCenterLocation().y;
        int row = (currentPosY - Bubble.RADIUS) / (Constants.ROW_DISTANCE / 2);
        int col;
        if (row < ROW_COUNT) {
            if (bubbles.get(row).isFullFlag()) {
                col = (currentPosX) / ((Bubble.RADIUS + 1) * 2);
            } else {
                col = (currentPosX - (Bubble.RADIUS + 1)) / ((Bubble.RADIUS + 1) * 2);
            }
            if (row == 0) {
                fixBubble(row, col);
            }
            ArrayList<Bubble> neighbours = getNeighbours(row, col);
            for (Bubble b : Objects.requireNonNull(neighbours)) {
                if (b.isVisible() && BubbleDist(moving_bubble, b) <= 4 + (Bubble.RADIUS + 1) * 2) {
                    fixBubble(row, col);
                    break;
                }
            }
        }
    }

    /**
     * places the moving bubble in the grid on the given position
     *
     * @param row the row-index in the grid
     * @param col the column-index in the grid
     */
    private void fixBubble(int row, int col) {
        Point temp_point = bubbles.get(row).get(col).getLocation();
        moving_bubble.setLocation(temp_point);
        bubbles.get(row).set(col, moving_bubble);
        timer.stop();
        moving_bubble.setMoving(false);
        int removed = removeCoherent(row, col) + removeFloating();
        mainFrame.updateScore(score);
        numOfBubbles -= removed;
        if (removed == 0) {
            shotCount++;
        }
        if (shotCount == 5) {
            shotCount = 0;
            addRow();
        }
        canvas.repaint();
        if (numOfBubbles == 0) {
            stop();
            score *= 1.2;
            mainFrame.gameWon(score);
        }
        for (Bubble b : bubbles.get(ROW_COUNT - 1)) {
            if (b.isVisible()) {
                stop();
                score *= 0.8;
                mainFrame.gameLost(score);
                break;
            }
        }
    }

    /**
     * adds a new row on the top of the field
     */
    private void addRow() {
        bubbles.remove(ROW_COUNT - 1);
        for (RowList r : bubbles) {
            for (Bubble b : r) {
                b.setLocation(new Point(b.getLocation().x,
                        b.getLocation().y + Constants.ROW_DISTANCE / 2));
            }
        }
        RowList newRow = new RowList(!bubbles.get(0).isFullFlag());
        for (int i = 0; i < (newRow.isFullFlag() ? 14 : 13); i++) {
            Bubble b = new Bubble(Bubble.getRandomColor(colors));
            b.setLocation(
                    new Point((newRow.isFullFlag() ?
                            i * 2 * (Bubble.RADIUS + 1) :
                            i * 2 * (Bubble.RADIUS + 1) + (Bubble.RADIUS + 1)), 0));
            b.setVisible(true);
            newRow.add(b);
            numOfBubbles++;
        }
        bubbles.add(0, newRow);
    }

    /**
     * returns the neighbours of a bubble in the grid
     *
     * @param row row-index of the bubble in the grid
     * @param col column-index of the bubble in the grid
     * @return list of the neighbouring bubbles
     */
    private ArrayList<Bubble> getNeighbours(int row, int col) {
        try {

            ArrayList<Bubble> neighbours = new ArrayList<>();
            //LEFT
            if (col > 0) neighbours.add(bubbles.get(row).get(col - 1));
            //RIGHT
            if (col < (bubbles.get(row).isFullFlag() ? COL_COUNT_FULL : COL_COUNT) - 1) {
                neighbours.add(bubbles.get(row).get(col + 1));
            }
            //UPPER LEFT
            if (bubbles.get(row).isFullFlag() && col > 0 && row > 0) {
                neighbours.add(bubbles.get(row - 1).get(col - 1));
            }
            if (!bubbles.get(row).isFullFlag() && row > 0) {
                neighbours.add(bubbles.get(row - 1).get(col));
            }
            //UPPER RIGHT
            if (bubbles.get(row).isFullFlag() && col < COL_COUNT_FULL - 1 && row > 0) {
                neighbours.add(bubbles.get(row - 1).get(col));
            }
            if (!bubbles.get(row).isFullFlag() && row > 0) {
                neighbours.add(bubbles.get(row - 1).get(col + 1));
            }
            //LOWER LEFT
            if (bubbles.get(row).isFullFlag() && col > 0 && row < ROW_COUNT - 1) {
                neighbours.add(bubbles.get(row + 1).get(col - 1));
            }
            if (!bubbles.get(row).isFullFlag() && row < ROW_COUNT - 1) {
                neighbours.add(bubbles.get(row + 1).get(col));
            }
            //LOWER RIGHT
            if (bubbles.get(row).isFullFlag() && col < COL_COUNT_FULL - 1 && row < ROW_COUNT - 1) {
                neighbours.add(bubbles.get(row + 1).get(col));
            }
            if (!bubbles.get(row).isFullFlag() && row < ROW_COUNT - 1) {
                neighbours.add(bubbles.get(row + 1).get(col + 1));
            }
            return neighbours;
        } catch (Exception e) {
            System.err.println("Could not return the neighbors due to: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * returns the distance of the two bubbles given as parameters
     *
     * @param b1 first bubble
     * @param b2 second bubble
     * @return the distance of the bubbles
     */
    public static double BubbleDist(Bubble b1, Bubble b2) {
        double x_dist = b1.getCenterLocation().x - b2.getCenterLocation().x;
        double y_dist = b1.getCenterLocation().y - b2.getCenterLocation().y;
        return Math.sqrt(Math.pow(x_dist, 2) + Math.pow(y_dist, 2));
    }

    /**
     * removes the bubbles that are coherent with the recently placed one
     *
     * @param row row-index of the placed bubble
     * @param col column-index of the placed bubble
     * @return the number of removed bubbles
     */
    private int removeCoherent(int row, int col) {
        unMarkAll();
        markColor(row, col);
        int ret = 0;
        if (countMarked() > 2) {
            ret = countMarked();
            removeMarked();
        }
        unMarkAll();
        score += (long) ret * SCORE_COHERENT;
        return ret;
    }

    /**
     * removes all bubbles that would anyway just float in the grid
     * (so not connected to the top of the field by a chain of bubbles)
     *
     * @return the number of removed bubbles
     */
    private int removeFloating() {
        markAll();
        for (Bubble b : bubbles.get(0)) {
            if (b.isVisible()) {
                unMarkNotFloating(b.getRow(), b.getCol());
            }
        }
        int ret = countMarked();
        removeMarked();
        unMarkAll();
        score += (long) ret * SCORE_FLOATING;
        return ret;
    }

    /**
     * unmarks not floating elements outgoing from the bubble gived with
     * the coordinates
     *
     * @param row row-index of the bubble
     * @param col column-index o the bubble
     */
    private void unMarkNotFloating(int row, int col) {
        bubbles.get(row).get(col).unmark();
        for (Bubble b : getNeighbours(row, col)) {
            if (b.isMarked() && b.isVisible()) {
                unMarkNotFloating(b.getRow(), b.getCol());
            }
        }
    }

    /**
     * marks the bubbles that have the same color as the one given with
     * the coordinates (these ones must be somehow connected to the
     * given one with a series of bubbles of the same color)
     *
     * @param row row-index of the bubble
     * @param col column-index of the bubble
     */
    private void markColor(int row, int col) {

        try {
            bubbles.get(row).get(col).mark();
            for (Bubble b : getNeighbours(row, col)) {
                if (b.isVisible() && !b.isMarked()) {
                    if (b.getColor().equals(bubbles.get(row).get(col).getColor())) {
                        markColor(b.getRow(), b.getCol());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark color due to: " + e.getMessage());
        }

    }

    /**
     * counts the marked bubbles in the bubble-matrix
     *
     * @return number of the marked bubbles
     */
    private int countMarked() {
        try {
            int ret = 0;
            for (RowList r : bubbles) {
                for (Bubble b : r) {
                    if (b.isMarked() && b.isVisible()) {
                        ret++;
                    }
                }
            }
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("Failed to count marked bubble due to: " + e.getMessage());
        }
    }

    /**
     * unmarks all bubbles
     */
    private void unMarkAll() {
        try {
            for (RowList r : bubbles) {
                for (Bubble b : r) {
                    b.unmark();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to unmarks bubbles due to: " + e.getMessage());
        }
    }

    /**
     * marks all bubbles
     */
    private void markAll() {
        try {
            for (RowList r : bubbles) {
                for (Bubble b : r) {
                    b.mark();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark bubbles due to: " + e.getMessage());
        }
    }

    /**
     * removes all marked bubbles
     */
    private void removeMarked() {
        try {
            for (RowList r : bubbles) {
                for (Bubble b : r) {
                    if (b.isMarked()) {
                        b.setVisible(false);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove marked bubbles due to: " + e.getMessage());
        }
    }

    /**
     * returns whether the game is stopped or running
     *
     * @return true if the game is stopped, else false
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * stops the game
     */
    public void stop() {
        stopped = true;
    }

    /**
     * returns the initial number of rows of the game
     *
     * @return inital number of rows
     */
    public int getInitialRows() {
        return initial_rows;
    }

    /**
     * returns the number of colors used in the game to color bubbles
     *
     * @return number of colors maximally used
     */
    public int getColors() {
        return colors;
    }

    /**
     * returns the score currently achieved by the player
     *
     * @return the currently achieved score
     */
    public long getScore() {
        return score;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        moving_bubble.move();
        checkProximity();
        canvas.repaint();
    }

}
