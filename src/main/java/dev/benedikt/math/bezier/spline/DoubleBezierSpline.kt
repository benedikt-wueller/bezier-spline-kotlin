package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.math.DoubleMathHelper
import dev.benedikt.math.bezier.vector.Vector

class DoubleBezierSpline<V : Vector<Double, V>> @JvmOverloads constructor(closed: Boolean = false, resolution: Int = Resolution.DEFAULT,
                                                                          minWeight: Double = Double.MIN_VALUE)
    : BezierSpline<Double, V>(closed, resolution, minWeight, DoubleMathHelper())
