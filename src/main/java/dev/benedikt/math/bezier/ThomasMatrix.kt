package dev.benedikt.math.bezier

import dev.benedikt.math.bezier.math.NumberHelper
import dev.benedikt.math.bezier.vector.Vector

/**
 * A Matrix using the Thomas algorithm to calculate the control points of the individual bezier curves creating the
 * bezier spline.
 */
class ThomasMatrix<N : Number, V : Vector<N, V>>(private val helper: NumberHelper<N>) {

    private val a = mutableListOf<N>()
    private val b = mutableListOf<N>()
    private val c = mutableListOf<N>()
    private val r = mutableListOf<V>()

    fun set(a: N, b: N, c: N, r: V) {
        this.a.add(a)
        this.b.add(b)
        this.c.add(c)
        this.r.add(r)
    }

    fun solve() : List<V> {
        val b = this.b.toMutableList()
        val r = this.r.toMutableList()

        for (i in 1 until r.size) {
            val m = this.div(this.a[i], b[i - 1])
            b[i] = this.minus(b[i], this.times(this.c[i - 1], m))
            r[i] = r[i] - r[i - 1] * m
        }

        val size = r.size
        val result = mutableListOf<V>()
        result.add(r.last() / b.last())
        for (i in 1 until size) {
            val realIndex = size - 1 - i
            result.add((r[realIndex] - result[i - 1] * c[realIndex]) / b[realIndex])
        }

        return result.reversed()
    }

    fun solveClosed() : List<V> {
        val a = this.a.toMutableList()
        val b = this.b.toMutableList()
        val c = this.c.toMutableList()
        val r = this.r.toMutableList()

        val size = r.size

        val lastColumn = mutableListOf(a.first())
        var lastRow = c[c.lastIndex]

        for (i in 0 until size - 1) {
            var m = this.div(a[i + 1], b[i])
            b[i + 1] = this.minus(b[i + 1], this.times(c[i], m))
            r[i + 1] = r[i + 1] - r[i] * m

            if (i > size - 3) continue

            if (i < size - 3) {
                lastColumn.add(this.times(lastColumn[i], this.negate(m)))
            } else { // i = n-3
                c[i + 1] = this.minus(c[i + 1], this.times(lastColumn[i], m))
            }

            m = this.div(lastRow, b[i])
            b[b.lastIndex] = this.minus(b[b.lastIndex], this.times(lastColumn[i], m))

            if (i < size - 3) {
                lastRow = this.times(c[i], this.negate(m))
            } else { // i = n-3
                a[a.lastIndex] = this.minus(a[a.lastIndex], this.times(c[i], m))
            }

            r[r.lastIndex] -= r[i] * m
        }

        lastColumn.add(this.helper.zero)

        val result = mutableListOf<V>()
        result.add(r.last() / b.last())

        for (i in 1 until size) {
            val realIndex = size - 1 - i
            result.add((r[realIndex] - result[i - 1] * c[realIndex] - result[0] * lastColumn[realIndex]) / b[realIndex])
        }

        return result.reversed()
    }

    //
    // Math helpers for readability
    //

    private fun minus(a: N, b: N) = this.helper.minus(a, b)
    private fun times(a: N, b: N) = this.helper.times(a, b)
    private fun div(a: N, b: N) = this.helper.div(a, b)
    private fun negate(n: N) = this.helper.negate(n)
}
