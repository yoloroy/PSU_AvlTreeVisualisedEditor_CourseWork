import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import tree.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AvlTreeApp(tree: AvlTreeFacade<Int>) {

    val iconLabelSpace = 12.dp

    var isLtrOrderDemonstrated by remember { mutableStateOf(false) }
    val renderFunction: (AvlTree<Int>?) -> ImageBitmap = { graph: AvlTree<Int>? ->
        if (isLtrOrderDemonstrated) {
            graph.linedRenderToImageBitmap { it.value.toString() }
        } else {
            graph.renderToImageBitmap { it.value.toString() }
        }
    }

    var renderedGraph by remember(isLtrOrderDemonstrated) { mutableStateOf(renderFunction(tree.root)) }
    var showSingleValueDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var dropDownMenuActive by remember { mutableStateOf(false) }
    val contextAvlTree = tree.observed { renderedGraph = renderFunction(tree.root) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                    dropDownMenuActive = true
                }
        ) {
            Image(renderedGraph, "", modifier = Modifier.fillMaxSize())
            Text(
                text = "Справка:\n\tНажатие ПКМ открывает\n\tменю для работы с деревом",
                modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
            )
        }

        DropdownMenu(
            expanded = dropDownMenuActive,
            onDismissRequest = { dropDownMenuActive = false }
        ) {
            DropdownMenuItem(
                onClick = { isLtrOrderDemonstrated = !isLtrOrderDemonstrated }
            ) {
                Icon(if (!isLtrOrderDemonstrated) Icons.Default.TextRotationNone else Icons.Default.TextRotationDown, "Insert")
                Spacer(Modifier.width(iconLabelSpace))
                Text(if (!isLtrOrderDemonstrated) "Продемонстрировать проход слева направо" else "Вернуть вид сверху вниз")
            }
            DropdownMenuItem(
                onClick = { showSingleValueDialog = true }
            ) {
                Icon(Icons.Default.Add, "Insert")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Вставить")
            }
            DropdownMenuItem(
                onClick = { showSingleValueDialog = true }
            ) {
                Icon(Icons.Default.Delete, "Remove")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Удалить")
            }
            DropdownMenuItem(
                onClick = { showSingleValueDialog = true }
            ) {
                Icon(Icons.Default.Search, "Search")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Найти")
            }
            DropdownMenuItem(
                onClick = { showEditDialog = true }
            ) {
                Icon(Icons.Default.Edit, "Edit")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Модифицировать")
            }
        }

        if (showSingleValueDialog) {
            DialogWindow(
                title = "Работа с одним значением",
                onCloseRequest = { showSingleValueDialog = false }
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SingleValueDialogContent(contextAvlTree)
                }
            }
        }
        if (showEditDialog) {
            DialogWindow(
                title = "Замена",
                onCloseRequest = { showEditDialog = false }
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EditDialogContent(contextAvlTree)
                }
            }
        }
    }
}

@Composable
private fun SingleValueDialogContent(tree: AvlTreeFacade<Int>) {
    var value by remember { mutableStateOf("0") }
    val valid = remember(value) { value.toIntOrNull() != null }
    val isTreeContainsValue = value.toIntOrNull()?.let(tree::contains)

    OutlinedTextField(
        value = value,
        isError = !valid,
        onValueChange = {
            value = it
        },
        colors = when (isTreeContainsValue) {
            true -> outlinedTextFieldColors(
                focusedBorderColor = Color(0, 0xc0, 0x18).copy(alpha = 0.8f),
                focusedLabelColor = Color(0, 0xc0, 0x18).copy(alpha = 0.8f),
                unfocusedBorderColor = Color(0, 0xc0, 0x18).copy(alpha = 0.5f),
                unfocusedLabelColor = Color(0, 0xc0, 0x18).copy(alpha = 0.5f)
            )
            false -> outlinedTextFieldColors(
                focusedBorderColor = Color(0xff, 0x80, 0x20).copy(alpha = 0.8f),
                focusedLabelColor = Color(0xff, 0x80, 0x20).copy(alpha = 0.8f),
                unfocusedBorderColor = Color(0xff, 0x80, 0x20).copy(alpha = 0.5f),
                unfocusedLabelColor = Color(0xff, 0x80, 0x20).copy(alpha = 0.5f)
            )
            null -> outlinedTextFieldColors()
        },
        label = when (isTreeContainsValue) {
            true -> ({ Text("Было найдено в АВЛ дереве") })
            false -> ({ Text("Не было найдено в АВЛ дереве") })
            null -> ({ Text("Значение не число") })
        },
        trailingIcon = {
            Row {
                IconButton(
                    onClick = { tree.insert(value.toInt()) },
                    enabled = valid
                ) {
                    Icon(Icons.Default.Add, "Insert")
                }
                IconButton(
                    onClick = { tree.remove(value.toInt()) },
                    enabled = valid
                ) {
                    Icon(Icons.Default.Delete, "Remove")
                }
            }
        }
    )
}

context(ColumnScope)
@Composable
private fun EditDialogContent(tree: AvlTreeFacade<Int>) {
    var replacee by remember { mutableStateOf("0") }
    val isReplaceeValid = remember(replacee) { replacee.toIntOrNull() != null }
    var replacer by remember { mutableStateOf("0") }
    val isReplacerValid = remember(replacer) { replacer.toIntOrNull() != null }

    val isReplaceeFound = replacee.toIntOrNull()?.let(tree::contains) == true

    fun treeChange(block: () -> Unit): () -> Unit = {
        block()
    }

    OutlinedTextField(
        value = replacee,
        isError = !isReplaceeValid || !isReplaceeFound,
        onValueChange = { replacee = it },
        label = {
            Text("Заменяемое" + if (!isReplaceeFound) " не найдено" else "")
        }
    )
    OutlinedTextField(
        value = replacer,
        isError = !isReplacerValid,
        onValueChange = { replacer = it },
        label = {
            Text("Заменитель")
        }
    )
    IconButton(
        onClick = treeChange { tree.replace(replacee.toInt(), replacer.toInt()) },
        enabled = isReplaceeValid && isReplacerValid && isReplaceeFound,
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.End)
    ) {
        Icon(Icons.Default.Edit, "Replace")
    }
}
