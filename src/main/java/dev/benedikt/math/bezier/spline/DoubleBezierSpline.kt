package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.math.DoubleNumberHelper
import dev.benedikt.math.bezier.math.NumberHelper
import dev.benedikt.math.bezier.vector.Vector

class DoubleBezierSpline<V : Vector<Double, V>> @JvmOverloads constructor(closed: Boolean = false, resolution: Int = 10000, override val minWeight: Double = Double.MIN_VALUE)
    : BezierSpline<Double, V>(closed, resolution) {

    override val numberHelper: NumberHelper<Double> get() = DoubleNumberHelper()
}
