import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import tree.AvlTreeFacadeImpl
import tree.BinaryTreeFacadeImpl

fun main() {
    val avlTree = AvlTreeFacadeImpl(10, 20, 30, 40, 50, 60, 70, 80, 90)
    val binaryTree = BinaryTreeFacadeImpl(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110)

    application {
        Window(onCloseRequest = ::exitApplication, title = "Фасад работы с АВЛ-Деревом") {
            AvlTreeApp(avlTree)
        }
        Window(onCloseRequest = ::exitApplication, title = "Фасад работы с Бинарным Деревом") {
            BinaryTreeApp(binaryTree)
        }
        Window(onCloseRequest = ::exitApplication, title = "Сравнение структур") {
            StructuresComparisonApp()
        }
    }
}
