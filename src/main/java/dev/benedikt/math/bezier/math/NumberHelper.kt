package dev.benedikt.math.bezier.math

/**
 * This is a helper class to allow for generic maths in the bezier splines and thomas matrices.
 */
abstract class NumberHelper<N : Number> {

    abstract val zero: N
    abstract val one: N

    @Suppress("LeakingThis") val two = this.plus(this.one, this.one)
    @Suppress("LeakingThis") val three = this.plus(this.two, this.one)

    abstract fun plus(a: N, b: N) : N
    abstract fun minus(a: N, b: N) : N
    abstract fun times(a: N, b: N) : N
    abstract fun times(a: N, b: Int) : N
    abstract fun div(a: N, b: N) : N
    abstract fun div(a: N, b: Int) : N

    abstract fun max(a: N, b: N) : N
    abstract fun pow(n: N, p: N) : N

    abstract fun isBetween(n: N, a: N, b: N) : Boolean
    abstract fun negate(n: N) : N
}
