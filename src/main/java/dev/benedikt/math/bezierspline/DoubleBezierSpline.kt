package dev.benedikt.math.bezierspline

import dev.benedikt.math.bezierspline.matrix.ThomasDoubleMatrix
import dev.benedikt.math.bezierspline.vector.VectorD
import kotlin.math.pow

class DoubleBezierSpline<V : VectorD<V>> @JvmOverloads constructor(closed: Boolean = false, override val minWeight: Double = 1.0)
    : BezierSpline<Double, V>(closed) {

    override val zero: Double = 0.0
    override val one: Double = 1.0

    override fun createMatrix() = ThomasDoubleMatrix<V>()

    override fun plus(a: Double, b: Double) = a + b
    override fun minus(a: Double, b: Double) = a - b
    override fun times(a: Double, b: Double) = a * b
    override fun div(a: Double, b: Double) = a / b

    override fun max(a: Double, b: Double) = kotlin.math.max(a, b)
    override fun square(n: Double) = n.pow(2)
}
