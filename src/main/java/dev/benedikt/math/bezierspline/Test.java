package dev.benedikt.math.bezierspline;

import dev.benedikt.math.bezierspline.vector.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private List<String> list = new ArrayList<>();

    public static void main(String[] args) {
        DoubleBezierSpline<Vector2D> spline = new DoubleBezierSpline<>(false);

        spline.addKnots(
                new Vector2D(100, 100),
                new Vector2D(200, 200),
                new Vector2D(100, 300),
                new Vector2D(500, 400)
        );

        System.out.println(spline.getControlPoints());
    }
}
