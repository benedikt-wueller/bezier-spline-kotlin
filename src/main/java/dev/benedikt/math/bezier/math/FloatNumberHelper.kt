package dev.benedikt.math.bezier.math

import kotlin.math.pow
import kotlin.math.roundToInt

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
