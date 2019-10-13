package dev.benedikt.math.bezier

/**
 * This object provides some pre-defined resolution constants which can be used to adjust the speed and accuracy of the computedLength estimation.
 * The more extreme some sections of the spline are, the finer the resolution has to be in order to provide a reliable estimation.
 */
object Resolution {

    const val ROUGHEST = 100
    const val ROUGHER  = 1000
    const val ROUGH    = 10000
    const val FINE     = 100000
    const val FINER    = 1000000
    const val FINEST   = 10000000

    const val DEFAULT = this.ROUGHEST
}
