package dev.benedikt.math.bezierspline.vector

interface Vector<N : Number, V : Vector<N, V>> {

    operator fun plus(other: V) : V
    operator fun plus(value: N) : V

    operator fun minus(other: V) : V
    operator fun minus(value: N) : V

    operator fun times(other: V) : V
    operator fun times(value: N) : V

    operator fun div(other: V) : V
    operator fun div(value: N) : V

    /**
     * Returns the euclidean distance between this and the given vector.
     *
     * @param other the vector to calculate the distance to.
     * @return the calculated distance.
     */
    fun distanceTo(other: V) : N

    /**
     * Creates and returns a copy of this vector.
     *
     * @return the new vector.
     */
    fun copy() : V
}
