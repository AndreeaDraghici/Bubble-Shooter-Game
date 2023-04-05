package implementation.joystick;

import org.intellij.lang.annotations.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by Andreea Draghici on 05.04.2023
 * Name of project: BubbleShooter
 */
public class InputAxis {

    public static final int EMPTY = -1;

    private final String name;
    private int keyCode = EMPTY, mouseCode = EMPTY;
    private Identifier[] identifiers = null;
    private float actZone = 0.0f;

    public InputAxis(String name, int keyCode, Identifier identifier, float actZone, int mouseCode) {
        this.name = name;
        this.keyCode = keyCode;
        this.identifiers = new Identifier[1];
        if (identifier == null)
            throw new NullPointerException("Identifier array should not be null, empty, or contain a null identfier!");
        this.identifiers[0] = identifier;
        this.actZone = actZone;
        this.mouseCode = mouseCode;
    }

    public InputAxis(String name, int keyCode, Identifier[] identifiers, float actZone, int mouseCode) {
        this.name = name;
        this.keyCode = keyCode;
        if (identifiers == null || identifiers.length == 0 || Arrays.asList(identifiers).contains(null))
            throw new NullPointerException("Identifier array should not be null, empty, or contain a null identfier!");
        this.identifiers = identifiers;
        this.actZone = actZone;
        this.mouseCode = mouseCode;
    }

    public InputAxis(String name, int keyCode, int mouseCode) {
        this.name = name;
        this.keyCode = keyCode;
        this.mouseCode = mouseCode;
    }

    public InputAxis(String name, int keyCode) {
        this.name = name;
        this.keyCode = keyCode;
    }

    public String getName() {
        return name;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public Identifier[] getIdentifiers() {
        return identifiers;
    }

    public float getActZone() {
        return actZone;
    }

    public int getMouseCode() {
        return mouseCode;
    }

}
