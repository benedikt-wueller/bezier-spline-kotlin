package dev.benedikt.math.bezier.curve

enum class Order(val degree: Int, val controlPoints: Int, val binomals: IntArray, val previous: Order?) {

    CONSTANT(0, 0, intArrayOf(1), null),
    LINEAR(1, 0, intArrayOf(1, 1), CONSTANT),
    QUADRATIC(2, 1, intArrayOf(1, 2, 1), LINEAR),
    CUBIC(3, 2, intArrayOf(1, 3, 3, 1), QUADRATIC),
    QUARTIC(4, 3, intArrayOf(1, 4, 6, 4, 1), CUBIC),
    QUINTIC(5, 4, intArrayOf(1, 5, 10, 10, 5, 1), QUARTIC),
    SEXTIC(6, 5, intArrayOf(1, 6, 15, 20, 15, 6, 1), QUINTIC),
    SEPTIC(7, 6, intArrayOf(1, 7, 21, 35, 35, 21, 7, 1), SEXTIC),

}
