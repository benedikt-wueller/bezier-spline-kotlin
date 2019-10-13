package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.math.NumberHelper
import dev.benedikt.math.bezier.vector.Vector

class CubicBezierCurve<N : Number, V : Vector<N, V>>(val from: V, val to: V, val controlPoints: Pair<V, V>,
                                                     val resolution: Int, private val numberHelper: NumberHelper<N>) {

    private var computedLength: N? = null
    val length get() = this.computedLength ?: this.computeLength()

    fun getCoordinatesAt(t: N) : V {
        // See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

        val p0 = this.from
        val (p1, p2) = this.controlPoints
        val p3 = this.to

        val mt = this.minus(this.one, t)
        val mt2 = this.square(mt)
        val mt3 = this.pow(mt, this.three)

        val t2 = this.square(t)
        val t3 = this.pow(t, this.three)

        return (p0 * mt3) + (p1 * this.three * mt2 * t) + (p2 * this.three * mt * t2) + (p3 * t3)
    }

    fun getTangentAt(t: N) : V {
        // See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

        val p0 = this.from
        val (p1, p2) = this.controlPoints
        val p3 = this.to

        val mt = this.minus(this.one, t)
        val mt2 = this.square(mt)

        val t2 = this.square(t)

        return (((p1 - p0) * this.three * mt2) + ((p2 - p1) * t * this.six * mt) + ((p3 - p2) * this.three * t2)).normalized()
    }

    private fun computeLength() : N {
        val fraction = this.div(this.one, this.resolution)

        var length = this.zero
        var lastCoordinates = this.from

        for (i in 1..this.resolution) {
            val coordinates = this.getCoordinatesAt(this.times(fraction, i))
            val sectionLength = lastCoordinates.distanceTo(coordinates)
            lastCoordinates = coordinates
            length = this.plus(length, sectionLength)
        }

        this.computedLength = length
        return length
    }

    //
    // Math helpers for readability
    //

    @Suppress("LeakingThis") private val zero = this.numberHelper.zero
    @Suppress("LeakingThis") private val one = this.numberHelper.one
    @Suppress("LeakingThis") private val three = this.numberHelper.three
    @Suppress("LeakingThis") private val six = this.plus(this.three, this.three)

    private fun plus(a: N, b: N) = this.numberHelper.plus(a, b)
    private fun minus(a: N, b: N) = this.numberHelper.minus(a, b)
    private fun times(a: N, b: Int) = this.numberHelper.times(a, b)
    private fun div(a: N, b: Int) = this.numberHelper.div(a, b)

    private fun square(n: N) = this.numberHelper.pow(n, this.numberHelper.two)
    private fun pow(n: N, p: N) = this.numberHelper.pow(n, p)
}