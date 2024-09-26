@file:Suppress("DEPRECATION")

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import tree.BinaryTree
import tree.BinaryTreeFacade
import tree.leftToRightIterator
import tree.observed

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BinaryTreeApp(tree: BinaryTreeFacade<Int>) {

    val iconLabelSpace = 12.dp

    var isLtrOrderDemonstrated by remember { mutableStateOf(false) }
    val renderFunction: (BinaryTree<Int>?) -> ImageBitmap = { graph: BinaryTree<Int>? ->
        if (isLtrOrderDemonstrated) {
            graph.linedRenderToImageBitmap(Int::toString)
        } else {
            graph.renderToImageBitmap(Int::toString)
        }
    }

    var renderedGraph by remember(isLtrOrderDemonstrated) { mutableStateOf(renderFunction(tree.root)) }
    var dropDownMenuActive by remember { mutableStateOf(false) }
    val contextTree = tree.observed { renderedGraph = renderFunction(tree.root) }

    var showInsertionDialog by remember { mutableStateOf(false) }
    var showRemovalDialog by remember { mutableStateOf(false) }
    var showReplacementDialog by remember { mutableStateOf(false) }
    var showRotationDialog by remember { mutableStateOf(false) }

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
                onClick = { showInsertionDialog = true }
            ) {
                Icon(Icons.Default.Add, "Insert")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Добавить дочерний узел")
            }
            DropdownMenuItem(
                onClick = { showRemovalDialog = true }
            ) {
                Icon(Icons.Default.Search, "Search")
                Text("/")
                Icon(Icons.Default.Delete, "Delete")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Найти/удалить узел")
            }
            DropdownMenuItem(
                onClick = { showReplacementDialog = true }
            ) {
                Icon(Icons.Default.Edit, "Edit")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Изменить значение в узле")
            }
            DropdownMenuItem(
                onClick = { showRotationDialog = true }
            ) {
                Icon(Icons.Default.RotateRight, "Rotate")
                Spacer(Modifier.width(iconLabelSpace))
                Text("Выполнить поворот вокруг узла")
            }
        }

        if (showInsertionDialog) {
            DialogWindow(
                title = "Добавить дочерний узел",
                onCloseRequest = { showInsertionDialog = false }
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InsertionDialogContent(contextTree)
                }
            }
        }

        if (showRemovalDialog) {
            DialogWindow(
                title = "Найти/удалить узел",
                onCloseRequest = { showRemovalDialog = false }
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RemovalDialogContent(contextTree)
                }
            }
        }

        if (showReplacementDialog) {
            DialogWindow(
                title = "Изменить значение в узле",
                onCloseRequest = { showReplacementDialog = false }
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReplacementDialogContent(contextTree)
                }
            }
        }

        if (showRotationDialog) {
            val state = rememberDialogState(size = DpSize(400.dp, 550.dp))

            DialogWindow(
                title = "Выполнить поворот вокруг узла",
                state = state,
                onCloseRequest = { showRotationDialog = false }
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RotationDialogContent(contextTree)
                }
            }
        }
    }
}

context(ColumnScope)
@Composable
private fun InsertionDialogContent(tree: BinaryTreeFacade<Int>) {
    val parent = object {
        var value by remember { mutableStateOf("0") }
        val valid = remember(value) { value.toIntOrNull() != null }
        val exists = remember(valid, value) { valid && value.toInt() in tree }
        val node = remember(value, valid, exists) {
            if (!valid || !exists) return@remember null

            tree.root
                .leftToRightIterator()
                .asSequence()
                .first { it.state == value.toInt() }
        }
    }
    val newItem = object {
        var value by remember { mutableStateOf("0") }
        val valid = remember(value) { value.toIntOrNull() != null }
        val hasAvailablePlace = remember(parent.node) {
            parent.node?.run { left == null || right == null } ?: false
        }
    }

    val existsColor = remember { Color(0, 0xc0, 0x18) }
    val notExistsColor = Color(0xff, 0x80, 0x20)

    with(parent) {
        OutlinedTextField(
            value = value,
            isError = !(valid && exists),
            onValueChange = { value = it },
            colors = when {
                valid && exists -> outlinedTextFieldColors(
                    focusedBorderColor = existsColor.copy(alpha = 0.8f),
                    focusedLabelColor = existsColor.copy(alpha = 0.8f),
                    unfocusedBorderColor = existsColor.copy(alpha = 0.5f),
                    unfocusedLabelColor = existsColor.copy(alpha = 0.5f)
                )
                valid && !exists -> outlinedTextFieldColors(
                    focusedBorderColor = notExistsColor.copy(alpha = 0.8f),
                    focusedLabelColor = notExistsColor.copy(alpha = 0.8f),
                    unfocusedBorderColor = notExistsColor.copy(alpha = 0.5f),
                    unfocusedLabelColor = notExistsColor.copy(alpha = 0.5f)
                )
                else -> outlinedTextFieldColors()
            },
            label = when {
                valid && exists -> ({ Text("Было найдено в АВЛ дереве") })
                valid && !exists -> ({ Text("Не было найдено в АВЛ дереве") })
                else -> ({ Text("Значение не число") })
            }
        )
    }

    with(newItem) {
        OutlinedTextField(
            value = value,
            isError = !valid,
            onValueChange = { value = it },
            label = when {
                !valid -> ({ Text("Значение не число") })
                !parent.valid || !parent.exists -> ({ Text("Родительский элемент не валиден") })
                !hasAvailablePlace -> ({ Text("Нет свободной позиции") })
                else -> ({ Text("Может быть добавлено") })
            }
        )
    }

    IconButton(
        onClick = { tree.attach(parent.value.toInt(), newItem.value.toInt()) },
        enabled = newItem.hasAvailablePlace,
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.End)
    ) {
        Icon(Icons.Default.Add, "Insert")
    }
}

context(ColumnScope)
@Composable
private fun RemovalDialogContent(tree: BinaryTreeFacade<Int>) {
    var value by remember { mutableStateOf("0") }
    val valid = remember(value) { value.toIntOrNull() != null }
    val exists = remember(valid, value) { valid && value.toInt() in tree }

    val existsColor = remember { Color(0, 0xc0, 0x18) }
    val notExistsColor = Color(0xff, 0x80, 0x20)

    OutlinedTextField(
        value = value,
        isError = !(valid && exists),
        onValueChange = { value = it },
        colors = when {
            valid && exists -> outlinedTextFieldColors(
                focusedBorderColor = existsColor.copy(alpha = 0.8f),
                focusedLabelColor = existsColor.copy(alpha = 0.8f),
                unfocusedBorderColor = existsColor.copy(alpha = 0.5f),
                unfocusedLabelColor = existsColor.copy(alpha = 0.5f)
            )
            valid && !exists -> outlinedTextFieldColors(
                focusedBorderColor = notExistsColor.copy(alpha = 0.8f),
                focusedLabelColor = notExistsColor.copy(alpha = 0.8f),
                unfocusedBorderColor = notExistsColor.copy(alpha = 0.5f),
                unfocusedLabelColor = notExistsColor.copy(alpha = 0.5f)
            )
            else -> outlinedTextFieldColors()
        },
        label = when {
            valid && exists -> ({ Text("Было найдено в АВЛ дереве") })
            valid && !exists -> ({ Text("Не было найдено в АВЛ дереве") })
            else -> ({ Text("Значение не число") })
        }
    )

    IconButton(
        onClick = { tree.remove(value.toInt()) },
        enabled = exists,
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.End)
    ) {
        Icon(Icons.Default.Delete, "Remove")
    }
}

context(ColumnScope)
@Composable
private fun ReplacementDialogContent(tree: BinaryTreeFacade<Int>) {
    val target = object {
        var value by remember { mutableStateOf("0") }
        val valid = remember(value) { value.toIntOrNull() != null }
        val exists = remember(valid, value) { valid && value.toInt() in tree }
    }
    val replacement = object {
        var value by remember { mutableStateOf("0") }
        val valid = remember(value) { value.toIntOrNull() != null }
    }

    val existsColor = remember { Color(0, 0xc0, 0x18) }
    val notExistsColor = Color(0xff, 0x80, 0x20)

    with(target) {
        OutlinedTextField(
            value = value,
            isError = !(valid && exists),
            onValueChange = { value = it },
            colors = when {
                valid && exists -> outlinedTextFieldColors(
                    focusedBorderColor = existsColor.copy(alpha = 0.8f),
                    focusedLabelColor = existsColor.copy(alpha = 0.8f),
                    unfocusedBorderColor = existsColor.copy(alpha = 0.5f),
                    unfocusedLabelColor = existsColor.copy(alpha = 0.5f)
                )
                valid && !exists -> outlinedTextFieldColors(
                    focusedBorderColor = notExistsColor.copy(alpha = 0.8f),
                    focusedLabelColor = notExistsColor.copy(alpha = 0.8f),
                    unfocusedBorderColor = notExistsColor.copy(alpha = 0.5f),
                    unfocusedLabelColor = notExistsColor.copy(alpha = 0.5f)
                )
                else -> outlinedTextFieldColors()
            },
            label = when {
                valid && exists -> ({ Text("Было найдено в АВЛ дереве") })
                valid && !exists -> ({ Text("Не было найдено в АВЛ дереве") })
                else -> ({ Text("Значение не число") })
            }
        )
    }

    with(replacement) {
        OutlinedTextField(
            value = value,
            isError = !valid,
            onValueChange = { value = it },
            label = when {
                !valid -> ({ Text("Значение не число") })
                !target.valid || !target.exists -> ({ Text("Нечего заменять") })
                else -> ({ Text("Замещяющее значение") })
            }
        )
    }

    IconButton(
        onClick = { tree.replace(target.value.toInt(), replacement.value.toInt()) },
        enabled = replacement.valid && target.exists,
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.End)
    ) {
        Icon(Icons.Default.Add, "Insert")
    }
}

context(ColumnScope)
@Composable
private fun RotationDialogContent(tree: BinaryTreeFacade<Int>) {
    var value by remember { mutableStateOf("0") }
    val valid = remember(value) { value.toIntOrNull() != null }
    val exists = remember(valid, value) { valid && value.toInt() in tree }

    val existsColor = remember { Color(0, 0xc0, 0x18) }
    val notExistsColor = Color(0xff, 0x80, 0x20)

    OutlinedTextField(
        value = value,
        isError = !(valid && exists),
        onValueChange = { value = it },
        colors = when {
            valid && exists -> outlinedTextFieldColors(
                focusedBorderColor = existsColor.copy(alpha = 0.8f),
                focusedLabelColor = existsColor.copy(alpha = 0.8f),
                unfocusedBorderColor = existsColor.copy(alpha = 0.5f),
                unfocusedLabelColor = existsColor.copy(alpha = 0.5f)
            )
            valid && !exists -> outlinedTextFieldColors(
                focusedBorderColor = notExistsColor.copy(alpha = 0.8f),
                focusedLabelColor = notExistsColor.copy(alpha = 0.8f),
                unfocusedBorderColor = notExistsColor.copy(alpha = 0.5f),
                unfocusedLabelColor = notExistsColor.copy(alpha = 0.5f)
            )
            else -> outlinedTextFieldColors()
        },
        label = when {
            valid && exists -> ({ Text("Было найдено в АВЛ дереве") })
            valid && !exists -> ({ Text("Не было найдено в АВЛ дереве") })
            else -> ({ Text("Значение не число") })
        }
    )

    TextButton(
        onClick = { tree.rotateLeftAround(value.toInt()) },
        enabled = valid && exists && tree.canRotateLeftAround(value.toInt()),
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        Icon(Icons.Default.RotateLeft, "LL rotation")
        Text("Выполнить LL поворот")
    }

    TextButton(
        onClick = { tree.rotateRightAround(value.toInt()) },
        enabled = valid && exists && tree.canRotateRightAround(value.toInt()),
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        Icon(Icons.Default.RotateRight, "RR rotation")
        Text("Выполнить RR поворот")
    }

    TextButton(
        onClick = { tree.rotateLeftRightAround(value.toInt()) },
        enabled = valid && exists && tree.canRotateLeftRightAround(value.toInt()),
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        Icon(Icons.Default.RotateLeft, "L rotation")
        Icon(Icons.Default.RotateRight, "R rotation")
        Text("Выполнить LR поворот")
    }

    TextButton(
        onClick = { tree.rotateRightLeftAround(value.toInt()) },
        enabled = valid && exists && tree.canRotateRightLeftAround(value.toInt()),
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        Icon(Icons.Default.RotateRight, "R rotation")
        Icon(Icons.Default.RotateLeft, "L rotation")
        Text("Выполнить RL поворот")
    }
}
