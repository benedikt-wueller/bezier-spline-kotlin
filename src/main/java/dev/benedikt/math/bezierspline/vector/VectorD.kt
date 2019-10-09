package dev.benedikt.math.bezierspline.vector

interface VectorD<V : VectorD<V>> : Vector<Double, V> {

    override fun copy() = this + 0.0
}
