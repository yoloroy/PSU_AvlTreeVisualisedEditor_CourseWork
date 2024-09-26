package tree

interface BinaryTreeFacade<T> {

    val root: BinaryTree<T>

    fun attach(target: T, value: T): Boolean

    fun remove(target: T): Boolean

    fun removeForcefully(target: T): Boolean

    fun replace(oldValue: T, newValue: T): Boolean

    fun rotateLeftAround(target: T): Boolean

    fun rotateRightAround(target: T): Boolean

    fun rotateLeftRightAround(target: T): Boolean

    fun rotateRightLeftAround(target: T): Boolean

    fun canRotateLeftAround(target: T): Boolean

    fun canRotateRightAround(target: T): Boolean

    fun canRotateLeftRightAround(target: T): Boolean

    fun canRotateRightLeftAround(target: T): Boolean

    operator fun contains(value: T): Boolean
}
