package dev.benedikt.math.bezierspline.vector

import kotlin.math.pow
import kotlin.math.sqrt

class Vector2D(var x: Double = 0.0, var y: Double = 0.0) : VectorD<Vector2D> {

    override operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    override operator fun plus(value: Double) = Vector2D(this.x + value, this.y + value)

    override operator fun minus(other: Vector2D) = Vector2D(this.x - other.x, this.y - other.y)
    override operator fun minus(value: Double) = Vector2D(this.x - value, this.y - value)

    override operator fun times(other: Vector2D) = Vector2D(this.x * other.x, this.y * other.y)
    override operator fun times(value: Double) = Vector2D(this.x * value, this.y * value)

    override operator fun div(other: Vector2D) = Vector2D(this.x / other.x, this.y / other.y)
    override operator fun div(value: Double) = Vector2D(this.x / value, this.y / value)

    override fun distanceTo(other: Vector2D) = sqrt((this.x - other.x).pow(2.0) + (this.y - other.y).pow(2.0))

}
