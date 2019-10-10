package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.math.FloatNumberHelper
import dev.benedikt.math.bezier.math.NumberHelper
import dev.benedikt.math.bezier.vector.Vector

class FloatBezierSpline<V : Vector<Float, V>>
@JvmOverloads constructor(closed: Boolean = false, resolution: Int = Resolution.BALANCED, override val minWeight: Float = Float.MIN_VALUE)
    : BezierSpline<Float, V>(closed, resolution) {

    override val numberHelper: NumberHelper<Float> get() = FloatNumberHelper()
}
