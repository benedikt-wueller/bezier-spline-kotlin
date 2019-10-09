kotlin-bezier-spline
====================

A generic cubic bezier spline implementation for Kotlin and Java [WIP].

**Note**: currently only the computation of control points is implemented.

Example
-------

The following snippet creates a closed (indicated by the `true` value in the constructor) bezier spline.

```java
DoubleBezierSpline<Vector3D> spline = new DoubleBezierSpline<>(true);

spline.addKnots(
        new Vector3D(0.06, 0.06, 0.06),
        new Vector3D(0.7, 0.24, 0.5),
        new Vector3D(0.6, 0.1, 0.35),
        new Vector3D(0.33, 0.39, 0.4)
);
```

Custom number types and vectors
-------------------------------

Everything in this library is generic. You may create splines for any number type and as many dimensions as you like.
In this example, a Bezier Spline for the number type `Float` is implemented.

First you will have to crate a custom `Vector` implementation. Based on the current naming scheme, this would be a
`Vector3F` where the `3` indicates the number of dimensions and `F` is the shorthand for the data type.

**Note**: You should create a generic `VectorF` interface first which is then extended by `Vector3F` to allow full
generic use of the bezier spline.

```kotlin
interface VectorF<V : VectorF<V>> : Vector<Float, V>

class Vector3F(var x: Float = 0.0f, var y: Float = 0.0f, var z: Float = 0.0f) : VectorF<Vector3F> {

    override operator fun plus(other: Vector3F) = Vector3F(this.x + other.x, this.y + other.y, this.z + other.z)
    override operator fun plus(value: Float) = Vector3F(this.x + value, this.y + value, this.z + value)

    override operator fun minus(other: Vector3F) = Vector3F(this.x - other.x, this.y - other.y, this.z - other.z)
    override operator fun minus(value: Float) = Vector3F(this.x - value, this.y - value, this.z - value)

    override operator fun times(other: Vector3F) = Vector3F(this.x * other.x, this.y * other.y, this.z * other.z)
    override operator fun times(value: Float) = Vector3F(this.x * value, this.y * value, this.z * value)

    override operator fun div(other: Vector3F) = Vector3F(this.x / other.x, this.y / other.y, this.z / other.z)
    override operator fun div(value: Float) = Vector3F(this.x / value, this.y / value, this.z / value)

    override fun distanceTo(other: Vector3F) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2) + (this.z - other.z).pow(2))

}
```

Next you would need to create a `ThomasMatrix` implementation.

```kotlin
class ThomasFloatMatrix<V : VectorF<V>> : ThomasMatrix<Float, V>() {

    override val zero = 0.0f

    override fun plus(a: Float, b: Float) = a + b
    override fun minus(a: Float, b: Float) = a - b
    override fun times(a: Float, b: Float) = a * b
    override fun div(a: Float, b: Float) = a / b

    override fun negate(n: Float) = -n

}
```

Finally you can create a new `BezierSpline` implementation using the custom `Vector` and `ThomasMatrix` implementations.

```kotlin
class FloatBezierSpline<V : VectorF<V>> @JvmOverloads constructor(closed: Boolean = false, override val minWeight: VectorF = 1.0f)
    : BezierSpline<Float, V>(closed) {

    override val zero: Float = 0.0f
    override val one: Float = 1.0f

    override fun createMatrix() = ThomasFloatMatrix<V>()

    override fun plus(a: Float, b: Float) = a + b
    override fun minus(a: Float, b: Float) = a - b
    override fun times(a: Float, b: Float) = a * b
    override fun div(a: Float, b: Float) = a / b

    override fun max(a: Float, b: Float) = kotlin.math.max(a, b)
    override fun pow(n: Float, p: Float) = n.pow(p)
}
```

And that's it. You can now use the `FloatBezierSpline` with any class extending the `VectorF` interface.
