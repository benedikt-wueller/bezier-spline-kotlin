package dev.benedikt.math.bezierspline.matrix

import dev.benedikt.math.bezierspline.vector.VectorD

class ThomasDoubleMatrix<V : VectorD<V>> : ThomasMatrix<Double, V>() {

    override val zero = 0.0

    override fun plus(a: Double, b: Double) = a + b
    override fun minus(a: Double, b: Double) = a - b
    override fun times(a: Double, b: Double) = a * b
    override fun div(a: Double, b: Double) = a / b

    override fun negate(n: Double) = -n

}
