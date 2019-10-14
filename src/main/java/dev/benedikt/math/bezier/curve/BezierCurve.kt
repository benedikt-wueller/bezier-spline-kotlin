package dev.benedikt.math.bezier.curve

import dev.benedikt.math.bezier.math.MathComponent
import dev.benedikt.math.bezier.math.MathHelper
import dev.benedikt.math.bezier.vector.Vector
import java.lang.IllegalArgumentException

// See https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves

open class BezierCurve<N : Number, V : Vector<N, V>>(val order: Order, val from: V, val to: V, val controlPoints: Collection<V>,
                                                val resolution: Int, helper: MathHelper<N>) : MathComponent<N>(helper) {

    val knots = this.from to this.to
    val points: List<V>

    private var computedLength: N? = null

    val length: N get() {
        if (this.computedLength == null) this.computeLength()
        return this.computedLength!!
    }

    init {
        if (this.controlPoints.size == this.order.controlPoints) {
            throw IllegalArgumentException("The bezier curve of order ${order.name} expects exactly ${order.controlPoints} control points.")
        }

        if (this.order.degree < Order.LINEAR.degree || this.order.previous == null) {
            throw IllegalArgumentException("The bezier curve expects a minimum order of ${Order.LINEAR.name}.")
        }

        val points = mutableListOf(this.from)
        points.addAll(this.controlPoints)
        points.add(to)
        this.points = points.toList()
    }

    fun getCoordinatesAt(t: N) : V {
        return this.getCoordinatesAt(t, this.order, this.points)
    }

    fun getTangentAt(t: N) : V {
        val newPoints = (0 until this.order.degree).map { (this.points[it + 1] - this.points[it]) * (this.one * this.order.degree) }
        return this.getCoordinatesAt(t, this.order.previous!!, newPoints).normalized()
    }

    private fun getCoordinatesAt(t: N, order: Order, points: List<V>) : V {
        var result : V? = null

        val mt = this.one - t
        val size = order.binomals.size

        for (i in 0 until size) {
            val part = points[i] * (this.pow(mt, size - i - 1) * this.pow(t, i) * order.binomals[i])
            result = if (result == null) part else result + part
        }

        return result!!
    }

    fun computeLength() {
        val fraction = this.one / this.resolution

        var length = this.zero
        var lastCoordinates = this.from

        for (i in 1..this.resolution) {
            val coordinates = this.getCoordinatesAt(fraction * i)
            length += lastCoordinates.distanceTo(coordinates)
            lastCoordinates = coordinates
        }

        this.computedLength = length
    }
}
