package dev.benedikt.math.bezier;

import dev.benedikt.math.bezier.spline.DoubleBezierSpline;
import dev.benedikt.math.bezier.vector.Vector2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class Example {

    public static void main(String[] args) {
        DoubleBezierSpline<Vector2D> spline = new DoubleBezierSpline<>(true);

        spline.addKnots(
                new Vector2D(60, 60),
                new Vector2D(700, 240),
                new Vector2D(600, 100),
                new Vector2D(330, 390)
        );

        int pixels = 10000;
        int frames = 400;

        File file = new File("./example/result.gif");
        try {
            ImageOutputStream output = new FileImageOutputStream(file);
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_3BYTE_BGR, 30, true);

            for (int i = 0; i < frames; i++) {
                writer.writeToSequence(drawFrame(spline, pixels, 1.0 / frames * i));
            }

            writer.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage drawFrame(DoubleBezierSpline<Vector2D> spline, int pixels, double t) {
        BufferedImage image = new BufferedImage(850, 550, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(Color.WHITE);
        graphics.fillRect( 0, 0, image.getWidth(), image.getHeight());

        for (int i = 0; i < pixels; i++) {
            Vector2D coordinates = spline.getCoordinatesAt(1.0 / pixels * i).plus(50);
            drawPixel(image, coordinates, Color.BLACK);
        }

        Vector2D coordinates = spline.getCoordinatesAt(t).plus(50);
        Vector2D tangent = spline.getTangentAt(t).times(120);
        drawLine(image, coordinates.minus(tangent.div(2)), tangent, Color.RED);

        return image;
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
