package dev.benedikt.math.bezierspline;

import dev.benedikt.math.bezierspline.vector.Vector3D;

public class Test {

    public static void main(String[] args) {
        DoubleBezierSpline<Vector3D> spline = new DoubleBezierSpline<>(true);

        spline.addKnots(
                new Vector3D(0.06, 0.06, 0.06),
                new Vector3D(0.7, 0.24, 0.5),
                new Vector3D(0.6, 0.1, 0.35),
                new Vector3D(0.33, 0.39, 0.4)
        );
    }
}
