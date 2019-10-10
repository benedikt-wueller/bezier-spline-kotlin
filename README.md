kotlin-bezier-spline
====================

A generic cubic bezier spline implementation for Kotlin and Java [WIP].

Getting Started
---------------

You can create open or closed bezier splines for any amount of dimensions using the provided `FloatBezierSpline`,
`DoubleBezierSpline` and `Vector` implementations. You may also create a custom bezier spline implementation (see
below).

```java
BezierSpline<Double, Vector3D> spline = new DoubleBezierSpline<>(true); // true = closed

spline.addKnot(new Vector3D(0.06, 0.06, 0.06));
spline.addKnot(new Vector3D(0.7, 0.24, 0.5));
spline.addKnot(new Vector3D(0.6, 0.1, 0.35));
spline.addKnot(new Vector3D(0.33, 0.39, 0.4));

Vector3D coordinates = spline.getCoordinatesAt(0.5);
Vector3D tangent = spline.getTangentAt(0.5);
```

### Manual Updates

The required and _potentially_ heavy length estimation happens every time a knot is added or removed.
You may want to reduce the workload by updating the spline manually.

```java
spline.addKnot(new Vector3D(0.06, 0.06, 0.06), false);
spline.addKnot(new Vector3D(0.7, 0.24, 0.5), false);
spline.addKnot(new Vector3D(0.6, 0.1, 0.35), false);
spline.addKnot(new Vector3D(0.33, 0.39, 0.4), false);

// ...

spline.update();
);
```

**Note**: You may only receive coordinates, tangents or control points after am update has been triggered, either
manually or automatically.

### Increase Speed and/or Accuracy

To modify the computation speed or accuracy you may pass a `resolution` integer value as second construction parameter.
This parameter determines in how many pieces to slice each spline section to estimate the partial and total lengths of
the bezier spline, which are used for further calculations.

Helper constants are provided by the `Resolution` object. However, you are free to enter custom integer values to tune
speed and accuracy.

```java
BezierSpline<Double, Vector3D> spline = new DoubleBezierSpline<>(false, Resolution.FASTEST);
```

### Adjusting Weights

To adjust the weights used to calculate the control points, you can pass the `minWeight` as third construction
parameter. The parameter defaults to the smallest possible positive value above zero of the given number type.

**Note**: The minimum allowed weight must always be greater than zero for the calculations to work.
