package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.ThomasMatrix
import dev.benedikt.math.bezier.math.NumberHelper
import dev.benedikt.math.bezier.vector.Vector
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException

abstract class BezierSpline<N : Number, V : Vector<N, V>>(val closed: Boolean = false, val resolution: Int = Resolution.DEFAULT) {

    protected abstract val numberHelper: NumberHelper<N>
    protected abstract val minWeight: N

    private val knots = mutableListOf<V>()
    private var controlPoints = listOf<Pair<V, V>>()
    private var segmentLengths = listOf<N>()

    @Suppress("LeakingThis") var length: N? = null; private set

    val isComputable: Boolean get() = this.knots.size >= 2
    val isComputed: Boolean get() = this.length != null

    @JvmOverloads
    fun addKnot(knot: V, update: Boolean = true) {
        this.knots.add(knot)
        if (update) this.update()
    }

    @JvmOverloads
    fun addKnots(knots: Iterable<V>, update: Boolean = true) {
        this.knots.addAll(knots)
        if (update) this.update()
    }

    @JvmOverloads
    fun removeKnot(knot: V, update: Boolean = true) {
        this.knots.remove(knot)
        if (update) this.update()
    }

    @JvmOverloads
    fun removeKnots(knots: Iterable<V>, update: Boolean = true) {
        this.knots.removeAll(knots)
        if (update) this.update()
    }

    fun update() : Boolean {
        // We need at least two nodes for a path to be generated.
        if (!this.isComputable) {
            this.resetComputed()
            return false
        }

        val weights = this.computeWeights()
        this.controlPoints = this.computeControlPoints(weights)

        // Calculate curve segment and spline lengths.
        var length = this.zero
        val segmentLengths = mutableListOf<N>()

        for (i in 0 until this.controlPoints.size) {
            val segmentLength = this.computeSegmentLength(i)
            segmentLengths.add(segmentLength)
            length = this.plus(length, segmentLength)
        }

        this.length = length
        this.segmentLengths = segmentLengths
        return true
    }

    fun getCoordinatesAt(t: N) : V {
        if (!this.isComputed) {
            throw IllegalStateException("The spline's length has not been estimated yet.")
        }

        val (segment, value) = this.getMappedSegment(t)
        return this.getCoordinatesAt(segment, value)
    }

    fun getTangentAt(t: N) : V {
        if (!this.isComputed) {
            throw IllegalStateException("The spline's length has not been estimated yet.")
        }

        val (segment, value) = this.getMappedSegment(t)
        return this.getTangentAt(segment, value)
    }

    fun getKnots(segment: Int) : Pair<V, V> {
        if (segment < 0) throw IndexOutOfBoundsException(segment)
        if ((this.closed && segment > this.knots.size) || (!this.closed && segment > this.knots.lastIndex)) throw IndexOutOfBoundsException(segment)

        val first = this.knots[segment]
        val seconds = if (this.closed && segment == this.knots.lastIndex) this.knots[0] else this.knots[segment + 1]
        return Pair(first, seconds)
    }

    fun getControlPoints(segment: Int) : Pair<V, V> {
        if (segment < 0 || segment > this.controlPoints.lastIndex) {
            if (segment >= 0 && !this.isComputed) {
                throw IllegalStateException("The control points have not been computed yet.")
            }

            throw IndexOutOfBoundsException(segment)
        }

        return this.controlPoints[segment]
    }

    private fun getMappedSegment(t: N) : Pair<Int, N> {
        if (!this.isBetween(t, this.zero, this.one)) {
            throw IllegalArgumentException("The factor t has to be a value between 0 and 1.")
        }

        // Find the segment.
        val targetLength = this.times(this.length!!, t)
        var currentLength = this.zero

        for (i in 0 until this.segmentLengths.size) {
            val length = this.segmentLengths[i]
            val total = this.plus(currentLength, length)

            if (this.isBetween(targetLength, currentLength, total)) {
                return Pair(i, this.div(this.minus(targetLength, currentLength), length))
            }

            currentLength = total
        }

        // As far as I am concerned, this code is logically unreachable.
        throw IllegalStateException("Unable to calculate the corresponding spline segment.")
    }

    private fun getCoordinatesAt(segment: Int, t: N) : V {
        // See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

        val (p0, p3) = this.getKnots(segment)
        val (p1, p2) = this.getControlPoints(segment)

        val mt = this.minus(this.one, t)
        val mt2 = this.square(mt)
        val mt3 = this.pow(mt, this.three)

        val t2 = this.square(t)
        val t3 = this.pow(t, this.three)

        return (p0 * mt3) + (p1 * this.three * mt2 * t) + (p2 * this.three * mt * t2) + (p3 * t3)
    }

    private fun getTangentAt(segment: Int, t: N) : V {
        // See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

        val (p0, p3) = this.getKnots(segment)
        val (p1, p2) = this.getControlPoints(segment)

        val mt = this.minus(this.one, t)
        val mt2 = this.square(mt)

        val t2 = this.square(t)

        return (((p1 - p0) * this.three * mt2) + ((p2 - p1) * t * this.six * mt) + ((p3 - p2) * this.three * t2)).normalized()
    }

    private fun resetComputed() {
        this.controlPoints = listOf()
        this.segmentLengths = listOf()
        this.length = this.zero
    }

    private fun computeSegmentLength(segment: Int) : N {
        val fraction = this.div(this.one, this.resolution)

        var length = this.zero
        var lastCoordinates = this.knots[segment]

        for (i in 1..this.resolution) {
            val coordinates = this.getCoordinatesAt(segment, this.times(fraction, i))
            val sectionLength = lastCoordinates.distanceTo(coordinates)
            lastCoordinates = coordinates
            length = this.plus(length, sectionLength)
        }

        return length
    }

    private fun computeWeights() : List<N> {
        val weights = mutableListOf<N>()

        // Calculate the euclidean distance between all nodes.
        for (i in 0 until this.knots.size - 1) {
            val distance = this.knots[i].distanceTo(this.knots[i + 1])
            weights.add(this.max(distance, this.minWeight))
        }

        // If the spline is closed, we need an additional weight between the first and last knot.
        if (this.closed) {
            val distance = this.knots.first().distanceTo(this.knots.last())
            weights.add(this.max(distance, this.minWeight))
        }

        return weights
    }

    private fun computeControlPoints(initialWeights: List<N>): List<Pair<V, V>> {
        val weights = initialWeights.toMutableList()

        val matrix = ThomasMatrix<N, V>(this.numberHelper)

        if (this.closed) {
            for (i in 0 until this.knots.size) { // 1 to knots.size inclusive
                val weight = weights[i]
                val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]
                val prevWeight = if (i == 0) weights.last() else weights[i - 1]

                val knot = this.knots[i]
                val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]

                val fraction = this.div(weight, nextWeight)
                val combinedWeight = this.plus(prevWeight, weight)

                val a = this.square(weight)
                val b = this.times(this.two, this.times(prevWeight, combinedWeight))
                val c = this.times(this.square(prevWeight), fraction)
                val r = knot * this.square(combinedWeight) + nextKnot * this.square(prevWeight) * this.plus(this.one, fraction)

                matrix.set(a, b, c, r)
            }

            val controlPoints = matrix.solveClosed().toMutableList()
            controlPoints.add(controlPoints.first())

            return (0 until this.knots.size).map { i ->
                val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]
                val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]

                val fraction = this.div(weights[i], nextWeight)
                val p2 = nextKnot * this.plus(this.one, fraction) - controlPoints[i + 1] * fraction
                return@map Pair(controlPoints[i], p2)
            }
        }

        weights.add(weights.last())

        // First segment
        matrix.set(this.zero, this.two, this.div(weights[0], weights[1]), this.knots[0] + this.knots[1] * this.plus(this.one, this.div(weights[0], weights[1])))

        // Central segments
        for (i in 1 until this.knots.lastIndex) {
            val weight = weights[i]
            val nextWeight = weights[i + 1]
            val prevWeight = weights[i - 1]

            val fraction = this.div(weight, nextWeight)

            val a = this.square(weight)
            val b = this.times(this.two, this.times(prevWeight, this.plus(prevWeight, weight)))
            val c = this.times(this.square(prevWeight), fraction)
            val r = this.knots[i] * this.square(this.plus(prevWeight, weight)) + this.knots[i + 1] * this.square(prevWeight) * this.plus(this.one, fraction)

            matrix.set(a, b, c, r)
        }

        // Last segment
        matrix.set(this.one, this.two, this.zero, this.knots.last() * this.three)

        // Calculate the first set of control points.
        val controlPoints = matrix.solve()

        return (0 until this.knots.lastIndex).map { i ->
            val fraction = this.div(weights[i], weights[i + 1])
            val p2 = this.knots[i + 1] * this.plus(this.one, fraction) - controlPoints[i + 1] * fraction
            return@map Pair(controlPoints[i], p2)
        }
    }

    //
    // Math helpers for readability
    //

    @Suppress("LeakingThis") private val zero = this.numberHelper.zero
    @Suppress("LeakingThis") private val one = this.numberHelper.one
    @Suppress("LeakingThis") private val two = this.numberHelper.two
    @Suppress("LeakingThis") private val three = this.numberHelper.three
    @Suppress("LeakingThis") private val six = this.plus(this.three, this.three)

    private fun plus(a: N, b: N) = this.numberHelper.plus(a, b)
    private fun minus(a: N, b: N) = this.numberHelper.minus(a, b)
    private fun times(a: N, b: N) = this.numberHelper.times(a, b)
    private fun times(a: N, b: Int) = this.numberHelper.times(a, b)
    private fun div(a: N, b: N) = this.numberHelper.div(a, b)
    private fun div(a: N, b: Int) = this.numberHelper.div(a, b)

    private fun square(n: N) = this.numberHelper.pow(n, this.numberHelper.two)
    private fun pow(n: N, p: N) = this.numberHelper.pow(n, p)
    private fun max(a: N, b: N) = this.numberHelper.max(a, b)

    private fun isBetween(n: N, a: N, b: N) = this.numberHelper.isBetween(n, a, b)
}
