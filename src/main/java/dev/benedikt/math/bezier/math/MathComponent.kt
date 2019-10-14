package dev.benedikt.math.bezier.math

abstract class MathComponent<N : Number>(private val helper: MathHelper<N>) {

    protected val zero = this.helper.zero
    protected val one = this.helper.one
    protected val two = this.one + this.one
    protected val three = this.one + this.two
    protected val six = this.three + this.three

    protected fun max(a: N, b: N) = this.helper.max(a, b)
    protected fun isBetween(n: N, a: N, b: N) = this.helper.isBetween(n, a, b)

    protected fun pow(n: N, p: N) = this.helper.pow(n, p)
    protected fun pow(n: N, p: Int) = this.helper.pow(n, p)
    protected fun square(n: N) = this.helper.pow(n, this.two)
    protected fun cube(n: N) = this.helper.pow(n, this.three)

    protected operator fun N.plus(other: N) = helper.plus(this, other)
    protected operator fun N.minus(other: N) = helper.minus(this, other)
    protected operator fun N.times(other: N) = helper.times(this, other)
    protected operator fun N.times(other: Int) = helper.times(this, other)
    protected operator fun N.div(other: N) = helper.div(this, other)
    protected operator fun N.div(other: Int) = helper.div(this, other)

    protected operator fun N.unaryMinus() = zero - this
}
