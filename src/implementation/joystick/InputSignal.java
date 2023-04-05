package implementation.joystick;

import model.iface.IControllable;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.intellij.lang.annotations.Identifier;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Andreea Draghici on 05.04.2023
 * Name of project: BubbleShooter
 */
public class InputSignal implements KeyListener, MouseMotionListener, MouseListener, FocusListener {


    private static final int NUM_KEYS = KeyEvent.KEY_LAST;
    private static final int NUM_MBTNS = MouseEvent.MOUSE_LAST;

    private boolean[] keys = new boolean[NUM_KEYS];
    private boolean[] lastKeys = new boolean[NUM_KEYS];
    private boolean[] mouseButtons = new boolean[NUM_MBTNS];
    private boolean[] lastMouseButtons = new boolean[NUM_MBTNS];

    private int mouseX = 0, mouseY = 0;

    private Controller activeController = null;
    private HashMap<Identifier, Float> controllerAxes = new HashMap<>();
    private HashMap<Identifier, Float> lastControllerAxes = new HashMap<>();
    private List<String> currentAxesPressed = new ArrayList<>();

    private boolean hasFocus = false;
    private boolean useXInputController = false;

    private IControllable controllable;

    private InputAxis[] inputAxes;

    public InputSignal(IControllable controllable, boolean useXInputController) {
        this.useXInputController = useXInputController;
        setControllable(controllable);
        scanControllers();
    }

    public InputSignal() {

    }

    private void scanControllers() {
        if (usingController()) {
            if (!activeController.poll()) {
                activeController = null;
                System.out.println("Controller disconnected.");
            }
            return;
        }
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i].getType() == Controller.Type.GAMEPAD) {
                activeController = ca[i];
                break;
            }
        }
        if (!usingController()) {
            System.out.println("No gamepad detected.");
        } else {
            System.out.println("Gamepad detected: " + activeController.getName());
            net.java.games.input.Component[] components = activeController.getComponents();
            controllerAxes.clear();
            lastControllerAxes.clear();
            for (net.java.games.input.Component c : components) {
                controllerAxes.put((Identifier) c.getIdentifier(), 0.0f);
                lastControllerAxes.put((Identifier) c.getIdentifier(), 0.0f);
            }
        }
    }

    public void setControllable(IControllable controllable) {
        if (this.controllable != null) {
            this.controllable.getComponent().removeKeyListener(this);
            this.controllable.getComponent().removeMouseMotionListener(this);
            this.controllable.getComponent().removeMouseListener(this);
            this.controllable.getComponent().removeFocusListener(this);
        }
        this.controllable = controllable;
        this.controllable.getComponent().addKeyListener(this);
        this.controllable.getComponent().addMouseMotionListener(this);
        this.controllable.getComponent().addMouseListener(this);
        this.controllable.getComponent().addFocusListener(this);
    }

    public boolean getInput(String name) {
        InputAxis axis = getInputAxisFromName(name);

        if (!hasFocus) {
            return false;
        }

        if (axis.getKeyCode() != InputAxis.EMPTY && (keys[axis.getKeyCode()])) {
            return true;
        }

        if (axis.getMouseCode() != InputAxis.EMPTY && (mouseButtons[axis.getMouseCode()])) {
            return true;
        }

        if (usingController()) {
            float value = 0.0f;
            for (Identifier identifier : axis.getIdentifiers()) {
                if (identifier != null) {
                    value = controllerAxes.get(identifier);
                    if (axis.getActZone() > 0) {
                        if (value >= axis.getActZone())
                            return true;
                    } else {
                        if (value <= axis.getActZone())
                            return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() >= mouseButtons.length)
            return;
        mouseButtons[e.getButton()] = true;
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() >= mouseButtons.length)
            return;
        mouseButtons[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        handleMouse(e);
    }

    public void mouseMoved(MouseEvent e) {
        handleMouse(e);
    }

    private void handleMouse(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() >= keys.length)
            return;
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() >= keys.length)
            return;
        keys[e.getKeyCode()] = false;
    }

    public void focusGained(FocusEvent e) {
        hasFocus = true;
    }

    public void focusLost(FocusEvent e) {
        for (int i = 0; i < NUM_KEYS; i++) {
            keys[i] = false;
        }
        for (int i = 0; i < NUM_MBTNS; i++) {
            mouseButtons[i] = false;
        }
        hasFocus = false;
    }

    public boolean usingController() {
        return activeController != null && useXInputController;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public InputAxis getInputAxisFromName(String name) {
        if (inputAxes != null) {
            for (InputAxis inputAx : inputAxes) {
                if (inputAx.getName().equalsIgnoreCase(name)) {
                    return inputAx;
                }
            }
        }
        System.out.println(("Cannot get axis from name: " + name));
        return null;
    }

    private void updateAxesPressed() {
        currentAxesPressed.clear();
        if (inputAxes != null) {
            for (InputAxis a : inputAxes) {
                if (getInput(a.getName())) {
                    currentAxesPressed.add(a.getName());
                }
            }
        }
    }
}
