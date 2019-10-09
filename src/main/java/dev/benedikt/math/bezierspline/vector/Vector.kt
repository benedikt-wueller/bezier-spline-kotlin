package dev.benedikt.math.bezierspline.vector

abstract class Vector<N : Number, V : Vector<N, V>> {

    abstract operator fun plus(other: V) : V
    abstract operator fun plus(value: N) : V

    abstract operator fun minus(other: V) : V
    abstract operator fun minus(value: N) : V

    abstract operator fun times(other: V) : V
    abstract operator fun times(value: N) : V

    abstract operator fun div(other: V) : V
    abstract operator fun div(value: N) : V

    /**
     * Returns the euclidean distance between this and the given vector.
     *
     * @param other the vector to calculate the distance to.
     * @return the calculated distance.
     */
    abstract fun distanceTo(other: V) : N

    /**
     * Creates and returns a copy of this vector.
     *
     * @return the new vector.
     */
    abstract fun copy() : V
}
