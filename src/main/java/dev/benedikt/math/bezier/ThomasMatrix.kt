package dev.benedikt.math.bezier

import dev.benedikt.math.bezier.math.MathComponent
import dev.benedikt.math.bezier.math.MathHelper
import dev.benedikt.math.bezier.vector.Vector

/**
 * A Matrix using the Thomas algorithm to calculate the control points of the individual bezier curves creating the bezier spline.
 */
class ThomasMatrix<N : Number, V : Vector<N, V>>(helper: MathHelper<N>) : MathComponent<N>(helper) {

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

    fun solve(closed: Boolean = false) : List<V> {
        // We need mutable copies.
        val a = this.a.toMutableList()
        val b = this.b.toMutableList()
        val c = this.c.toMutableList()
        val r = this.r.toMutableList()

        val size = r.size

        val lastColumn = mutableListOf(a.first())
        var lastRow = c[c.lastIndex]

        for (i in 1 until size) {
            var m = a[i] / b[i - 1]
            b[i] = b[i] - c[i - 1] * m
            r[i] = r[i] - r[i - 1] * m

            if (!closed) continue
            if (i > size - 3) break

            if (i < size - 3) {
                lastColumn.add(lastColumn[i - 1] * -m)
            } else { // i = n-3
                c[i] = c[i] - lastColumn[i - 1] * m
            }

            m = lastRow / b[i - 1]
            b[b.lastIndex] = b[b.lastIndex] - lastColumn[i - 1] * m

            if (i < size - 3) {
                lastRow = c[i - 1] * -m
            } else { // i = n-3
                a[a.lastIndex] = a[a.lastIndex] - c[i - 1] * m
            }

            r[r.lastIndex] -= r[i - 1] * m
        }

        if (closed) lastColumn.add(this.zero)

        val result = mutableListOf<V>()
        result.add(r.last() / b.last())

        for (i in 1 until size) {
            val realIndex = size - 1 - i
            val closedFactor = if (closed) lastColumn[realIndex] else this.zero
            result.add((r[realIndex] - result[i - 1] * c[realIndex] - result[0] * closedFactor) / b[realIndex])
        }

        return result.reversed()
    }
}
