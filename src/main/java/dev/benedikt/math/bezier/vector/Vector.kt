package dev.benedikt.math.bezier.vector

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

    fun magnitude() : N

    fun normalized() = this / this.magnitude()
}
