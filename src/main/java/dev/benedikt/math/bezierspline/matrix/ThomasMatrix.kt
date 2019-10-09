package dev.benedikt.math.bezierspline.matrix

import dev.benedikt.math.bezierspline.vector.Vector

abstract class ThomasMatrix<N : Number, V : Vector<N, V>> {

    protected abstract val zero: N

    protected val a = mutableListOf<N>()
    protected val b = mutableListOf<N>()
    protected val c = mutableListOf<N>()
    protected val r = mutableListOf<V>()

    fun set(a: N, b: N, c: N, r: V) {
        this.a.add(a)
        this.b.add(b)
        this.c.add(c)
        this.r.add(r)
    }

    fun solveThomas() : List<V> {
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

    fun solveThomasClosed() : List<V> {
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

        lastColumn.add(this.zero)

        val result = mutableListOf<V>()
        result.add(r.last() / b.last())

        for (i in 1 until size) {
            val realIndex = size - 1 - i
            result.add((r[realIndex] - result[i - 1] * c[realIndex] - result[0] * lastColumn[realIndex]) / b[realIndex])
        }

        return result.reversed()
    }

    //
    // Math helpers for generic types
    //

    abstract fun plus(a: N, b: N) : N
    abstract fun minus(a: N, b: N) : N
    abstract fun times(a: N, b: N) : N
    abstract fun div(a: N, b: N) : N

    /**
     * Equivalent of -1 * n.
     *
     * @param n the number to negate.
     * @return the negated number.
     */
    abstract fun negate(n: N) : N
}
