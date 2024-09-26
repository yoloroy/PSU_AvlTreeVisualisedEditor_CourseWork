package tree

class AvlTreeFacadeImpl<T : Comparable<T>>() : AvlTreeFacade<T> {

    constructor(vararg values: T) : this() {
        values.forEach(::insert)
    }

    override var root: BinaryTree<ValueWithHeight<T>>? = null
        private set

    override fun insert(value: T) {
        root = root.inserted(value)
    }

    override fun remove(value: T) {
        root = root.removed(value)
    }

    override fun replace(oldValue: T, newValue: T) {
        root = root.removed(oldValue).inserted(newValue)
    }

    override fun contains(value: T): Boolean {
        val node = root?.search {
            when {
                value == it.value -> it
                value < it.value -> it.left
                value > it.value -> it.right
                else -> null
            }
        }

        return node != null
    }
}
