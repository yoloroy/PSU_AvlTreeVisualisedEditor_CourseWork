package tree

typealias AvlTree<T> = BinaryTree<ValueWithHeight<T>>

class BinaryTree<T>(
    var state: T,
    var left: BinaryTree<T>? = null,
    var right: BinaryTree<T>? = null
) {
    companion object {
        fun <T> fromRepresentation(firstValue: T, vararg values: T) = fromRepresentation(listOf(firstValue) + values.toList())

        fun <T> fromRepresentation(values: List<T>): BinaryTree<T> {
            val nodes = values.map { BinaryTree(it) }
            for ((i, node) in nodes.withIndex()) {
                node.left = nodes.getOrNull(i * 2 + 1)
                node.right = nodes.getOrNull(i * 2 + 2)
            }

            return nodes[0]
        }
    }

    operator fun get(index: Int) = when (index) {
        0 -> left
        1 -> right
        else -> throw IllegalArgumentException()
    }

    operator fun set(index: Int, node: BinaryTree<T>?) = when (index) {
        0 -> left = node
        1 -> right = node
        else -> throw IllegalArgumentException()
    }
}

data class ValueWithHeight<T>(
    var value: T,
    var height: Int
)

fun <T> BinaryTree<T>.leftest(): BinaryTree<T> {
    var current = this

    while (current.left != null) {
        current = current.left!!
    }

    return current
}

operator fun <T : Comparable<T>> AvlTree<T>?.contains(toFind: T): Boolean = when {
    this == null -> false
    toFind == value -> true
    toFind < value -> toFind in left
    toFind > value -> toFind in right
    else -> throw IllegalArgumentException("Something wrong with comparable")
}

inline fun <T> BinaryTree<T>.search(next: (BinaryTree<T>) -> BinaryTree<T>?): BinaryTree<T>? {
    var current: BinaryTree<T> = this
    var nextCurrent = current
    do {
        current = nextCurrent
        nextCurrent = next(current) ?: return null
    } while (current !== nextCurrent)

    return current
}

fun <T : Comparable<T>> avlTree(value: T, height: Int) = BinaryTree(ValueWithHeight(value, height))

var <T> AvlTree<T>.value: T
    get() = state.value
    set(value) { state.value = value }

var <T> AvlTree<T>.height: Int
    get() = state.height
    set(height) { state.height = height }

fun <T : Comparable<T>> AvlTree<T>?.inserted(value: T): AvlTree<T> {
    this ?: return avlTree(value, 1)

    when {
        value < this.value -> this.left = this.left.inserted(value)
        value > this.value -> this.right = this.right.inserted(value)
        else -> return this
    }

    updateHeight()
    val balance = balanceFactor
    return when {
        balance > 1 && value < left!!.value -> rightRotated()
        balance < -1 && value > right!!.value -> leftRotated()
        balance > 1 && value > left!!.value -> leftRightRotated()
        balance < -1 && value < right!!.value -> rightLeftRotated()
        else -> this
    }
}

fun <T : Comparable<T>> AvlTree<T>?.removed(value: T): AvlTree<T>? {
    this ?: return null

    var newRoot = this

    if (value < this.value) {
        left = left.removed(value)
    } else if (value > this.value) {
        right = right.removed(value)
    } else if (left == null || right == null) {
        newRoot = left ?: right
    } else {
        val replacer = right!!.leftest().value
        this.value = replacer
        right = right.removed(replacer)
    }

    return newRoot?.run {
        updateHeight()
        val balance = balanceFactor
        when {
            balance > 1 && left.balanceFactor >= 0 -> rightRotated()
            balance > 1 && left.balanceFactor < 0 -> leftRightRotated()
            balance < -1 && right.balanceFactor <= 0 -> leftRotated()
            balance < -1 && right.balanceFactor > 0 -> rightLeftRotated()
            else -> this
        }
    }
}

val <T> AvlTree<T>?.balanceFactor: Int get() = this?.run { left.heightOrZero - right.heightOrZero } ?: 0

val <T> AvlTree<T>?.heightOrZero: Int get() = this?.state?.height ?: 0

fun <T> AvlTree<T>.leftRotated(): AvlTree<T> {
    return leftRotatedNotAvl().also {
        updateHeight()
        it.updateHeight()
    }
}

fun <T> AvlTree<T>.rightRotated(): AvlTree<T> {
    return rightRotatedNotAvl().also {
        updateHeight()
        it.updateHeight()
    }
}

fun <T> AvlTree<T>.rightLeftRotated(): AvlTree<T> {
    right = right!!.rightRotated()
    return leftRotated()
}

fun <T> AvlTree<T>.leftRightRotated(): AvlTree<T> {
    left = left!!.leftRotated()
    return rightRotated()
}

fun <T> AvlTree<T>.updateHeight() {
    height = maxOf(left.heightOrZero, right.heightOrZero) + 1
}

fun <T> BinaryTree<T>?.leftToRightIterator(): Iterator<BinaryTree<T>> {
    suspend fun SequenceScope<BinaryTree<T>>.step(node: BinaryTree<T>) {
        node.left?.let { step(it) }
        yield(node)
        node.right?.let { step(it) }
    }

    return iterator { this@leftToRightIterator?.let { step(it) } }
}

fun <T> BinaryTree<T>?.breadthFirstNodesIterator() = iterator {
    var line = listOfNotNull(this@breadthFirstNodesIterator)

    while (line.isNotEmpty()) {
        yieldAll(line)
        line = line.flatMap { listOfNotNull(it.left, it.right) }
    }
}

fun <T> BinaryTree<T>.forEachTopToBottom(onEach: (BinaryTree<T>) -> Unit) {
    breadthFirstNodesIterator().forEach(onEach)
}

fun <T> BinaryTree<T>.forEachLeftToRight(onEach: (BinaryTree<T>) -> Unit) {
    leftToRightIterator().forEach(onEach)
}

fun <T> BinaryTree<T>.leftRotatedNotAvl(): BinaryTree<T> {
    val previousRight = right!!
    val newRight = previousRight.left

    previousRight.left = this
    right = newRight

    return previousRight
}

fun <T> BinaryTree<T>.rightRotatedNotAvl(): BinaryTree<T> {
    val previousLeft = left!!
    val newLeft = previousLeft.right

    previousLeft.right = this
    left = newLeft

    return previousLeft
}

fun <T> BinaryTree<T>.rightLeftRotatedNotAvl(): BinaryTree<T> {
    right = right!!.rightRotatedNotAvl()
    return leftRotatedNotAvl()
}

fun <T> BinaryTree<T>.leftRightRotatedNotAvl(): BinaryTree<T> {
    left = left!!.leftRotatedNotAvl()
    return rightRotatedNotAvl()
}

fun <T> BinaryTree<T>.canRotateLeft(): Boolean = right != null

fun <T> BinaryTree<T>.canRotateRight(): Boolean = left != null

fun <T> BinaryTree<T>.canRotateLeftRight(): Boolean = right?.left != null

fun <T> BinaryTree<T>.canRotateRightLeft(): Boolean = left?.right != null
