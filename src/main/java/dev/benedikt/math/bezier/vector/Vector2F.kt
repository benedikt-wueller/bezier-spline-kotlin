package dev.benedikt.math.bezier.vector

import kotlin.math.pow
import kotlin.math.sqrt

class Vector2F(var x: Float = 0.0f, var y: Float = 0.0f) : Vector<Float, Vector2F> {

    override operator fun plus(other: Vector2F) = Vector2F(this.x + other.x, this.y + other.y)
    override operator fun plus(value: Float) = Vector2F(this.x + value, this.y + value)

    override operator fun minus(other: Vector2F) = Vector2F(this.x - other.x, this.y - other.y)
    override operator fun minus(value: Float) = Vector2F(this.x - value, this.y - value)

    override operator fun times(other: Vector2F) = Vector2F(this.x * other.x, this.y * other.y)
    override operator fun times(value: Float) = Vector2F(this.x * value, this.y * value)

    override operator fun div(other: Vector2F) = Vector2F(this.x / other.x, this.y / other.y)
    override operator fun div(value: Float) = Vector2F(this.x / value, this.y / value)

    override fun distanceTo(other: Vector2F) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2))
    override fun magnitude() = sqrt(this.x * this.x + this.y * this.y)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Vector2F
        return (x == other.x && y == other.y)
    }

    override fun hashCode() = 31 * x.hashCode() + y.hashCode()

    override fun toString() = "Vector2F(x=$x,y=$y)"
}
