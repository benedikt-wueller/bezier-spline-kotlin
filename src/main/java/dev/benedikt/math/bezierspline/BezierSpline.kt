package dev.benedikt.math.bezierspline

import dev.benedikt.math.bezierspline.matrix.ThomasMatrix
import dev.benedikt.math.bezierspline.vector.Vector

abstract class BezierSpline<N : Number, V : Vector<N, V>>(val closed: Boolean = false) {

    protected abstract val zero: N
    protected abstract val one: N

    private val two = this.plus(this.one, this.one)
    private val three = this.plus(this.one, this.two)

    protected abstract val minWeight : N

    protected val knots = mutableListOf<V>()
    protected val controlPoints = mutableListOf<Pair<V, V>>()

    fun addKnots(vararg knots: V) {
        this.knots.addAll(knots)
        this.update()
    }

    fun removeKnots(vararg knots: V) {
        this.knots.removeAll(knots)
        this.update()
    }

    private fun update() {
        this.controlPoints.clear()

        // We need at least two nodes for a path to be generated.
        if (this.knots.size < 2) return

        val weights = this.computeWeights()
        this.controlPoints.addAll(this.computeControlPoints(weights))
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

        val matrix = this.createMatrix()

        if (this.closed) {
            for (i in 0 until this.knots.size) { // 1 to knots.size inclusive
                val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]
                val prevWeight = if (i == 0) weights.last() else weights[i - 1]
                val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]

                val fraction = this.div(weights[i], nextWeight)

                matrix.set(this.square(weights[i]),
                        this.times(this.two, this.times(prevWeight, this.plus(prevWeight, weights[i]))),
                        this.times(this.square(prevWeight), fraction),
                        this.knots[i] * this.square(this.plus(prevWeight, weights[i])) + nextKnot * this.square(prevWeight) * this.plus(this.one, fraction))
            }

            val controlPoints = matrix.solveThomasClosed().toMutableList()
            controlPoints.add(controlPoints.first())

            return (0 until this.knots.size).map { i ->
                val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]
                val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]

                val fraction = this.div(weights[i], nextWeight)
                val p2 = nextKnot * this.plus(this.one, fraction) - controlPoints[i + 1] * fraction
                return@map Pair(controlPoints[i], p2)
            }
        }

        // First segment
        matrix.set(this.zero, this.two, this.div(weights[0], weights[1]), this.knots[0] + this.knots[1] * this.plus(this.one, this.div(weights[0], weights[1])))

        // Central segments
        for (i in 1 until this.knots.lastIndex) {
            val fraction = this.div(weights[i], weights[i + 1])
            matrix.set(this.square(weights[i]),
                    this.times(this.two, this.times(weights[i - 1], this.plus(weights[i - 1], weights[i]))),
                    this.times(this.square(weights[i - 1]), fraction),
                    this.knots[i] * this.square(this.plus(weights[i - 1], weights[i])) + this.knots[i + 1] * this.square(weights[i - 1]) * this.plus(this.one, fraction))
        }

        // Last segment
        matrix.set(this.one, this.two, this.zero, this.knots.last() * this.three)

        // Calculate the first set of control points.
        val controlPoints = matrix.solveThomas()

        return (0 until this.knots.lastIndex).map { i ->
            val fraction = this.div(weights[i], weights[i + 1])
            val p2 = this.knots[i + 1] * this.plus(this.one, fraction) - controlPoints[i + 1] * fraction
            return@map Pair(controlPoints[i], p2)
        }
    }

    protected abstract fun createMatrix() : ThomasMatrix<N, V>

    //
    // Math helpers for generic types
    //

    protected abstract fun plus(a: N, b: N) : N
    protected abstract fun minus(a: N, b: N) : N
    protected abstract fun times(a: N, b: N) : N
    protected abstract fun div(a: N, b: N) : N

    /**
     * Equivalent of Math.max(a, b).
     *
     * @return the bigger number.
     */
    protected abstract fun max(a: N, b: N) : N

    /**
     * Equivalent of Math.pow(n, 2).
     *
     * @param n the number to square.
     * @return the squared number.
     */
    protected abstract fun square(n: N) : N
}
