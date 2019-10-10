package dev.benedikt.math.bezier.vector

import kotlin.math.pow
import kotlin.math.sqrt

class Vector2D(var x: Double = 0.0, var y: Double = 0.0) : Vector<Double, Vector2D> {

    override operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    override operator fun plus(value: Double) = Vector2D(this.x + value, this.y + value)

    override operator fun minus(other: Vector2D) = Vector2D(this.x - other.x, this.y - other.y)
    override operator fun minus(value: Double) = Vector2D(this.x - value, this.y - value)

    override operator fun times(other: Vector2D) = Vector2D(this.x * other.x, this.y * other.y)
    override operator fun times(value: Double) = Vector2D(this.x * value, this.y * value)

    override operator fun div(other: Vector2D) = Vector2D(this.x / other.x, this.y / other.y)
    override operator fun div(value: Double) = Vector2D(this.x / value, this.y / value)

    override fun distanceTo(other: Vector2D) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2))
    override fun magnitude() = sqrt(this.x * this.x + this.y * this.y)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Vector2D
        return (x == other.x && y == other.y)
    }

    override fun hashCode() = 31 * x.hashCode() + y.hashCode()

    override fun toString() = "Vector2D(x=$x,y=$y)"
}
