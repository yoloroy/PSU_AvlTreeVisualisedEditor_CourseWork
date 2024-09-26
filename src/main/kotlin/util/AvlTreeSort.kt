package util

import tree.AvlTree
import tree.inserted
import tree.leftToRightIterator

fun sortUsingAvlTree(array: IntArray): Iterable<Int> {
    return array
        .fold(null, AvlTree<Int>?::inserted)
        .leftToRightIterator().asSequence().map { it.state.value }
        .asIterable()
}
