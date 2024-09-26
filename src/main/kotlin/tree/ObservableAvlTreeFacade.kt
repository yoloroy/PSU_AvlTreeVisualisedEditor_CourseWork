package tree

fun <T : Comparable<T>> AvlTreeFacade<T>.observed(onChange: () -> Unit) = ObservableAvlTreeFacade(this, onChange)

class ObservableAvlTreeFacade<T : Comparable<T>>(
    private val concrete: AvlTreeFacade<T>,
    val onChange: () -> Unit
) : AvlTreeFacade<T> by concrete {

    override fun insert(value: T) {
        concrete.insert(value)
        onChange()
    }

    override fun remove(value: T) {
        concrete.remove(value)
        onChange()
    }

    override fun replace(oldValue: T, newValue: T) {
        concrete.replace(oldValue, newValue)
        onChange()
    }
}
