package dev.benedikt.math.bezier

import dev.benedikt.math.bezier.math.MathHelper
import dev.benedikt.math.bezier.vector.Vector

/**
 * A Matrix using the Thomas algorithm to calculate the control points of the individual bezier curves creating the bezier spline.
 */
class ThomasMatrix<N : Number, V : Vector<N, V>>(private val math: MathHelper<N>) {

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
            val m = this.math.div(this.a[i], b[i - 1])
            b[i] = this.math.minus(b[i], this.math.times(this.c[i - 1], m))
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
            var m = this.math.div(a[i + 1], b[i])
            b[i + 1] = this.math.minus(b[i + 1], this.math.times(c[i], m))
            r[i + 1] = r[i + 1] - r[i] * m

            if (i > size - 3) continue

            if (i < size - 3) {
                lastColumn.add(this.math.times(lastColumn[i], this.math.negate(m)))
            } else { // i = n-3
                c[i + 1] = this.math.minus(c[i + 1], this.math.times(lastColumn[i], m))
            }

            m = this.math.div(lastRow, b[i])
            b[b.lastIndex] = this.math.minus(b[b.lastIndex], this.math.times(lastColumn[i], m))

            if (i < size - 3) {
                lastRow = this.math.times(c[i], this.math.negate(m))
            } else { // i = n-3
                a[a.lastIndex] = this.math.minus(a[a.lastIndex], this.math.times(c[i], m))
            }

            r[r.lastIndex] -= r[i] * m
        }

        lastColumn.add(this.math.zero)

        val result = mutableListOf<V>()
        result.add(r.last() / b.last())

        for (i in 1 until size) {
            val realIndex = size - 1 - i
            result.add((r[realIndex] - result[i - 1] * c[realIndex] - result[0] * lastColumn[realIndex]) / b[realIndex])
        }

        return result.reversed()
    }
}
