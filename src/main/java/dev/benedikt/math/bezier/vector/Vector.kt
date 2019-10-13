package dev.benedikt.math.bezier.vector

/**
 * Generic interface to allow variable dimensions and generic number types for use in bezier splines. The bezier spline is designed to work with
 * immutable Vectors. There should be no way in which the values can be modified after creation.
 */
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
     * Calculates and returns the magnitude of this vector.
     *
     * @return the magnitude.
     */
    fun magnitude() : N

    /**
     * Returns the normalized vector.
     *
     * @return the normalized vector.
     */
    fun normalized() = this / this.magnitude()
}
