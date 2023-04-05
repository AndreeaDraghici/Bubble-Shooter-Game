package implementation;

import implementation.joystick.InputSignal;
import utils.Constants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;

public class Arrow extends InputSignal{

    private Point point;
    private static final int TIP_LENGTH = 50;
    private static final int LENGTH = 100;

    public Arrow() {
        super();
        point = new Point(Constants.FIELD_SIZE_X / 2, 0);
    }

    /**
     *
     * @param graphics -
     * @param coordinate -
     */
    public void paintComponent(Graphics2D graphics, Point coordinate) {

        graphics.setColor(Color.MAGENTA);

        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        int x = mouseLocation.x - coordinate.x;
        int y = mouseLocation.y - coordinate.y;

        if ((0 <= x) && (x < Constants.FIELD_SIZE_X) && (0 <= y) && (y < Constants.FIELD_SIZE_Y)) {
            point = mouseLocation;
        }

        x = point.x - coordinate.x;
        y = point.y - coordinate.y;

        double angle = Math.atan((double) (x - Constants.FIELD_SIZE_X / 2) / (Constants.FIELD_SIZE_Y - y));

        drawArrowLines(graphics, angle);
    }

    private static void drawArrowLines(Graphics2D graphics2D, double angle) {

        graphics2D.rotate(angle, Constants.FIELD_SIZE_X / 2, Constants.FIELD_SIZE_Y);
        
        graphics2D.drawLine(Constants.FIELD_SIZE_X / 2, Constants.FIELD_SIZE_Y,
                Constants.FIELD_SIZE_X / 2, Constants.FIELD_SIZE_Y - LENGTH);

        graphics2D.drawLine(Constants.FIELD_SIZE_X / 2, Constants.FIELD_SIZE_Y - LENGTH,
                Constants.FIELD_SIZE_X / 2 - TIP_LENGTH, Constants.FIELD_SIZE_Y - LENGTH + TIP_LENGTH);

        graphics2D.drawLine(Constants.FIELD_SIZE_X / 2, Constants.FIELD_SIZE_Y - LENGTH,
                Constants.FIELD_SIZE_X / 2 + TIP_LENGTH, Constants.FIELD_SIZE_Y - LENGTH + TIP_LENGTH);

        graphics2D.rotate(-angle, Constants.FIELD_SIZE_X / 2, Constants.FIELD_SIZE_Y);
    }
}