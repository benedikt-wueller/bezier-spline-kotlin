package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.ThomasMatrix
import dev.benedikt.math.bezier.math.NumberHelper
import dev.benedikt.math.bezier.vector.Vector
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

abstract class BezierSpline<N : Number, V : Vector<N, V>>(val isClosed: Boolean = false, val resolution: Int = Resolution.DEFAULT) {

    protected abstract val numberHelper: NumberHelper<N>
    protected abstract val minWeight: N

    private val knots = mutableListOf<V>()
    private val segments = mutableListOf<CubicBezierCurve<N, V>>()
    private var computedLength: N? = null

    val length: N get() {
        if (this.isDirty) this.compute()
        return this.computedLength!!
    }

    val segmentCount: Int get() = if (this.isClosed) this.knots.size + 1 else this.knots.size

    val isComputable: Boolean get() = this.knots.size >= 2
    var isDirty: Boolean = false; private set

    fun addKnot(knot: V) {
        this.knots.add(knot)
        this.isDirty = false
    }

    fun addKnots(knots: Iterable<V>) {
        this.knots.addAll(knots)
        this.isDirty = false
    }

    fun removeKnot(knot: V) {
        this.knots.remove(knot)
        this.isDirty = false
    }

    fun removeKnots(knots: Iterable<V>) {
        this.knots.removeAll(knots)
        this.isDirty = false
    }

    fun getSegment(index: Int) : CubicBezierCurve<N, V> {
        if (this.isDirty) this.compute()
        return this.segments[index]
    }

    fun getKnots(segmentIndex: Int) : Pair<V, V> {
        val segment = this.getSegment(segmentIndex)
        return Pair(segment.from, segment.to)
    }

    fun getControlPoints(segmentIndex: Int) : Pair<V, V> {
        return this.getSegment(segmentIndex).controlPoints
    }

    fun getCoordinatesAt(t: N) : V {
        if (this.isDirty) this.compute()
        val (segment, value) = this.getMappedSegment(t)
        return segment.getCoordinatesAt(value)
    }

    fun getTangentAt(t: N) : V {
        if (this.isDirty) this.compute()
        val (segment, value) = this.getMappedSegment(t)
        return segment.getTangentAt(value)
    }

    fun compute() {
        if (!this.isDirty) return
        if (!this.isComputable) throw IllegalStateException("The bezier spline requires at least 2 knots.")

        val weights = this.computeWeights()
        val controlPoints = this.computeControlPoints(weights)

        this.segments.clear()
        for (segment in 0 until this.segmentCount) {
            val knots = this.getKnots(segment)
            this.segments.add(CubicBezierCurve(knots.first, knots.second, controlPoints[segment], this.resolution, this.numberHelper))
        }

        // Estimates curve segment and spline lengths.
        var length = this.zero
        this.segments.forEach { length = this.plus(length, it.length) }
        this.computedLength = length
    }

    private fun getMappedSegment(t: N) : Pair<CubicBezierCurve<N, V>, N> {
        if (!this.isDirty) this.compute()

        if (!this.isBetween(t, this.zero, this.one)) {
            throw IllegalArgumentException("The factor t has to be a value between 0 and 1.")
        }

        // Find the segment.
        val targetLength = this.times(this.length, t)
        var currentLength = this.zero

        for (i in 0 until this.segmentCount) {
            val segment = this.getSegment(i)
            val length = segment.length
            val total = this.plus(currentLength, length)

            if (this.isBetween(targetLength, currentLength, total)) {
                return Pair(segment, this.div(this.minus(targetLength, currentLength), length))
            }

            currentLength = total
        }

        // As far as I am concerned, this code is logically unreachable.
        throw IllegalStateException("Unable to calculate the corresponding spline segment.")
    }

    private fun computeWeights() : List<N> {
        val weights = mutableListOf<N>()

        // Calculate the euclidean distance between all nodes.
        for (i in 0 until this.knots.size - 1) {
            val distance = this.knots[i].distanceTo(this.knots[i + 1])
            weights.add(this.max(distance, this.minWeight))
        }

        // If the spline is isClosed, we need an additional weight between the first and last knot.
        if (this.isClosed) {
            val distance = this.knots.first().distanceTo(this.knots.last())
            weights.add(this.max(distance, this.minWeight))
        }

        return weights
    }

    private fun computeControlPoints(initialWeights: List<N>): List<Pair<V, V>> {
        val weights = initialWeights.toMutableList()

        val matrix = ThomasMatrix<N, V>(this.numberHelper)

        if (this.isClosed) {
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
        matrix.set(this.zero, this.two, this.div(weights[0], weights[1]),
                this.knots[0] + this.knots[1] * this.plus(this.one, this.div(weights[0], weights[1])))

        // Central segments
        for (i in 1 until this.knots.lastIndex) {
            val weight = weights[i]
            val nextWeight = weights[i + 1]
            val prevWeight = weights[i - 1]

            val fraction = this.div(weight, nextWeight)

            val a = this.square(weight)
            val b = this.times(this.two, this.times(prevWeight, this.plus(prevWeight, weight)))
            val c = this.times(this.square(prevWeight), fraction)
            val r = this.knots[i] * this.square(this.plus(prevWeight, weight)) +
                    this.knots[i + 1] * this.square(prevWeight) * this.plus(this.one, fraction)

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
    private fun div(a: N, b: N) = this.numberHelper.div(a, b)

    private fun square(n: N) = this.numberHelper.pow(n, this.numberHelper.two)
    private fun max(a: N, b: N) = this.numberHelper.max(a, b)

    private fun isBetween(n: N, a: N, b: N) = this.numberHelper.isBetween(n, a, b)
}
