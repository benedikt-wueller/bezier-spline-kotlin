package dev.benedikt.math.bezier.math

/**
 * A helper class to allow for generic maths in the bezier splines and thomas matrices.
 */
abstract class MathHelper<N : Number> {

    abstract val zero: N
    abstract val one: N

    abstract fun plus(a: N, b: N) : N
    abstract fun minus(a: N, b: N) : N
    abstract fun times(a: N, b: N) : N
    abstract fun times(a: N, b: Int) : N
    abstract fun div(a: N, b: N) : N
    abstract fun div(a: N, b: Int) : N

    abstract fun max(a: N, b: N) : N
    abstract fun pow(n: N, p: N) : N
    abstract fun pow(n: N, p: Int) : N

    abstract fun isBetween(n: N, a: N, b: N) : Boolean
}
