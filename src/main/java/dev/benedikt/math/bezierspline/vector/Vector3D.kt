package dev.benedikt.math.bezierspline.vector

import kotlin.math.pow
import kotlin.math.sqrt

class Vector3D(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) : VectorD<Vector3D> {

    override operator fun plus(other: Vector3D) = Vector3D(this.x + other.x, this.y + other.y, this.z + other.z)
    override operator fun plus(value: Double) = Vector3D(this.x + value, this.y + value, this.z + value)

    override operator fun minus(other: Vector3D) = Vector3D(this.x - other.x, this.y - other.y, this.z - other.z)
    override operator fun minus(value: Double) = Vector3D(this.x - value, this.y - value, this.z - value)

    override operator fun times(other: Vector3D) = Vector3D(this.x * other.x, this.y * other.y, this.z * other.z)
    override operator fun times(value: Double) = Vector3D(this.x * value, this.y * value, this.z * value)

    override operator fun div(other: Vector3D) = Vector3D(this.x / other.x, this.y / other.y, this.z / other.z)
    override operator fun div(value: Double) = Vector3D(this.x / value, this.y / value, this.z / value)

    override fun distanceTo(other: Vector3D) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2) + (this.z - other.z).pow(2))

    override fun toString(): String {
        return "($x,$y,$z)"
    }
}
