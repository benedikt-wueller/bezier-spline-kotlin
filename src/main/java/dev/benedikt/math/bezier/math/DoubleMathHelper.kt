package dev.benedikt.math.bezier.math

import kotlin.math.pow

class DoubleMathHelper : MathHelper<Double>() {

    override val zero: Double get() = 0.0
    override val one: Double get() = 1.0

    override fun plus(a: Double, b: Double) = a + b
    override fun minus(a: Double, b: Double) = a - b
    override fun times(a: Double, b: Double) = a * b
    override fun times(a: Double, b: Int) = a * b
    override fun div(a: Double, b: Double) = a / b
    override fun div(a: Double, b: Int) = a / b

    override fun max(a: Double, b: Double) = kotlin.math.max(a, b)
    override fun pow(n: Double, p: Double) = n.pow(p)

    override fun isBetween(n: Double, a: Double, b: Double) = n in a..b
    override fun negate(n: Double) = -n
}
