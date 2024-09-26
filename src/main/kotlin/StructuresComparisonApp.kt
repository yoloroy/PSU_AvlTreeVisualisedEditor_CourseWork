import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sunnychung.lib.android.composabletable.ux.Table
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.*
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.LogAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import kotlinx.coroutines.*
import util.sortByDirectInclusion
import util.sortUsingAvlTree
import kotlin.concurrent.thread
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.measureTime

@Composable
fun StructuresComparisonApp() {
    val viewModel = remember { StructuresComparisonViewModel() }
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .scrollable(scrollState, Orientation.Vertical)
    ) {
        FunctionsSegment(
            regenerateArray = viewModel::regenerateArray,
            rerunStats = viewModel::rerunStats
        )

        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            TableSegment(viewModel = viewModel)
            ChartSegment(viewModel = viewModel)
        }
    }
}

context(RowScope)
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun ChartSegment(viewModel: StructuresComparisonViewModel) {
    var chartVisible by remember { mutableStateOf(true) }

    IconButton(
        onClick = { chartVisible = !chartVisible }
    ) {
        Icon(Icons.Default.BarChart, "Run")
    }

    if (!chartVisible || viewModel.stats.isEmpty()) return

    val yRange by remember { derivedStateOf {
        viewModel.stats.run {
            val min = minOf { it.time }.inWholeMicroseconds.toDouble()
            val max = maxOf { it.time }.inWholeMicroseconds.toDouble()
            log10(min).toInt() .. ceil(log10(max)).toInt()
        }
    } }
    val groups by remember { derivedStateOf {
        viewModel.stats.groupBy { it.runN }.map { (runN, stats) ->
            DefaultVerticalBarPlotGroupedPointEntry(
                runN,
                stats.map {
                    DefaultVerticalBarPosition(0f, it.time.inWholeMicroseconds.toFloat())
                }
            )
        }
    } }
    val runNs by remember { derivedStateOf {
        viewModel.stats.groupBy { it.runN }.keys.sorted()
    } }

    ChartLayout(modifier = Modifier.weight(1f)) {
        XYGraph(
            xAxisModel = CategoryAxisModel(runNs),
            yAxisModel = LogAxisModel(yRange),
            xAxisLabels = { "#$it" },
            yAxisLabels = { it.toDouble().microseconds.formatted() },
            xAxisTitle = "Серии"
        ) {
            GroupedVerticalBarPlot(groups, maxBarGroupWidth = 0.6f)
        }
    }
}

context(RowScope)
@Composable
private fun TableSegment(viewModel: StructuresComparisonViewModel) {
    var tableVisible by remember { mutableStateOf(true) }
    IconButton(
        onClick = { tableVisible = !tableVisible }
    ) {
        Icon(Icons.Default.TableRows, "Run")
    }

    if (!tableVisible) return

    val stats by remember { derivedStateOf { viewModel.stats.sortedBy { it.runN } } }

    Table(
        rowCount = stats.size + 1, // stats plus header
        columnCount = 3,
        modifier = Modifier.wrapContentWidth()
    ) { y, x ->
        Box(modifier = Modifier.padding(8.dp)) {
            if (y == 0) {
                when (x) {
                    0 -> Text("Метод", fontWeight = FontWeight.Bold)
                    1 -> Text("Серия", fontWeight = FontWeight.Bold)
                    2 -> Text("Время", fontWeight = FontWeight.Bold)
                }
                return@Table
            }

            val i = y - 1
            val stat = stats[i]

            when (x) {
                0 -> Text(stat.name)
                1 -> Text("#${stat.runN}")
                2 -> Text(stat.time.formatted())
            }
        }
    }
}

@Composable
private fun FunctionsSegment(regenerateArray: () -> Unit, rerunStats: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = regenerateArray
        ) {
            Icon(Icons.Default.Dataset, "Dataset")
            Spacer(modifier = Modifier.width(12.dp))
            Text("Сгенерировать массив")
        }
        TextButton(
            onClick = rerunStats
        ) {
            Icon(Icons.Default.PlayArrow, "Run")
            Spacer(modifier = Modifier.width(12.dp))
            Text("Запустить сортировку")
        }
    }
}

private class StructuresComparisonViewModel {
    private var array: IntArray by mutableStateOf(intArrayOf())
    var stats: List<MethodStats> by mutableStateOf(emptyList())
        private set
    private var runN by mutableStateOf(0)

    companion object {
        private const val ARRAY_SIZE = 10_000
    }

    fun regenerateArray() {
        array = IntArray(ARRAY_SIZE) { Random.nextInt() }
    }

    fun rerunStats() {
        if (array.isEmpty()) {
            regenerateArray()
        }

        runN += 1
        val runN = runN
        thread {
            val currentArray = array
            for (method in SortingMethod.entries) {
                val time = measureTime { method.invoke(currentArray).count() }
                GlobalScope.launch(Dispatchers.IO) {
                    stats += MethodStats(runN, method.displayName, time)
                }
            }
        }
    }

    data class MethodStats(val runN: Int, val name: String, val time: Duration)
}

private enum class SortingMethod(val displayName: String): (IntArray) -> Iterable<Int> {
    Avl("Сортировка АВЛ деревом") {
        override fun invoke(array: IntArray): Iterable<Int> = sortUsingAvlTree(array)
    },
    Insertion("Сортировка прямым включением") {
        override fun invoke(array: IntArray): Iterable<Int> = sortByDirectInclusion(array)
    };

    abstract override operator fun invoke(array: IntArray): Iterable<Int>
}

private fun Duration.formatted(): String {
    return when {
        inWholeSeconds > 0 -> "$inWholeSeconds s"
        inWholeMilliseconds > 0 -> "$inWholeMilliseconds ms"
        inWholeMicroseconds > 0 -> "$inWholeMicroseconds μs"
        else -> "$inWholeNanoseconds ns"
    }
}
