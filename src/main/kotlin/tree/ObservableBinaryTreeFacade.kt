package tree

fun <T> BinaryTreeFacade<T>.observed(onChange: () -> Unit) = ObservableBinaryTreeFacade(this, onChange)

class ObservableBinaryTreeFacade<T>(
    private val concrete: BinaryTreeFacade<T>,
    private val onChange: () -> Unit
): BinaryTreeFacade<T> by concrete {

    override fun attach(target: T, value: T) = concrete.attach(target, value).also { onChange() }

    override fun remove(target: T) = concrete.remove(target).also { onChange() }

    override fun removeForcefully(target: T) = concrete.removeForcefully(target).also { onChange() }

    override fun replace(oldValue: T, newValue: T) = concrete.replace(oldValue, newValue).also { onChange() }

    override fun rotateLeftAround(target: T) = concrete.rotateLeftAround(target).also { onChange() }

    override fun rotateRightAround(target: T) = concrete.rotateRightAround(target).also { onChange() }

    override fun rotateLeftRightAround(target: T) = concrete.rotateLeftRightAround(target).also { onChange() }

    override fun rotateRightLeftAround(target: T) = concrete.rotateRightLeftAround(target).also { onChange() }
}
