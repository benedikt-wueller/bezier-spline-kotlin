package dev.benedikt.math.bezier.curve

import dev.benedikt.math.bezier.Resolution
import dev.benedikt.math.bezier.math.DoubleMathHelper
import dev.benedikt.math.bezier.vector.Vector

class DoubleBezierCurve<V : Vector<Double, V>>(order: Order, from: V, to: V, controlPoints: Collection<V>, resolution: Int = Resolution.DEFAULT)
    : BezierCurve<Double, V>(order, from, to, controlPoints, resolution, DoubleMathHelper())
