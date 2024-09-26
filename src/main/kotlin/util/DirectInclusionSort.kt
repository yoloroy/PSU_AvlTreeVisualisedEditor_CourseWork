package util

private data class OrderedIntLinkedList(val value: Int, var next: OrderedIntLinkedList? = null)

fun sortByDirectInclusion(array: IntArray): Iterable<Int> {
    val lap = object {
        var head: OrderedIntLinkedList? = OrderedIntLinkedList(array.first())
    }

    for (value in array) {
        var ptr = lap::head
        var node = ptr.get()

        while (node != null && value > node.value) {
            ptr = node::next
            node = node.next
        }

        ptr.set(OrderedIntLinkedList(value, node))
    }

    return iterator {
        var current = lap.head

        while (current != null) {
            yield(current.value)
            current = current.next ?: return@iterator
        }
    }.asSequence().asIterable()
}
