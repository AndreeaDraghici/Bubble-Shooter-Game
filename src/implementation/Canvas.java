package implementation;

import model.Game;
import model.score.HighscoreEntry;
import model.score.Highscores;
import utils.Constants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import static utils.Constants.BUBBLE_SHOOTER_SCORE_TEXT;

public class Canvas extends JPanel implements
        MouseMotionListener, MouseListener, ActionListener {


    private Arrow arrow;
    private Game game;
    private JLayeredPane lPane;
    private JPanel highscorePanel;
    private JPanel namePanel;
    private JLabel resultText;
    private JTextField textField;
    private Highscores highscores;
    private JTable highscoreTable;
    private JScrollPane scrollPane;

    /**
     * constructor for the class. sets of the table that displayes
     * the highscores, a layer to mildly blur the underlying
     * bubbles and a dialog for asking for a name of the player
     * reached the toplist
     */
    public Canvas() {
        setLayout(new BorderLayout());
        setPreferredSize(
                new Dimension(Constants.FIELD_SIZE_X,
                        Constants.FIELD_SIZE_Y));
        setBorder(BorderFactory.createEmptyBorder());
        addMouseMotionListener(this);
        addMouseListener(this);
        setOpaque(true);
        arrow = new Arrow();

        //blured background
        lPane = new JLayeredPane();
        lPane.setBackground(new Color(0, 255, 184, 255));
        JPanel blur = new JPanel();
        blur.setBackground(new Color(0, 250, 209, 255));

        blur.setBounds(0, 0, Constants.FIELD_SIZE_X, Constants.FIELD_SIZE_Y);

        //highscore panel

        highscorePanel = new JPanel();
        highscorePanel.setBackground(new Color(highscorePanel.getBackground().getRed(),
                highscorePanel.getBackground().getGreen(),
                highscorePanel.getBackground().getRed(),
                120));

        highscorePanel.setBounds(40, 20, Constants.FIELD_SIZE_X - 2 * 40, Constants.FIELD_SIZE_Y - 2 * 30);
        highscorePanel.setLayout(new BorderLayout());

        highscores = new Highscores();


        // highscores table
        highscoreTable = new JTable();
        highscoreTable.setBackground(new Color(250, 2, 33, 70)); //highscore table background

        highscoreTable.setFillsViewportHeight(true);
        highscoreTable.setModel(highscores);
        highscoreTable.getTableHeader().setReorderingAllowed(false);
        scrollPane = new JScrollPane(highscoreTable);
        highscorePanel.add(scrollPane, BorderLayout.CENTER);

        lPane.add(blur, JLayeredPane.DEFAULT_LAYER);
        lPane.add(highscorePanel, JLayeredPane.PALETTE_LAYER);

        namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.setBounds(80, 60, Constants.FIELD_SIZE_X - 2 * 80, 185);
        namePanel.setBorder(BorderFactory.createLineBorder(Color.red));

        JPanel subNamePanel = new JPanel();
        subNamePanel.setLayout(new BoxLayout(subNamePanel, BoxLayout.Y_AXIS));
        subNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        resultText = new JLabel("Result");
        resultText.setFont(new Font(resultText.getFont().getName(), Font.ITALIC, 30));
        resultText.setAlignmentX(CENTER_ALIGNMENT);
        resultText.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel please = new JLabel("<html><div style=\"text-align: center;\">Please enter your name and click the button to proceed!</html>");
        please.setFont(new Font(please.getFont().getName(), Font.PLAIN, 13));
        please.setAlignmentX(CENTER_ALIGNMENT);
        please.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        please.setBackground(new Color(0, 250, 209, 255));

        textField = new JTextField(20);
        JButton button = new JButton("Enter");
        button.addActionListener(this);

        JPanel formContainer = new JPanel();
        formContainer.add(textField);
        formContainer.add(button);

        subNamePanel.add(resultText);
        subNamePanel.add(please);
        subNamePanel.add(formContainer);
        namePanel.add(subNamePanel, BorderLayout.CENTER);

    }

    /**
     * triggers the repainting of the panel with the highscores
     * displayed. optionally adds a dialog for asking the name
     * of the player. if the parameter score is unequal zero then
     * the player has reached the toplist. if it equals zero, the
     * dialog isn't displayed.
     *
     * @param score the score reached by the player, or 0
     * @param win   true if the player won the game, else false
     */
    public void displayHighscore(long score, boolean win) {

        resultText.setText(win ? "You win!" : "You lose");
        if (score != 0) {
            lPane.add(namePanel, JLayeredPane.DRAG_LAYER);
            lPane.setBackground(new Color(0, 250, 209, 255));

        }
        add(lPane);
        loadHighscores();
        highscoreTable.setModel(highscores);
        repaint();
    }

    /**
     * instantiates a new game object and repaints the left panel
     * with the game displayed
     *
     * @param row   the initial number of rows
     * @param color the number of colors to be used
     */
    public void newGame(int row, int color) {
        game = new Game(row, color, this);
        lPane.remove(namePanel);
        remove(lPane);
        repaint();
    }

    /**
     * getter for the game object
     *
     * @return the game that is actually being played
     */
    public Game getGame() {
        return game;
    }

    /**
     * writes the highscores to a file named "bubble_shooter_score.text"
     */
    private void saveHighscores() {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(BUBBLE_SHOOTER_SCORE_TEXT));
            os.writeObject(highscores);
            os.close();
        } catch (Exception e) {
            System.err.println("Failed to save highscores!");
        }
    }

    /**
     * reads the highscores from the fie named "bubble_shooter_score.text"
     * if it exists
     */
    private void loadHighscores() {
        try {
            File file = new File(BUBBLE_SHOOTER_SCORE_TEXT);
            if (file.exists()) {
                ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));
                highscores = (Highscores) os.readObject();
                os.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to load highscores!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(
                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));
        if (game != null) {
            game.paintBubbles(g2d);
        }
        arrow.paintComponent(g2d, getLocationOnScreen());
    }

    ;

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent arg0) {
        mouseMoved(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent arg0) {
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (game != null) {
            if (!game.isStopped()) {
                game.fire(MouseInfo.getPointerInfo().getLocation(), getLocationOnScreen());
                repaint();
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!textField.getText().equals("")) {
            highscores.addEntry(new HighscoreEntry(textField.getText(),
                    game.getScore(), game.getInitialRows(), game.getColors()));
            saveHighscores();
            lPane.remove(namePanel);
            displayHighscore(0, true);
        }
    }
}
