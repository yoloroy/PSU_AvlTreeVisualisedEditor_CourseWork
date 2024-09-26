package tree

class BinaryTreeFacadeImpl<T> private constructor(root: BinaryTree<T>) : BinaryTreeFacade<T> {

    constructor(rootValue: T, vararg values: T) : this(BinaryTree.fromRepresentation(rootValue, *values))

    override var root: BinaryTree<T> = root
        private set

    override fun attach(target: T, value: T): Boolean {
        val node = findOrNull(target) ?: return false

        if (node.left == null) {
            node.left = BinaryTree(value)
            return true
        }
        if (node.right == null) {
            node.right = BinaryTree(value)
            return true
        }

        return false
    }

    override fun remove(target: T): Boolean {
        var parentOfTarget = null as BinaryTree<T>?
        var node = null as BinaryTree<T>?
        for (it in root.breadthFirstNodesIterator()) {
            if (it.left?.state == target) {
                parentOfTarget = it
                node = it.left
                break
            }
            if (it.right?.state == target) {
                parentOfTarget = it
                node = it.right
                break
            }
            if (it.state == target) {
                node = it
                break
            }
        }
        node ?: return false

        // we cannot delete this node because it has two children, thus cannot be replaced
        if (node.left != null && node.right != null) {
            return false
        }

        // node have no one to be replaced with
        if (node.left == null && node.right == null) {
            // we cannot leave root == null
            if (parentOfTarget == null) {
                return false
            } else {
                if (parentOfTarget.left === node) {
                    parentOfTarget.left = null
                } else {
                    parentOfTarget.right = null
                }

                return true
            }
        }

        // remaining the case where either left or right child is not equal to null, except not both
        if (parentOfTarget == null) { // means node is the root
            root = (node.left ?: node.right)!!
        } else {
            if (parentOfTarget.left === node) {
                parentOfTarget.left = node.left
            } else {
                parentOfTarget.right = node.right
            }
        }

        return true
    }

    override fun removeForcefully(target: T): Boolean {
        var parentOfTarget = null as BinaryTree<T>?
        var node = null as BinaryTree<T>?
        for (it in root.breadthFirstNodesIterator()) {
            if (it.left?.state == target) {
                parentOfTarget = it
                node = it.left
                break
            }
            if (it.right?.state == target) {
                parentOfTarget = it
                node = it.right
                break
            }
            if (it.state == target) {
                node = it
                break
            }
        }
        node ?: return false

        // node have no one to be replaced with
        if (node.left == null && node.right == null) {
            // we cannot leave root == null
            if (parentOfTarget == null) {
                return false
            } else {
                if (parentOfTarget.left === node) {
                    parentOfTarget.left = null
                } else {
                    parentOfTarget.right = null
                }

                return true
            }
        }

        // remaining the case where either left or right child is not equal to null, except not both
        if (parentOfTarget == null) { // means node is the root
            root = (node.left ?: node.right)!!
        } else {
            if (parentOfTarget.left === node) {
                parentOfTarget.left = node.left
            } else {
                parentOfTarget.right = node.right
            }
        }

        return true
    }

    override fun replace(oldValue: T, newValue: T): Boolean {
        return findOrNull(oldValue)?.apply { state = newValue } != null
    }

    override fun rotateLeftAround(target: T): Boolean {
        if (root.state == target) {
            root = root.leftRotatedNotAvl()
            return true
        }

        val prop = findPropByOrNull(target) ?: return false

        prop.set(prop.get()!!.leftRotatedNotAvl())
        return true
    }

    override fun rotateRightAround(target: T): Boolean {
        if (root.state == target) {
            root = root.rightRotatedNotAvl()
            return true
        }

        val prop = findPropByOrNull(target) ?: return false

        prop.set(prop.get()!!.rightRotatedNotAvl())
        return true
    }

    override fun rotateLeftRightAround(target: T): Boolean {
        if (root.state == target) {
            root = root.leftRightRotatedNotAvl()
            return true
        }

        val prop = findPropByOrNull(target) ?: return false

        prop.set(prop.get()!!.leftRightRotatedNotAvl())
        return true
    }

    override fun rotateRightLeftAround(target: T): Boolean {
        if (root.state == target) {
            root = root.rightLeftRotatedNotAvl()
            return true
        }

        val prop = findPropByOrNull(target) ?: return false

        prop.set(prop.get()!!.rightLeftRotatedNotAvl())
        return true
    }

    override fun canRotateLeftAround(target: T): Boolean {
        return findOrNull(target)?.canRotateLeft() == true
    }

    override fun canRotateRightAround(target: T): Boolean {
        return findOrNull(target)?.canRotateRight() == true
    }

    override fun canRotateLeftRightAround(target: T): Boolean {
        return findOrNull(target)?.canRotateLeftRight() == true
    }

    override fun canRotateRightLeftAround(target: T): Boolean {
        return findOrNull(target)?.canRotateRightLeft() == true
    }

    override fun contains(value: T): Boolean {
        return root
            .leftToRightIterator()
            .asSequence()
            .any { it.state == value }
    }

    private fun findOrNull(target: T): BinaryTree<T>? {
        if (root.state == target) {
            return root
        }

        return root
            .breadthFirstNodesIterator()
            .asSequence()
            .firstOrNull { it.state == target }
    }

    private fun findPropByOrNull(target: T) = root
        .breadthFirstNodesIterator()
        .asSequence()
        .firstNotNullOfOrNull {
            when (target) {
                it.left?.state -> it::left
                it.right?.state -> it::right
                else -> null
            }
        }
}
