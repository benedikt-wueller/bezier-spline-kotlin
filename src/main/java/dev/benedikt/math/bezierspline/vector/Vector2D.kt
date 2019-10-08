package dev.benedikt.math.bezierspline.vector

class Vector2D(var x: Double = 0.0, var y: Double = 0.0) : VectorD<Vector2D>() {

    override operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    override operator fun plus(value: Double) = Vector2D(this.x + value, this.y + value)

    override operator fun minus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    override operator fun minus(value: Double) = Vector2D(this.x - value, this.y - value)

    override operator fun times(other: Vector2D) = Vector2D(this.x * other.x, this.y * other.y)
    override operator fun times(value: Double) = Vector2D(this.x * value, this.y * value)

    override operator fun div(other: Vector2D) = Vector2D(this.x / other.x, this.y / other.y)
    override operator fun div(value: Double) = Vector2D(this.x / value, this.y / value)

    override fun distanceTo(other: Vector2D) = Math.sqrt(Math.pow(this.x - other.x, 2.0) + Math.pow(this.y - other.y, 2.0))

    override fun toString(): String {
        return "Vector2D(x=${this.x},y=${this.y})"
    }
}
