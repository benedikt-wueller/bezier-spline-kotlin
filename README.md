bezier-spline-kotlin
====================

A generic cubic bezier spline implementation for Java/Kotlin.

Installation
------------

TODO

Usage
-----

**Note**: Check out the [BezierSpline](https://github.com/Bw2801/bezier-spline-kotlin/wiki/BezierSpline) documentation for further options.

You can create open or closed bezier splines for any amount of dimensions using the provided `FloatBezierSpline`,
`DoubleBezierSpline` and `Vector` implementations. You may also create a custom bezier spline implementation.

```java
BezierSpline<Double, Vector3D> spline = new DoubleBezierSpline<>(true); // true = closed

spline.addKnot(new Vector3D(0.06, 0.06, 0.06));
spline.addKnot(new Vector3D(0.7, 0.24, 0.5));
spline.addKnot(new Vector3D(0.6, 0.1, 0.35));
spline.addKnot(new Vector3D(0.33, 0.39, 0.4));

Vector3D coordinates = spline.getCoordinatesAt(0.5);
Vector3D tangent = spline.getTangentAt(0.5);
```

The required and _potentially_ heavy length estimation happens every time a knot is added or removed.
You may want to reduce the workload by updating the spline manually.

```java
spline.addKnot(new Vector3D(0.06, 0.06, 0.06), false);
spline.addKnot(new Vector3D(0.7, 0.24, 0.5), false);
spline.addKnot(new Vector3D(0.6, 0.1, 0.35), false);
spline.addKnot(new Vector3D(0.33, 0.39, 0.4), false);

// ...

spline.update();
```

**Note**: In one way or another an update has to be triggered in order to be able to receive coordinates, tangents or
control points.
