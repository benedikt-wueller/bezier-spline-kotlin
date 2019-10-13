package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.math.MathHelper
import dev.benedikt.math.bezier.vector.Vector

class CubicBezierCurve<N : Number, V : Vector<N, V>>(val from: V, val to: V, val controlPoints: Pair<V, V>,
                                                     val resolution: Int, private val math: MathHelper<N>) {

    private var computedLength: N? = null
    val length get() = this.computedLength ?: this.computeLength()

    fun getCoordinatesAt(t: N) : V {
        // See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

        val p0 = this.from
        val (p1, p2) = this.controlPoints
        val p3 = this.to

        val mt = this.math.minus(this.math.one, t)
        val mt2 = this.math.square(mt)
        val mt3 = this.math.pow(mt, this.math.three)

        val t2 = this.math.square(t)
        val t3 = this.math.pow(t, this.math.three)

        return (p0 * mt3) + (p1 * this.math.three * mt2 * t) + (p2 * this.math.three * mt * t2) + (p3 * t3)
    }

    fun getTangentAt(t: N) : V {
        // See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

        val p0 = this.from
        val (p1, p2) = this.controlPoints
        val p3 = this.to

        val mt = this.math.minus(this.math.one, t)
        val mt2 = this.math.square(mt)

        val t2 = this.math.square(t)

        return (((p1 - p0) * this.math.three * mt2) + ((p2 - p1) * t * this.math.six * mt) + ((p3 - p2) * this.math.three * t2)).normalized()
    }

    private fun computeLength() : N {
        val fraction = this.math.div(this.math.one, this.resolution)

        var length = this.math.zero
        var lastCoordinates = this.from

        for (i in 1..this.resolution) {
            val coordinates = this.getCoordinatesAt(this.math.times(fraction, i))
            val sectionLength = lastCoordinates.distanceTo(coordinates)
            lastCoordinates = coordinates
            length = this.math.plus(length, sectionLength)
        }

        this.computedLength = length
        return length
    }
}
