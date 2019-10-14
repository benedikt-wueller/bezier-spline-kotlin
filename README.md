bezier-spline-kotlin
====================

A generic multi-order bezier curve and cubic bezier spline implementation for Java/Kotlin.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.benedikt.math/bezier-spline/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.benedikt.math/bezier-spline)

Installation
------------

```xml
<dependency>
  <groupId>dev.benedikt.math</groupId>
  <artifactId>bezier-spline</artifactId>
  <version>1.0</version>
</dependency>
```

Usage
-----

**Note**: Check out the [BezierSpline](https://github.com/Bw2801/bezier-spline-kotlin/wiki/BezierSpline) documentation for further options.

You can create open or closed bezier splines for any amount of dimensions using the provided `FloatBezierSpline`,
`DoubleBezierSpline` and `Vector` implementations. You may also create a custom bezier spline implementation.

```java
BezierSpline<Double, Vector3D> spline = new DoubleBezierSpline<>(true);

spline.addKnot(new Vector3D(0.06, 0.06, 0.06));
spline.addKnot(new Vector3D(0.7, 0.24, 0.5));
spline.addKnot(new Vector3D(0.6, 0.1, 0.35));
spline.addKnot(new Vector3D(0.33, 0.39, 0.4));

double length = spline.getComputedLength();
Vector3D coordinates = spline.getCoordinatesAt(0.5);
Vector3D tangent = spline.getTangentAt(0.5);
```

The required and potentially heavy length estimation happens the first time lengths, coordinates, tangents or control points are queried. You can
manually update the spline to move the workload.

```java
spline.addKnot(new Vector3D(0.06, 0.06, 0.06));
spline.addKnot(new Vector3D(0.7, 0.24, 0.5));
spline.addKnot(new Vector3D(0.6, 0.1, 0.35));
spline.addKnot(new Vector3D(0.33, 0.39, 0.4));

// ...

spline.compute(); // calculations happen here.

// ...

double length = spline.getComputedLength(); // without performance impact
Vector3D coordinates = spline.getCoordinatesAt(0.5);
Vector3D tangent = spline.getTangentAt(0.5);
```

**Note**: In one way or another an update has to be triggered in order to be able to receive coordinates, tangents or
control points.
