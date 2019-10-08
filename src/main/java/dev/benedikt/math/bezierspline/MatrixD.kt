package dev.benedikt.math.bezierspline

import dev.benedikt.math.bezierspline.vector.VectorD

class MatrixD<V : VectorD<V>> {

    private val a = mutableListOf<Double>()
    private val b = mutableListOf<Double>()
    private val c = mutableListOf<Double>()
    private val r = mutableListOf<V>()

    fun set(a: Double, b: Double, c: Double, r: V) {
        this.a.add(a)
        this.b.add(b)
        this.c.add(c)
        this.r.add(r)
    }

    fun solveThomas() : List<V> {
        val b = this.b.toMutableList()
        val r = this.r.toMutableList()

        for (i in 1 until r.size) {
            val m = this.a[i] / b[i - 1]
            b[i] = b[i] - this.c[i - 1] * m
            r[i] = r[i] - r[i - 1] * m
        }

        val result = mutableListOf<V>()
        result.add(r.last() / b.last())
        for (i in 1 until r.size) {
            result.add(((r[i] - this.c[i]) * result[i - 1]) / b[i])
        }

        return result.reversed()
    }

    fun solveThomasClosed() : List<V> {
        val a = this.a.toMutableList()
        val b = this.b.toMutableList()
        val c = this.c.toMutableList()
        val r = this.r.toMutableList()

        val size = r.size

        val lastColumn = mutableListOf(a.first())
        var lastRow = c[c.lastIndex]

        for (i in 0 until size - 1) {
            var m = a[i + 1] / b[i]
            b[i + 1] -= c[i] * m
            r[i + 1] -= r[i] * m

            if (i > size - 3) continue

            if (i < size - 3) {
                lastColumn.add(lastColumn[i] * -m)
            } else { // i = n-3
                c[i + 1] -= lastColumn[i] * m
            }

            m = lastRow / b[i]
            b[b.lastIndex] -= lastColumn[i] * m

            if (i < size - 3) {
                lastRow = c[i] * -m
            } else { // i = n-3
                a[a.lastIndex - 1] -= c[i] * m
            }

            r[r.lastIndex] -= r[i] * m
        }

        val result = mutableListOf<V>()
        result.add(r.last() / b.last())

        lastColumn.add(0.0)
        for (i in 1 until size) {
            result.add(((r[i] - c[i]) * result[i - 1] - result[0] * lastColumn[size - 1 - i]) / b[i])
        }

        return result.reversed()
    }
}
