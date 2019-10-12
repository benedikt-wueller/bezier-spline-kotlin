package dev.benedikt.math.bezier

import java.lang.IndexOutOfBoundsException

class SegmentIndexOutOfBoundsException(index: Int) : IndexOutOfBoundsException("Segment index out of bounds: $index.")