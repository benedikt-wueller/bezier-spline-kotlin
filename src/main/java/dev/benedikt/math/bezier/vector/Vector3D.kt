package dev.benedikt.math.bezier.vector

import kotlin.math.pow
import kotlin.math.sqrt

class Vector3D(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) : Vector<Double, Vector3D> {

    override operator fun plus(other: Vector3D) = Vector3D(this.x + other.x, this.y + other.y, this.z + other.z)
    override operator fun plus(value: Double) = Vector3D(this.x + value, this.y + value, this.z + value)

    override operator fun minus(other: Vector3D) = Vector3D(this.x - other.x, this.y - other.y, this.z - other.z)
    override operator fun minus(value: Double) = Vector3D(this.x - value, this.y - value, this.z - value)

    override operator fun times(other: Vector3D) = Vector3D(this.x * other.x, this.y * other.y, this.z * other.z)
    override operator fun times(value: Double) = Vector3D(this.x * value, this.y * value, this.z * value)

    override operator fun div(other: Vector3D) = Vector3D(this.x / other.x, this.y / other.y, this.z / other.z)
    override operator fun div(value: Double) = Vector3D(this.x / value, this.y / value, this.z / value)

    override fun distanceTo(other: Vector3D) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2) + (this.z - other.z).pow(2))
    override fun magnitude() = sqrt(this.x * this.x + this.y * this.y + this.z * this.z)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Vector3D
        return (x == other.x && y == other.y && z == other.z)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString() = "Vector3D(x=$x,y=$y,z=$z)"
}
