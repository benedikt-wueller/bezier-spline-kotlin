package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.ThomasMatrix
import dev.benedikt.math.bezier.curve.BezierCurve
import dev.benedikt.math.bezier.curve.Order
import dev.benedikt.math.bezier.math.MathComponent
import dev.benedikt.math.bezier.math.MathHelper
import dev.benedikt.math.bezier.vector.Vector
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

abstract class BezierSpline<N : Number, V : Vector<N, V>>(val isClosed: Boolean, val resolution: Int, val minWeight: N,
                                                          private val mathHelper: MathHelper<N>) : MathComponent<N>(mathHelper) {

    private val knots = mutableListOf<V>()
    private val segments = mutableListOf<BezierCurve<N, V>>()

    val segmentCount: Int get() = if (this.isClosed) this.knots.size + 1 else this.knots.size

    val isComputable: Boolean get() = this.knots.size >= 2
    var isDirty: Boolean = false; private set

    val length: N get() {
        if (this.isDirty) this.compute()
        var length = this.zero
        this.segments.forEach { length += it.length }
        return length
    }

    fun addKnots(vararg knots: V) {
        this.knots.addAll(knots)
        this.isDirty = true
    }

    fun removeKnots(vararg knots: V) {
        this.knots.removeAll(knots)
        this.isDirty = true
    }

    fun getKnots() = this.knots.map { it }

    fun getKnots(segmentIndex: Int) = this.getSegment(segmentIndex).knots

    fun getControlPoints(segmentIndex: Int) = this.getSegment(segmentIndex).controlPoints

    fun getCoordinatesAt(t: N) : V {
        val (segment, value) = this.getMappedSegment(t)
        return segment.getCoordinatesAt(value)
    }

    fun getTangentAt(t: N) : V {
        val (segment, value) = this.getMappedSegment(t)
        return segment.getTangentAt(value)
    }

    fun getSegment(index: Int) : BezierCurve<N, V> {
        if (this.isDirty) this.compute()
        return this.segments[index]
    }

    fun compute() {
        if (!this.isComputable) throw IllegalStateException("The bezier spline requires at least 2 knots.")
        if (!this.isDirty) return
        this.isDirty = false

        val weights = this.computeWeights()
        val controlPoints = this.computeControlPoints(weights)

        this.segments.clear()
        for (segment in 0 until this.segmentCount) {
            val knots = this.getKnots(segment)
            this.segments.add(BezierCurve(Order.CUBIC, knots.first, knots.second, controlPoints[segment].toList(), this.resolution, this.mathHelper))
        }
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

        val matrix = ThomasMatrix<N, V>(this.mathHelper)

        if (this.isClosed) {
            for (i in 0 until this.knots.size) { // 1 to knots.size inclusive
                val weight = weights[i]
                val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]
                val prevWeight = if (i == 0) weights.last() else weights[i - 1]

                val knot = this.knots[i]
                val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]

                val fraction = weight / nextWeight
                val combinedWeight = prevWeight + weight

                val a = this.square(weight)
                val b = this.two * prevWeight * combinedWeight
                val c = this.square(prevWeight) * fraction
                val r = knot * this.square(combinedWeight) +
                        nextKnot * this.square(prevWeight) * (this.one + fraction)

                matrix.set(a, b, c, r)
            }
        } else {
            weights.add(weights.last())

            // First segment
            matrix.set(this.zero, this.two, weights[0] / weights[1],
                    this.knots[0] + this.knots[1] * (this.one + weights[0] / weights[1]))

            // Central segments
            for (i in 1 until this.knots.lastIndex) {
                val weight = weights[i]
                val nextWeight = weights[i + 1]
                val prevWeight = weights[i - 1]

                val fraction = weight / nextWeight

                val a = this.square(weight)
                val b = this.two * prevWeight * (prevWeight + weight)
                val c = this.square(prevWeight) * fraction
                val r = this.knots[i] * this.square(prevWeight + weight) +
                        this.knots[i + 1] * this.square(prevWeight) * (this.one + fraction)

                matrix.set(a, b, c, r)
            }

            // Last segment
            matrix.set(this.one, this.two, this.zero, this.knots.last() * this.three)
        }

        val controlPoints = matrix.solve(true).toMutableList()
        if (this.isClosed) controlPoints.add(controlPoints.first())

        return (0 until this.segmentCount).map { i ->
            val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]
            val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]

            val fraction = weights[i] / nextWeight
            val p2 = nextKnot * (this.one + fraction) - controlPoints[i + 1] * fraction
            return@map Pair(controlPoints[i], p2)
        }
    }

    private fun getMappedSegment(t: N) : Pair<BezierCurve<N, V>, N> {
        if (this.isDirty) this.compute()

        if (!this.isBetween(t, this.zero, this.one)) {
            throw IllegalArgumentException("The factor t has to be a value between 0 and 1.")
        }

        // Find the segment.
        val targetLength = this.length * t
        var currentLength = this.zero

        for (i in 0 until this.segmentCount) {
            val segment = this.getSegment(i)
            val length = segment.length
            val total = currentLength + length

            if (this.isBetween(targetLength, currentLength, total)) {
                return Pair(segment, (targetLength - currentLength) / length)
            }

            currentLength = total
        }

        // As far as I am concerned, this code is logically unreachable.
        throw IllegalStateException("Unable to calculate the corresponding spline segment.")
    }
}
