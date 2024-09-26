import androidx.compose.ui.graphics.toComposeImageBitmap
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.model.Compass.NORTH
import guru.nidi.graphviz.toGraphviz
import tree.BinaryTree
import tree.forEachLeftToRight
import tree.forEachTopToBottom
import tree.leftToRightIterator

fun <T> BinaryTree<T>?.renderToImageBitmap(transform: (T) -> String) = graph(directed = true) {
    this@renderToImageBitmap?.forEachTopToBottom {
        -transform(it.state)
        it.left?.let { left ->
            (transform(it.state) - transform(left.state))["color" eq "blue", "label" eq "L"]
        }
        it.right?.let { right ->
            (transform(it.state) - transform(right.state))["color" eq "red", "label" eq "R"]
        }
    }
}.toGraphviz().render(Format.PNG).toImage().toComposeImageBitmap()

fun <T> BinaryTree<T>?.linedRenderToImageBitmap(transform: (T) -> String) = graph(directed = true) {

    graph[Rank.dir(LEFT_TO_RIGHT)]

    this@linedRenderToImageBitmap.leftToRightIterator().asSequence().zipWithNext().forEach { (a, b) ->
        (transform(a.state) - transform(b.state))["style" eq "invis"]
    }

    this@linedRenderToImageBitmap?.forEachLeftToRight {
        it.left?.let { left ->
            (transform(left.state) - transform(it.state))["color" eq "blue", "dir" eq "back", "label" eq "L"]
        }
        it.right?.let { right ->
            (transform(it.state) / NORTH - transform(right.state) / NORTH)["color" eq "red", "label" eq "R"]
        }
    }
}.toGraphviz().render(Format.PNG).toImage().toComposeImageBitmap()
