package tree

interface AvlTreeFacade<T : Comparable<T>> {

    val root: AvlTree<T>?

    fun insert(value: T)

    fun remove(value: T)

    fun replace(oldValue: T, newValue: T)

    operator fun contains(value: T): Boolean
}
