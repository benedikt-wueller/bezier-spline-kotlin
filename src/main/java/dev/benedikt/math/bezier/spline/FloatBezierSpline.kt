package dev.benedikt.math.bezier.spline

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.math.FloatMathHelper
import dev.benedikt.math.bezier.vector.Vector

class FloatBezierSpline<V : Vector<Float, V>> @JvmOverloads constructor(closed: Boolean = false, resolution: Int = Resolution.DEFAULT,
                                                                        minWeight: Float = Float.MIN_VALUE)
    : BezierSpline<Float, V>(closed, resolution, minWeight, FloatMathHelper())
