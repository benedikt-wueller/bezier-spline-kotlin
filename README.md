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
```

**Note**: In one way or another an update has to be triggered in order to be able to receive coordinates, tangents or
control points.

### Adjusting Speed and Accuracy

To modify the computation speed or accuracy you may pass a `resolution` integer value as second construction parameter.
This parameter determines in how many pieces to slice each spline section to estimate the partial and total lengths of
the bezier spline, which are used for further calculations.

Helper constants are provided by the `Resolution` object. However, you are free to enter custom integer values above
zero to tune speed and accuracy individually.

```java
BezierSpline<Double, Vector3D> spline = new DoubleBezierSpline<>(false, Resolution.FASTEST);
```

### Adjusting Weights

To adjust the weights used to calculate the control points, you can pass the `minWeight` as third construction
parameter. The parameter defaults to the smallest possible positive value above zero of the given number type.

**Note**: The minimum allowed weight must always be greater than zero for the calculations to work.


Custom Bezier Spline
--------------------

If you need bezier splines for other number types than doubles or floats, you can create a custom `BezierSpline`
implementation. This example is illustrated by the implementation of the `FloatBezierSpline`.

**Note**: The examples are written in Kotlin, because this is the original code. Obviously, everything can be
implemented using Java as well.

First you need to create a implementation of `NumberHelper` for your number type. This class is used to provide the
required mathematical functions for generic number calculations.

```kotlin
class FloatNumberHelper : NumberHelper<Float>() {

    override val zero: Float get() = 0.0f
    override val one: Float get() = 1.0f

    override fun plus(a: Float, b: Float) = a + b
    override fun minus(a: Float, b: Float) = a - b
    override fun times(a: Float, b: Float) = a * b
    override fun times(a: Float, b: Int) = a * b
    override fun div(a: Float, b: Float) = a / b
    override fun div(a: Float, b: Int) = a / b

    override fun max(a: Float, b: Float) = kotlin.math.max(a, b)
    override fun pow(n: Float, p: Float) = n.pow(p)

    override fun isBetween(n: Float, a: Float, b: Float) = n in a..b
    override fun negate(n: Float) = -n
}
```

Next you need to create a class implementing the `Vector` interface. Basically you could use the interface to wrap any
object, as long as you map the mathematical operations. For this example, the `Vector2F` implementation is used.

```kotlin
class Vector2F(var x: Float = 0.0f, var y: Float = 0.0f) : Vector<Float, Vector2F> {

    override operator fun plus(other: Vector2F) = Vector2F(this.x + other.x, this.y + other.y)
    override operator fun plus(value: Float) = Vector2F(this.x + value, this.y + value)

    override operator fun minus(other: Vector2F) = Vector2F(this.x - other.x, this.y - other.y)
    override operator fun minus(value: Float) = Vector2F(this.x - value, this.y - value)

    override operator fun times(other: Vector2F) = Vector2F(this.x * other.x, this.y * other.y)
    override operator fun times(value: Float) = Vector2F(this.x * value, this.y * value)

    override operator fun div(other: Vector2F) = Vector2F(this.x / other.x, this.y / other.y)
    override operator fun div(value: Float) = Vector2F(this.x / value, this.y / value)

    override fun distanceTo(other: Vector2F) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2))
    override fun magnitude() = sqrt(this.x * this.x + this.y * this.y)
}
```

The last (and simplest) step is to create the final `BezierSpline` implementation.

```kotlin
class FloatBezierSpline<V : Vector<Float, V>>
@JvmOverloads constructor(closed: Boolean = false, resolution: Int = Resolution.BALANCED, override val minWeight: Float = Float.MIN_VALUE)
    : BezierSpline<Float, V>(closed, resolution) {

    override val numberHelper: NumberHelper<Float> get() = FloatNumberHelper()
}
```

That's it. The custom implementation is now usable exactly like any other.
