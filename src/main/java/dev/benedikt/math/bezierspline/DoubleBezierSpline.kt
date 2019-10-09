package dev.benedikt.math.bezierspline

import dev.benedikt.math.bezierspline.matrix.ThomasDoubleMatrix
import dev.benedikt.math.bezierspline.vector.VectorD
import kotlin.math.pow
import kotlin.math.roundToInt

class DoubleBezierSpline<V : VectorD<V>> @JvmOverloads constructor(closed: Boolean = false, override val minWeight: Double = 1.0)
    : BezierSpline<Double, V>(closed) {

    override val zero: Double = 0.0
    override val one: Double = 1.0

    override fun createMatrix() = ThomasDoubleMatrix<V>()

    override fun isBetween(a: Double, b: Double, c: Double) = a in b..c
    override fun isSmallerThan(a: Double, b: Double) = a < b

    override fun plus(a: Double, b: Double) = a + b
    override fun minus(a: Double, b: Double) = a - b
    override fun times(a: Double, b: Double) = a * b
    override fun times(a: Double, b: Int) = a * b
    override fun div(a: Double, b: Double) = a / b
    override fun div(a: Double, b: Int) = a / b

    override fun max(a: Double, b: Double) = kotlin.math.max(a, b)
    override fun pow(n: Double, p: Double) = n.pow(p)

    override fun round(n: Double) = n.roundToInt()
}
