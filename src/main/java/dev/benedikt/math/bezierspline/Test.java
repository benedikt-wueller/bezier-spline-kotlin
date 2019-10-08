package dev.benedikt.math.bezierspline;

import dev.benedikt.math.bezierspline.vector.Vector2D;

public class Test {

    public static void main(String[] args) {
        DoubleBezierSpline<Vector2D> spline = new DoubleBezierSpline<>(true);

        spline.addKnots(
                new Vector2D(0.06, 0.06),
                new Vector2D(0.7, 0.24),
                new Vector2D(0.6, 0.1),
                new Vector2D(0.33, 0.39)
        );
    }
}
