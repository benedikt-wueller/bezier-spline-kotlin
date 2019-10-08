package dev.benedikt.math.bezierspline

import dev.benedikt.math.bezierspline.vector.VectorD

class DoubleBezierSpline<V : VectorD<V>>(val closed: Boolean = false) {

    private val knots = mutableListOf<V>()
    private var controlPoints = listOf<Pair<V, V>>()

    companion object {
        const val MIN_WEIGHT = 1.0
    }

    fun addKnots(vararg knots: V) {
        this.knots.addAll(knots)
        this.update()
    }

    fun removeKnots(vararg knots: V) {
        this.knots.removeAll(knots)
        this.update()
    }

    private fun update() {
        // We need at least two nodes for a path to be generated.
        if (this.knots.size < 2) {
            // Make sure no control points are set from previous calculations.
            this.controlPoints = listOf()
            return
        }

        val weights = this.computeWeights()

        // Calculate the control points.
        if (this.closed) {
            this.controlPoints = this.computeClosedControlPoints(weights)
        } else {
            this.controlPoints = this.computeOpenControlPoints(weights)
        }
    }

    private fun computeWeights() : List<Double> {
        val weights = mutableListOf<Double>()

        // Calculate the euclidean distance between all nodes.
        for (i in 0 until this.knots.lastIndex) {
            val distance = this.knots[i].distanceTo(this.knots[i + 1])
            weights.add(Math.max(distance, MIN_WEIGHT))
        }

        // If the spline is closed, we need an additional weight between the first and last knot.
        if (this.closed) {
            val distance = this.knots.first().distanceTo(this.knots.last())
            weights.add(Math.max(distance, MIN_WEIGHT))
        }

        return weights
    }

    private fun computeClosedControlPoints(initialWeights: List<Double>): List<Pair<V, V>> {
        val weights = initialWeights.toMutableList()

        val matrix = MatrixD<V>()

        for (i in 0 until this.knots.size) { // 1 to knots.size inclusive
            val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]
            val prevWeight = if (i == 0) weights.last() else weights[i - 1]
            val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]

            val fraction = weights[i] / nextWeight

            matrix.set(Math.pow(weights[i], 2.0),
                    2.0 * prevWeight * (prevWeight + weights[i]),
                    Math.pow(prevWeight, 2.0) * fraction,
                    this.knots[i] * Math.pow(prevWeight + weights[i], 2.0) + nextKnot * Math.pow(prevWeight, 2.0) * (1 + fraction))
        }

        val controlPoints = matrix.solveThomasClosed().toMutableList()
        controlPoints.add(controlPoints.first())

        return (0 until this.knots.size).map { i ->
            val nextKnot = if (i == this.knots.lastIndex) this.knots.first() else this.knots[i + 1]
            val nextWeight = if (i == this.knots.lastIndex) weights.first() else weights[i + 1]

            val fraction = weights[i] / nextWeight

            val p2 = nextKnot * (1 + fraction) - controlPoints[i + 1] * fraction
            return@map Pair(controlPoints[i], p2)
        }
    }

    private fun computeOpenControlPoints(initialWeights: List<Double>): List<Pair<V, V>> {
        // Copy the last weight value for the last pair of control points.
        val weights = initialWeights.toMutableList()
        weights.add(weights.last())

        val matrix = MatrixD<V>()

        // First segment
        matrix.set(0.0,
                2.0,
                weights[0] / weights[1],
                this.knots[0] + this.knots[1] * (1.0 + weights[0] / weights[1]))

        // Central segments
        for (i in 1 until this.knots.lastIndex) {
            val fraction = weights[i] / weights[i + 1]
            matrix.set(Math.pow(weights[i], 2.0),
                    2.0 * weights[i - 1] * (weights[i - 1] + weights[i]),
                    Math.pow(weights[i - 1], 2.0) * fraction,
                    this.knots[i] * Math.pow(weights[i - 1] + weights[i], 2.0) + this.knots[i + 1] * Math.pow(weights[i - 1], 2.0) * (1 + fraction))
        }

        // Last segment
        matrix.set(1.0, 2.0, 0.0, this.knots.last() * 3.0)

        // Calculate the first set of control points.
        val controlPoints = matrix.solveThomas()

        return (0 until this.knots.lastIndex).map { i ->
            val fraction = weights[i] / weights[i + 1]
            val p2 = this.knots[i + 1] * (1 + fraction) - controlPoints[i + 1] * fraction
            return@map Pair(controlPoints[i], p2)
        }
    }

}
