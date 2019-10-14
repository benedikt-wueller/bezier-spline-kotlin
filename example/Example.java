package dev.benedikt.math.bezier;

import dev.benedikt.math.bezier.spline.DoubleBezierSpline;
import dev.benedikt.math.bezier.vector.Vector2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Example {

    public static void main(String[] args) {
        DoubleBezierSpline<Vector2D> spline = new DoubleBezierSpline<>(false);

        spline.addKnots(
                new Vector2D(60, 60),
                new Vector2D(700, 240),
                new Vector2D(600, 100),
                new Vector2D(330, 390)
        );

        BufferedImage image = new BufferedImage(850, 550, BufferedImage.TYPE_4BYTE_ABGR);

        int pixels = 10000;
        int tangents = 35;

        for (int i = 0; i < pixels; i++) {
            Vector2D coordinates = spline.getCoordinatesAt(1.0 / pixels * i).plus(50);
            drawPixel(image, coordinates, Color.BLACK);
        }

        for (int i = 0; i < tangents; i++) {
            Vector2D coordinates = spline.getCoordinatesAt(1.0 / tangents * i).plus(50);
            Vector2D tangent = spline.getTangentAt(1.0 / tangents * i).times(55);
            drawLine(image, coordinates, tangent, Color.RED);
        }

        try {
            ImageIO.write(image, "png", new File("./result.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawPixel(BufferedImage image, Vector2D coordinates, Color color) {
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();

        image.setRGB(x, y, color.getRGB());
        image.setRGB(x + 1, y, color.getRGB());
        image.setRGB(x, y + 1, color.getRGB());
    }

    private static void drawLine(BufferedImage image, Vector2D origin, Vector2D delta, Color color) {
        int distance = (int) Math.ceil(origin.distanceTo(delta));

        for (int i = 1; i <= distance; i++) {
            drawPixel(image, origin.plus(delta.div(distance).times(i)), color);
        }
    }
}
