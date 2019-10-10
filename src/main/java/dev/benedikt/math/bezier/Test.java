package dev.benedikt.math.bezier;

import dev.benedikt.math.bezier.spline.BezierSpline;
import dev.benedikt.math.bezier.spline.DoubleBezierSpline;
import dev.benedikt.math.bezier.vector.Vector2D;

public class Test {

    public static void main(String[] args) {
        BezierSpline<Double, Vector2D> spline = new DoubleBezierSpline<>(false);

        spline.addKnot(new Vector2D(60, 60), false);
        spline.addKnot(new Vector2D(700, 240), false);
        spline.addKnot(new Vector2D(600, 100), false);
        spline.addKnot(new Vector2D(330, 390), false);

        spline.update();

        System.out.println(spline.getCoordinatesAt(0.0));
        System.out.println(spline.getCoordinatesAt(1.0));
    }
}
