package dev.benedikt.math.bezierspline.vector

abstract class VectorD<V : VectorD<V>> {

    abstract operator fun plus(other: V) : V
    abstract operator fun plus(value: Double) : V

    abstract operator fun minus(other: V) : V
    abstract operator fun minus(value: Double) : V

    abstract operator fun times(other: V) : V
    abstract operator fun times(value: Double) : V

    abstract operator fun div(other: V) : V
    abstract operator fun div(value: Double) : V

    /**
     * Returns the euclidean distance between this and the given vector.
     *
     * @param other the vector to calculate the distance to.
     * @return the calculated distance.
     */
    abstract fun distanceTo(other: V) : Double

    /**
     * Creates and returns a copy of this vector.
     *
     * @return the new vector.
     */
    fun copy() : V = this + 0.0
}
