package dev.benedikt.math.bezierspline.matrix

import dev.benedikt.math.bezierspline.vector.Vector

abstract class ThomasMatrix<N : Number, V : Vector<N, V>> {

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

    abstract fun solveThomas() : List<V>
    abstract fun solveThomasClosed() : List<V>
}
