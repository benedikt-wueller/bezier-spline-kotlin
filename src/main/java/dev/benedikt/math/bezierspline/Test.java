package dev.benedikt.math.bezierspline;

import dev.benedikt.math.bezierspline.vector.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private List<String> list = new ArrayList<>();

    public static void main(String[] args) {
        DoubleBezierSpline<Vector2D> spline = new DoubleBezierSpline<>(false);

        spline.addKnots(
                new Vector2D(60, 60),
                new Vector2D(700, 240),
                new Vector2D(600, 100),
                new Vector2D(330, 390)
        );

        System.out.println(spline.toPath());
    }
}
