package dev.benedikt.math.bezier.curve

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.math.FloatMathHelper
import dev.benedikt.math.bezier.vector.Vector

class FloatBezierCurve<V : Vector<Float, V>>(order: Order, from: V, to: V, controlPoints: Collection<V>, resolution: Int = Resolution.DEFAULT)
    : BezierCurve<Float, V>(order, from, to, controlPoints, resolution, FloatMathHelper())
